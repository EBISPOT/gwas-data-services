package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.RiskAllele;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.RiskAlleleRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.RiskAlleleService;

import java.util.List;

@Service
public class RiskAlleleServiceImpl implements RiskAlleleService {

    RiskAlleleRepository riskAlleleRepository;

    public RiskAlleleServiceImpl(RiskAlleleRepository riskAlleleRepository) {
        this.riskAlleleRepository = riskAlleleRepository;
    }

    public RiskAllele saveRiskAllele(RiskAllele riskAllele) {
       return riskAlleleRepository.save(riskAllele);
    }
}
