package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;

public interface SnpService {

    SingleNucleotidePolymorphism saveSnp(SingleNucleotidePolymorphism singleNucleotidePolymorphism);
}
