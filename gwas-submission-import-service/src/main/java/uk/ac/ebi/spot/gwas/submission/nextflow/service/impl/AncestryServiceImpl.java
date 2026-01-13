package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.AncestryRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AncestryService;

@Service
public class AncestryServiceImpl implements AncestryService {

    AncestryRepository ancestryRepository;

    public AncestryServiceImpl(AncestryRepository ancestryRepository) {
        this.ancestryRepository = ancestryRepository;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteAncestries(Long studyId) {
        ancestryRepository.deleteAll(ancestryRepository.findByStudyId(studyId));
    }



}
