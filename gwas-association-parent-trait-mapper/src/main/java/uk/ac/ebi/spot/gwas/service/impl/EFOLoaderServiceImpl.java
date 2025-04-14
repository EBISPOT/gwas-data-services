package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.config.Config;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.EFOTraitRepository;
import uk.ac.ebi.spot.gwas.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EFOLoaderServiceImpl implements EFOLoaderService {

    AssociationService associationService;

    AssociationRepository associationRepository;

    ParentEFOUpdateService parentEFOUpdateService;

    StudyService studyService;

    StudyRepository studyRepository;

    TraitMapperJobSubmitterService traitMapperJobSubmitterService;

    Config config;


    EfoTraitRetrieveService efoTraitRetrieveService;

    public EFOLoaderServiceImpl(AssociationService associationService,
                                AssociationRepository associationRepository,
                                ParentEFOUpdateService parentEFOUpdateService,
                                StudyService studyService,
                                StudyRepository studyRepository,
                                TraitMapperJobSubmitterService traitMapperJobSubmitterService,
                                Config config,
                                EfoTraitRetrieveService efoTraitRetrieveService) {
        this.associationService = associationService;
        this.associationRepository = associationRepository;
        this.parentEFOUpdateService = parentEFOUpdateService;
        this.studyService = studyService;
        this.studyRepository = studyRepository;
        this.traitMapperJobSubmitterService = traitMapperJobSubmitterService;
        this.config = config;
        this.efoTraitRetrieveService = efoTraitRetrieveService;
    }

    @Transactional
    public void loadAssociationsWithParentEfo(List<EfoTrait> parenEfos) {
       for (EfoTrait parentEfo : parenEfos) {
           updateAssociationsParentEfo(parentEfo, parentEfo);
           for (EfoTrait childEfo : parentEfo.getParentChildEfoTraits()) {
               updateAssociationsParentEfo(parentEfo, childEfo);
           }
       }
   }

    @Transactional
    public void runDataForLargeEfo(List<EfoTrait> parenEfos) {
        for (EfoTrait parentEfo : parenEfos) {
            updateAssociationsParentEfo(parentEfo, parentEfo);
            updateStudiesParentEfo(parentEfo, parentEfo);
            int count = 0;
            List<EfoTrait> childEfos = new ArrayList<>(parentEfo.getParentChildEfoTraits());
            List<String> shortForms = childEfos.stream().map(EfoTrait::getShortForm).collect(Collectors.toList());
            for(List<String> partEfos : ListUtils.partition(shortForms, 1000)) {
                traitMapperJobSubmitterService.executePipeline(partEfos, config.getSlurmLogsLocation(), config.getSlurmLogsLocation(),
                        "executor-"+count, parentEfo.getShortForm());
                count++;
            }
        }
    }


    @Transactional
    public void loadAssociationsForChildEfos(List<String> childEfos, String parentEfo) {
        EfoTrait parentEfoTrait = efoTraitRetrieveService.findByShortForm(parentEfo);
        efoTraitRetrieveService.findByShortForms(childEfos).forEach(childEfo ->
                updateAssociationsParentEfo(parentEfoTrait, childEfo)
        );
    }


    @Transactional
    public  void loadStudiesWithParentEfo(List<EfoTrait> parenEfos) {
        for (EfoTrait parentEfo : parenEfos) {
            updateStudiesParentEfo(parentEfo, parentEfo);
            for (EfoTrait childEfo : parentEfo.getParentChildEfoTraits()) {
                updateStudiesParentEfo(parentEfo, childEfo);
            }
        }
    }


    @Transactional
    public void loadStudiesForChildEfos(List<String> childEfos, String parentEfo) {
        EfoTrait parentEfoTrait = efoTraitRetrieveService.findByShortForm(parentEfo);
        efoTraitRetrieveService.findByShortForms(childEfos).forEach(childEfo ->
                updateStudiesParentEfo(parentEfoTrait, childEfo)
        );
    }

    private void updateStudiesParentEfo(EfoTrait parentEfo, EfoTrait childEfo) {
        Long count = studyRepository.countStudiesByEfoTraitsShortForm(childEfo.getShortForm());
        long bucket = (count / 100);
        for (int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i, 100);
            studyService.findStudiesByShortForm(childEfo.getShortForm(), pageable)
                    .forEach(study -> parentEFOUpdateService.saveStudyWithParentEfo(study, parentEfo));
        }
    }


    private void updateAssociationsParentEfo(EfoTrait parentEfo, EfoTrait childEfo) {
        Long count = associationRepository.countAssociationsByEfoTraitsShortForm(childEfo.getShortForm());
        long bucket = (count / 100);
        for (int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i, 100);
            associationService.findAssociationByShortForm(childEfo.getShortForm(), pageable).
                    forEach(association ->  parentEFOUpdateService.saveAssociationWithParentEfo(association, parentEfo));
        }
    }

}
