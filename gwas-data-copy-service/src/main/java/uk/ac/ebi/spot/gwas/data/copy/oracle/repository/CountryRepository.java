package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.data.copy.model.Country;


/**
 * Created by emma on 19/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing Country entity objects
 */

public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByCountryName(String countryName);
    Country findByCountryNameIgnoreCase(String countryName);
}
