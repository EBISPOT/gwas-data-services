package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.service.StudyService;

@Service
public class StudyServiceImpl implements StudyService {

    StudyRepository studyRepository;


    @Autowired
    public StudyServiceImpl(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }


    @Transactional(readOnly = true)
    public Page<Study> findStudiesByShortForm(String shortForm, Pageable pageable) {
        return studyRepository.findByEfoTraitsShortForm(shortForm, pageable);
    }

}
