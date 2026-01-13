package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.submission.service.AssociationService;

@Service
public class AssociationServiceImpl implements AssociationService {


    AssociationRepository associationRepository;

    public AssociationServiceImpl(AssociationRepository associationRepository) {
        this.associationRepository = associationRepository;
    }

    public Page<Association> findBySubmissionId(String submissionId, Pageable pageable) {
        return associationRepository.findBySubmissionId(submissionId, pageable);
    }

    public Long findBySubmissionId(String submissionId) {
        return associationRepository.findBySubmissionId(submissionId).count();
    }

    public Boolean checkAssociationExists(String submissionId) {
        Long totalAsscns = findBySubmissionId(submissionId);
        Long bucketAsscns = totalAsscns/1000;
        for(int i = 0; i <= bucketAsscns; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            Page<Association> asscns = findBySubmissionId(submissionId, pageable);
            for(Association asscn : asscns) {
                if(asscn.getStudyTag() != null ) {
                    return true;
                }
            }
        }
        return false;
    }

}
