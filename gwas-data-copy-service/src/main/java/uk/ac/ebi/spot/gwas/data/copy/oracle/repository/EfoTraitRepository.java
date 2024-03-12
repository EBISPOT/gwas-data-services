package uk.ac.ebi.spot.gwas.data.copy.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import uk.ac.ebi.spot.gwas.data.copy.model.EfoTrait;

import java.util.List;

/**
 * Created by emma on 04/12/14.
 *
 * @author emma
 *         <p>
 *         Repository accessing EfoTrait entity object
 */


public interface EfoTraitRepository extends JpaRepository<EfoTrait, Long> {




    List<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);

    List<EfoTrait> findByStudiesBkgIdAndStudiesBkgHousekeepingCatalogPublishDateIsNotNullAndStudiesBkgHousekeepingCatalogUnpublishDateIsNull(
            Long studyId);


    List<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Sort sort,
            Long studyId);

    Page<EfoTrait> findByStudiesIdAndStudiesHousekeepingCatalogPublishDateIsNotNullAndStudiesHousekeepingCatalogUnpublishDateIsNull(
            Pageable pageable,
            Long studyId);


    List<EfoTrait> findByAssociationsId(Long associationId);


    @Query("select e.trait from EfoTrait e join e.associations a where a.id = :associationId")
    List<String> findTraitNamesByAssociationsId(Long associationId);


    @Query("select e.trait from EfoTrait e join e.associationsBkg a where a.id = :associationId")
    List<String> findBkgTraitNamesByAssociationsId(Long associationId);


    List<EfoTrait> findByAssociationsId(Sort sort, Long associationId);

    Page<EfoTrait> findByAssociationsId(Pageable pageable, Long associationId);


    List<EfoTrait> findByUri(String uri);


    List<EfoTrait> findByUri(Sort sort, String uri);

    Page<EfoTrait> findByUri(Pageable pageable, String uri);

    EfoTrait findByTraitIgnoreCase(String trait);

    EfoTrait findByShortForm(String shortForm);

    EfoTrait findByMongoSeqId(String mongoSeqId);


    Page<EfoTrait> findByStudiesPublicationIdPubmedId(String pumbedId, Pageable pageable);



}

