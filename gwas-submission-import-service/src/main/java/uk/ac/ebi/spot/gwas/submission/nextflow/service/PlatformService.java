package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Platform;

public interface PlatformService {

   Platform findByManufacturer(String manufacturer);
}
