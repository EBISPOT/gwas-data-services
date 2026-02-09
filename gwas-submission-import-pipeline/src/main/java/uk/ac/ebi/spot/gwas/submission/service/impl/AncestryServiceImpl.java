package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.AncestryRepository;
import uk.ac.ebi.spot.gwas.submission.service.AncestryService;

@Service
public class AncestryServiceImpl implements AncestryService {

    AncestryRepository ancestryRepository;


    public AncestryServiceImpl(AncestryRepository ancestryRepository) {
        this.ancestryRepository = ancestryRepository;
    }

    public void deleteAncestries(Long studyId) {
        ancestryRepository.deleteAll(ancestryRepository.findByStudyId(studyId));
    }

}
