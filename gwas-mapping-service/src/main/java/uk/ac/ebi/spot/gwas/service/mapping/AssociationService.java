package uk.ac.ebi.spot.gwas.service.mapping;

import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.dto.MappingDto;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.projection.MappingProjection;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.GeneRepository;
import uk.ac.ebi.spot.gwas.repository.LocusRepository;
import uk.ac.ebi.spot.gwas.repository.SingleNucleotidePolymorphismRepository;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AssociationService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AssociationRepository associationRepository;
    @Autowired
    private LocusRepository locusRepository;
    @Autowired
    private GeneRepository generepository;
    @Autowired
    private SingleNucleotidePolymorphismRepository snpRepo;

    public Page<Association> getAssociationPageInfo(int start, int size) {
        Pageable pageable = PageRequest.of(start, size);
        return associationRepository.findAll(pageable);
    }

    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<MappingDto> getAssociationsBatch(int start, int size, String mapType) {

        log.info("Get data part {} : {} starts", start, size);
        Pageable pageable = PageRequest.of(start, size);

        List<Long> associationIds = new ArrayList<>();
        List<MappingProjection> associations;
        if (mapType.equals(CommandUtil.MAPPING_OPT)) {
            associations = associationRepository.findUnmappedWithFewAttributes(pageable);
        } else {
            associations = associationRepository.findAllWithFewAttributes(pageable);
        }

        associations.forEach(association -> associationIds.add(association.getAssociationId()));

        List<MappingProjection> authorReportedGeneNames = generepository.findUsingAssociationIds(associationIds);
        List<String> reportedGenes = authorReportedGeneNames.stream()
                .map(MappingProjection::getGeneName).distinct().collect(Collectors.toList());

        List<Long> locusIds = new ArrayList<>();
        List<MappingProjection> associationLoci = locusRepository.findUsingAssociationIds(associationIds);
        associationLoci.forEach(locus -> locusIds.add(locus.getLocusId()));

        Collection<MappingProjection> snpsLinkedToLocus = new ArrayList<>();
        ListUtils.partition(locusIds, 1000).forEach(listPart -> snpsLinkedToLocus.addAll(snpRepo.findUsingRiskAllelesLociIds(listPart)));

        List<String> snpRsIds = snpsLinkedToLocus.stream()
                .map(MappingProjection::getSnpRsid).distinct().collect(Collectors.toList());

        log.info("Get data part {} : {} ends", start, size);

        MappingDto mappingDto = MappingDto.builder()
                .snpRsIds(snpRsIds)
                .reportedGenes(reportedGenes).build();

        return CompletableFuture.completedFuture(mappingDto);
    }


    @Async("asyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<List<Association>> getAssociations(int start, int size, String mapType) {
        log.info("Get association {} : {} starts", start, size);
        Pageable pageable = PageRequest.of(start, size);

        List<Association> associations;
        if (mapType.equals(CommandUtil.MAPPING_OPT)) {
            associations = associationRepository.findBylastMappingDateIsNull(pageable);
        } else {
            Page<Association> associationPages = associationRepository.findAll(pageable);
            associations = associationPages.getContent();
        }

        log.info("Get association {} : {} ends", start, size);
        return CompletableFuture.completedFuture(associations);
    }

    public Association assocx() {
        return associationRepository.findById(87272585L).get();
    }

}

