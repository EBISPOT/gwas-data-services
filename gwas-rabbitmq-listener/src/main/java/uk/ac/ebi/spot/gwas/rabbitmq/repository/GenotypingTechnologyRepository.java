package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.GenotypingTechnology;

import java.util.List;

/**
 * Created by dwelter on 22/06/17.
 */


public interface GenotypingTechnologyRepository extends JpaRepository<GenotypingTechnology, Long> {

    GenotypingTechnology findByGenotypingTechnology(String genotypingTechnology);

//    List<GenotypingTechnology> findByStudyId(Long studyId);

    List<GenotypingTechnology> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);
}
