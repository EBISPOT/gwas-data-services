package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.deposition.domain.Association;
import uk.ac.ebi.spot.gwas.submission.mongo.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.submission.oracle.repository.AssociationOracleRepository;
import uk.ac.ebi.spot.gwas.submission.service.AssociationService;

@Service
public class AssociationServiceImpl implements AssociationService {


    AssociationRepository associationRepository;

    AssociationOracleRepository associationOracleRepository;

    public AssociationServiceImpl(AssociationRepository associationRepository,
                                  AssociationOracleRepository associationOracleRepository) {
        this.associationRepository = associationRepository;
        this.associationOracleRepository = associationOracleRepository;
    }

    public Page<Association> findBySubmissionId(String submissionId, Pageable pageable) {
        return associationRepository.findBySubmissionId(submissionId, pageable);
    }

    public Long findBySubmissionId(String submissionId) {
        return associationRepository.countAssociationsBySubmissionId(submissionId);
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


    @Transactional(propagation = Propagation.SUPPORTS)
    public void deleteAssociation(Long studyId) {
        Long totalAsscns = associationOracleRepository.countByStudyId(studyId);
        Long bucket = totalAsscns/1000;
        for(int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i, 1000);
            //associationRepository.deleteAll(associationRepository.findByStudyId(studyId, pageable));
                    associationOracleRepository.deleteAll(associationOracleRepository.findByStudyId(studyId, pageable));
        }
    }

}
