package uk.ac.ebi.spot.gwas.rabbitmq.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.ac.ebi.spot.gwas.model.Platform;

import java.util.List;

/**
 * Created by dwelter on 10/03/16.
 */

public interface PlatformRepository extends JpaRepository<Platform, Long>{

    Platform findByManufacturer(String manufacturer);

//    List<Platform> findByStudyId(Long studyId);

    List<Platform> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);



}
