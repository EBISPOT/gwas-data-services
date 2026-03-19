package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.RiskAllele;

public interface RiskAlleleService {

    RiskAllele saveRiskAllele(RiskAllele riskAllele);
}
