package uk.ac.ebi.spot.gwas.submission.nextflow.service;


import uk.ac.ebi.spot.gwas.model.Curator;

public interface CuratorService {

    Curator findByEmail(String email);
}
