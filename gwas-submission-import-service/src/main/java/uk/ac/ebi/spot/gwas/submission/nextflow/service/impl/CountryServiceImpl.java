package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Country;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.CountryRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CountryService;

@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

    CountryRepository countryRepository;

    public CountryServiceImpl(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public  Country findByCountryOfRecruitement(String country) {
        return countryRepository.findByCountryNameIgnoreCase(country).orElse(null);
   }
}
