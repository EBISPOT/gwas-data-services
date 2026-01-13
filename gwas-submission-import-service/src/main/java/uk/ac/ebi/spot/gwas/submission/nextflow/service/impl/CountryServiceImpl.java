package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Country;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CountryRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CountryService;

@Service
public class CountryServiceImpl implements CountryService {

    CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public  Country findByCountryOfRecruitement(String country) {
        return countryRepository.findByCountryNameIgnoreCase(country).orElse(null);
   }
}
