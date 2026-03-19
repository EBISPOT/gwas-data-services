package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.EfoTrait;

public interface EfoTraitService {

    EfoTrait findByShortForm(String shortForm);


    uk.ac.ebi.spot.gwas.deposition.domain.EfoTrait findByMongoId(String mongoId);

}
