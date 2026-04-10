package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Country;

public interface CountryService {

    Country findByCountryOfRecruitement(String country);
}
