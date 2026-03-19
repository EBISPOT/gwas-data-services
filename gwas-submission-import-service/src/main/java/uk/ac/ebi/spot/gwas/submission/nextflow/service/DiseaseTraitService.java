package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.DiseaseTrait;

public interface DiseaseTraitService {

    DiseaseTrait getMongoDiseaseTrait(String traitId);

    uk.ac.ebi.spot.gwas.model.DiseaseTrait getDiseaseTrait(String trait);
}
