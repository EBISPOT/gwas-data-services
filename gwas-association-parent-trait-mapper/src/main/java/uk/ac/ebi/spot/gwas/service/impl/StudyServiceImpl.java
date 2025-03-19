package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.service.StudyService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyServiceImpl implements StudyService {

    StudyRepository studyRepository;

    @Autowired
    public StudyServiceImpl(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }


    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public  void loadStudiesWithParentEfo(EfoTrait parenEfo) {
        for (EfoTrait childEfo : parenEfo.getParentChildEfoTraits()) {
            Long count = studyRepository.countStudiesByEfoTraitsShortForm(childEfo.getShortForm());
            long bucket = (count / 1000);
            for (int i = 0; i <= bucket; i++) {
                Pageable pageable = PageRequest.of(i, 100);
                studyRepository.findByEfoTraitsShortForm(childEfo.getShortForm(), pageable)
                        .forEach(study -> {
                           List<String> shortForms = study.getParentStudyEfoTraits().stream()
                                            .map(EfoTrait::getShortForm)
                                                    .collect(Collectors.toList());
                            if(!shortForms.contains(parenEfo.getShortForm())) {
                                study.getParentStudyEfoTraits().add(parenEfo);
                            }
                            studyRepository.save(study);
                        });
            }
        }
    }
}
