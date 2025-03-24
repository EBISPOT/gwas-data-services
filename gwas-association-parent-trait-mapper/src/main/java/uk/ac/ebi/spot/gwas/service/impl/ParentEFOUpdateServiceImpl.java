package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.service.ParentEFOUpdateService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParentEFOUpdateServiceImpl implements ParentEFOUpdateService {


    AssociationRepository associationRepository;

    StudyRepository studyRepository;

    public ParentEFOUpdateServiceImpl(AssociationRepository associationRepository,
                                      StudyRepository studyRepository ) {
        this.associationRepository = associationRepository;
        this.studyRepository = studyRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveAssociationWithParentEfo(Association association, EfoTrait parentEfo) {
        List<String> shortForms = association.getParentEfoTraits().stream()
                .map(EfoTrait::getShortForm)
                .collect(Collectors.toList());
        if(!shortForms.contains(parentEfo.getShortForm())) {
            association.getParentEfoTraits().add(parentEfo);
        }
        associationRepository.save(association);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saveStudyWithParentEfo(Study study, EfoTrait parentEfo) {
        List<String> shortForms = study.getParentStudyEfoTraits().stream()
                .map(EfoTrait::getShortForm)
                .collect(Collectors.toList());
        if (!shortForms.contains(parentEfo.getShortForm())) {
            study.getParentStudyEfoTraits().add(parentEfo);
        }
        studyRepository.save(study);
    }
}
