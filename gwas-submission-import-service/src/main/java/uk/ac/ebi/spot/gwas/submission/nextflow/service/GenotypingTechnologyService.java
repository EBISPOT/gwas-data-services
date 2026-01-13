package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.GenotypingTechnology;

public interface GenotypingTechnologyService {

    GenotypingTechnology findByGenotypingTechnology(String genotypingTechnology);
}
