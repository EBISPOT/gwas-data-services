package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.UnpublishedStudy;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.UnpublishedStudyRepository;
import uk.ac.ebi.spot.gwas.submission.service.UnpublishedStudiesRetrieveService;

@Service
public class UnpublishedStudiesRetrieveServiceImpl implements UnpublishedStudiesRetrieveService {

    UnpublishedStudyRepository unpublishedStudyRepository;

    public UnpublishedStudiesRetrieveServiceImpl(UnpublishedStudyRepository unpublishedStudyRepository) {
        this.unpublishedStudyRepository = unpublishedStudyRepository;
    }

    @Transactional(readOnly = true)
    public UnpublishedStudy findByAccession(String accessionId) {
        return unpublishedStudyRepository.findByAccession(accessionId).orElse(null);
    }
}
