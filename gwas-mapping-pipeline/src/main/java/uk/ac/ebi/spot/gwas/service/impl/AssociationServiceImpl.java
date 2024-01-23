package uk.ac.ebi.spot.gwas.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.LocationRepository;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.service.AssociationService;
import uk.ac.ebi.spot.gwas.service.FileHandlerService;
import uk.ac.ebi.spot.gwas.service.MappingJobSubmitterService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssociationServiceImpl implements AssociationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Logger merrlog = LoggerFactory.getLogger("mappingerrorlogger");

    @Autowired
    AssociationRepository associationRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    MappingJobSubmitterService mappingJobSubmitterService;

    @Autowired
    FileHandlerService fileHandlerService;
    @Autowired
    SingleNucleotidePolymorphismRepository singleNucleotidePolymorphismRepository;
    @Transactional(readOnly = true)
    public Set<Association> getAssociationBasedOnRsId(String rsId) {
        log.info("Rsid is ->"+rsId);
        List<Association> associations = singleNucleotidePolymorphismRepository.findAssociationsUsingRsId(rsId);
        Set<Association> associationSet = new HashSet<>(associations);
        associationSet.forEach(association -> log.info("Association Id ->"+association.getId()));
        return associationSet;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateMappingDetails(List<Long> ids) {

       for(List<Long> partIds :  ListUtils.partition(ids, 100)) {
           List<Association> partAsscns = null;
            try {
                partAsscns = associationRepository.findAllById(partIds);
            }catch(Exception ex){
                log.error("Exception in pulling associations"+ex.getMessage(),ex);
            }
           partAsscns.forEach(association -> {
               try {
                   association.setLastMappingDate(null);
                   associationRepository.save(association);
               }catch(Exception ex){
                   log.error("error on updating mapping date of association->"+association.getId());
                   log.error("Exception in pulling associations"+ex.getMessage(),ex);
               }

           });
       }
    }


    public void fullRemapping(String outputDir, String errorDir) {
        Long count = associationRepository.count();
        log.info("Full remapping for {} associations", count);
        int cnt = count.intValue();
        int batchsize = 1000;
        int noOfPages = (cnt / batchsize);
        for(int i = 0; i <= noOfPages; i++ ){
            log.info("Executor pool {} running",i);
            Pageable pageable = PageRequest.of(i, batchsize);
            List<Long> asscns = associationRepository.findAll(pageable)
                    .stream().map(Association::getId)
                    .collect(Collectors.toList());
            updateMappingDetails(asscns);
            mappingJobSubmitterService.executePipeline(asscns, outputDir, errorDir, "executor-"+i);
        }
    }


    public void findAssociationMappingError() {
      List<String> assncIds =  fileHandlerService.readFileInput("/Users/sajo/Documents/proj_files/Association-error-mapping.tsv");
        assncIds.forEach((asscnId) -> {
            Optional<Association> optionalAssociation = associationRepository.findById(Long.valueOf(asscnId));
            String formatMappingError = asscnId;
            if (optionalAssociation.isPresent()) {
                boolean hasMapping = false;
                Association association = optionalAssociation.get();
                Collection<Locus> loci = association.getLoci();
                int count = 0;
                for (Locus locus : loci) {
                    Collection<SingleNucleotidePolymorphism> snpLinkedtoLocus = singleNucleotidePolymorphismRepository.findByRiskAllelesLociId(locus.getId());
                    for (SingleNucleotidePolymorphism snp : snpLinkedtoLocus) {
                        String rsId = snp.getRsId();

                        if(count > 0) {
                            formatMappingError = formatMappingError + "," + rsId;
                        }else {
                            formatMappingError = formatMappingError + " " + rsId;
                        }

                        List<MappingProjection>  mappingProjections = locationRepository.getLocationDetails(snp.getId());
                        if(mappingProjections != null && !mappingProjections.isEmpty() ){
                            mappingProjections.forEach(mappingProjection ->  {
                                //log.info("Location name" + mappingProjection.getChromosomeName());
                                //log.info("Location Pos" + mappingProjection.getChromosomePosition());
                            });
                             hasMapping = true;
                        }
                        count++;
                    }
                }
                if(hasMapping) {
                    formatMappingError = formatMappingError + " " + "Yes";
                }else {
                    formatMappingError = formatMappingError + " " + "No";
                }
                merrlog.info(formatMappingError);
            }

        });
    }


    public void scheduledRemapping(String outputDir, String errorDir) {
        //Long count = associationRepository.countByLastMappingDateIsNull();

        /*int cnt = count.intValue();
        int batchsize = 1000;
        int noOfPages = (cnt / batchsize);
        for (int i = 0; i <= noOfPages; i++) {
            log.info("Executor pool {} running", i);
            Pageable pageable = PageRequest.of(i, batchsize);
            List<Long> asscns = associationRepository.findByLastMappingDateIsNullOrderByLastUpdateDateDesc(pageable)
                    .stream().map(Association::getId)
                    .collect(Collectors.toList());
            //updateMappingDetails(asscns);
            mappingJobSubmitterService.executePipeline(asscns, outputDir, errorDir, "executor-" + i);
        }*/
        int pool = 0;
        Long count = countAssociationsWithMappingDateNull();
        log.info("Scheduled remapping for {} associations", count);
        while(count != 0){
            log.info("Executor pool {} running", pool);
            Pageable pageable = PageRequest.of(0, 1000);
            List<Long> asscns = associationRepository.findByLastMappingDateIsNullOrderByLastUpdateDateDesc(pageable)
                    .stream().map(Association::getId)
                    .collect(Collectors.toList());
            //updateMappingDetails(asscns);
            mappingJobSubmitterService.executePipeline(asscns, outputDir, errorDir, "executor-" + pool);
            count = countAssociationsWithMappingDateNull();
            pool++;
        }

    }

    private Long countAssociationsWithMappingDateNull(){
        return associationRepository.countByLastMappingDateIsNull();
    }

}
