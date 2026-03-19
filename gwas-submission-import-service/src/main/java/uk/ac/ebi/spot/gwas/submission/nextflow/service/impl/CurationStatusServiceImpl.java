package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.CurationStatus;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CurationStatusRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CurationStatusService;

@Service
public class CurationStatusServiceImpl implements CurationStatusService {

    CurationStatusRepository curationStatusRepository;

    public CurationStatusServiceImpl(CurationStatusRepository curationStatusRepository) {
        this.curationStatusRepository = curationStatusRepository;
    }

    @Transactional(readOnly = true)
    public CurationStatus findByStatus(String curationStatus) {
        return curationStatusRepository.findByStatus(curationStatus).orElse(null);
    }

}
