package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.service.AssociationService;
import uk.ac.ebi.spot.gwas.service.EFOLoaderService;
import uk.ac.ebi.spot.gwas.service.ParentEFOUpdateService;
import uk.ac.ebi.spot.gwas.service.StudyService;

import java.util.List;

@Service
@Slf4j
public class EFOLoaderServiceImpl implements EFOLoaderService {

    AssociationService associationService;

    AssociationRepository associationRepository;

    ParentEFOUpdateService parentEFOUpdateService;

    StudyService studyService;

    StudyRepository studyRepository;

    public EFOLoaderServiceImpl(AssociationService associationService,
                                AssociationRepository associationRepository,
                                ParentEFOUpdateService parentEFOUpdateService,
                                StudyService studyService,
                                StudyRepository studyRepository) {
        this.associationService = associationService;
        this.associationRepository = associationRepository;
        this.parentEFOUpdateService = parentEFOUpdateService;
        this.studyService = studyService;
        this.studyRepository = studyRepository;
    }

    @Transactional
    public void loadAssociationsWithParentEfo(List<EfoTrait> parenEfos) {
       for (EfoTrait parentEfo : parenEfos) {
           //log.info("Inside loadAssociationsWithParentEfo");
           //log.info("parentEfo : {}", parentEfo);
           for (EfoTrait childEfo : parentEfo.getParentChildEfoTraits()) {
               //log.info("childEfo : {}", childEfo);
               Long count = associationRepository.countAssociationsByEfoTraitsShortForm(childEfo.getShortForm());
               //log.info(" count childEfo : {},{}", childEfo, count);
               long bucket = (count / 100);
               for (int i = 0; i <= bucket; i++) {
                   Pageable pageable = PageRequest.of(i, 100);
                   associationService.findAssociationByShortForm(childEfo.getShortForm(), pageable).
                           forEach(association -> parentEFOUpdateService.saveAssociationWithParentEfo(association, parentEfo));
               }
           }
       }
   }


    @Transactional
    public  void loadStudiesWithParentEfo(List<EfoTrait> parenEfos) {
        for (EfoTrait parentEfo : parenEfos) {
            for (EfoTrait childEfo : parentEfo.getParentChildEfoTraits()) {
                Long count = studyRepository.countStudiesByEfoTraitsShortForm(childEfo.getShortForm());
                long bucket = (count / 100);
                for (int i = 0; i <= bucket; i++) {
                    Pageable pageable = PageRequest.of(i, 100);
                    studyService.findStudiesByShortForm(childEfo.getShortForm(), pageable)
                            .forEach(study -> parentEFOUpdateService.saveStudyWithParentEfo(study, parentEfo));
                }
            }
        }
    }
}
