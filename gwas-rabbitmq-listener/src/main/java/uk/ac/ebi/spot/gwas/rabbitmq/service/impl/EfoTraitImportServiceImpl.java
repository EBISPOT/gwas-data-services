package uk.ac.ebi.spot.gwas.rabbitmq.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.EfoTraitRabbitMessage;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.rabbitmq.dto.EFOTraitRabbitMessageAssembler;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.EfoTraitRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.rabbitmq.service.EfoTraitImportService;

import java.util.Optional;

@Slf4j
@Service
public class EfoTraitImportServiceImpl implements EfoTraitImportService {

    @Autowired
    EFOTraitRabbitMessageAssembler efoTraitRabbitMessageAssembler ;

    @Autowired
    EfoTraitRepository efoTraitRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    AssociationRepository associationRepository;



    @Transactional(propagation = Propagation.REQUIRED)
    public void importEfoTrait(EfoTraitRabbitMessage efoTraitRabbitMessage) {

        if(efoTraitRabbitMessage.getOperation().equalsIgnoreCase("insert")) {
            EfoTrait efoTrait = efoTraitRabbitMessageAssembler.disassemble(efoTraitRabbitMessage);
            saveEfoTrait(efoTrait);
        }else if(efoTraitRabbitMessage.getOperation().equalsIgnoreCase("update")) {
            EfoTrait efoTrait =  getEfoTrait(efoTraitRabbitMessage.getMongoSeqId());
            if(efoTrait != null) {
                efoTrait.setTrait(Optional.ofNullable(efoTraitRabbitMessage.getTrait()).orElse(efoTrait.getTrait()));
                efoTrait.setShortForm(Optional.ofNullable(efoTraitRabbitMessage.getShortForm()).orElse(efoTrait.getShortForm()));
                efoTrait.setUri(Optional.ofNullable(efoTraitRabbitMessage.getUri()).orElse(efoTrait.getUri()));
                saveEfoTrait(efoTrait);
            }
        }else if(efoTraitRabbitMessage.getOperation().equalsIgnoreCase("delete")) {
            try {
                removeStudyEFOMapping(efoTraitRabbitMessage.getMongoSeqId());
            }catch(Exception ex){
                log.error("Exception with EFO Trait SeqId -: {} ",efoTraitRabbitMessage.getMongoSeqId());
                log.error("Exception in syncEFOTraits Delete ()"+ex.getMessage(),ex );

            }
        }

    }

    void saveEfoTrait(EfoTrait efoTrait) {
        efoTraitRepository.save(efoTrait);
    }

    void updateEfoTrait(EfoTrait efoTrait) {
        efoTraitRepository.save(efoTrait);
    }

    EfoTrait getEfoTrait(String mongoSeqId) {
        return efoTraitRepository.findByMongoSeqId(mongoSeqId).orElse(null);
    }

    void deleteEfoTrait(EfoTrait efoTrait) {
        efoTraitRepository.delete(efoTrait);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeStudyEFOMapping( String seqId) {

        log.info("Mongo Ids to be deleted {}", seqId);
        efoTraitRepository.findByMongoSeqId(seqId).ifPresent(efoTrait -> {

            log.info("Trait which is deleted is {}", efoTrait.getShortForm());
            log.info("Trait ID which is deleted is {}", efoTrait.getId());
            log.info("Size of the study list" +studyRepository.findByEfoTraitsId(efoTrait.getId()).size());
            studyRepository.findByEfoTraitsId(efoTrait.getId()).forEach(study -> {
                //study.setEfoTraits(null);
                log.info("Calling removeStudyEFOMappings()");
                study.getEfoTraits().remove(efoTrait);
                studyRepository.save(study);
            });

            studyRepository.findByParentStudyEfoTraitsId(efoTrait.getId()).forEach(study -> {
                log.info("Calling removeParentStudyEFOMappings()");
                study.getParentStudyEfoTraits().remove(efoTrait);
                studyRepository.save(study);
            });
            studyRepository.findByMappedBackgroundTraitsId(efoTrait.getId()).forEach(study -> {
                //study.setEfoTraits(null);
                log.info("Calling removeStudyBGEFOMappings()");
                study.getMappedBackgroundTraits().remove(efoTrait);
                studyRepository.save(study);
            });


            associationRepository.findByEfoTraitsId(efoTrait.getId()).forEach(asscn -> {
                //asscn.setEfoTraits(null);
                log.info("Calling removeAsscnEFOMappings()");
                asscn.getEfoTraits().remove(efoTrait);
                associationRepository.save(asscn);
            });
            associationRepository.findByParentEfoTraitsId(efoTrait.getId()).forEach(asscn -> {
                log.info("Calling removeParentAsscnEFOMappings()");
                asscn.getParentEfoTraits().remove(efoTrait);
                associationRepository.save(asscn);
            });
            associationRepository.findByBkgEfoTraitsId(efoTrait.getId()).forEach(asscn -> {
                //asscn.setEfoTraits(null);
                log.info("Calling removeAsscnBGEFOMappings()");
                asscn.getBkgEfoTraits().remove(efoTrait);
                associationRepository.save(asscn);
            });

            efoTrait.getParentChildEfoTraits().remove(efoTrait);
            deleteEfoTrait(efoTrait);
            //efoTraitService.delete(efoTrait);
        });
    }


}
