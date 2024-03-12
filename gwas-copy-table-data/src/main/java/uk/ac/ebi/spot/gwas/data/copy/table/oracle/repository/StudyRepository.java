package uk.ac.ebi.spot.gwas.data.copy.table.oracle.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uk.ac.ebi.spot.gwas.data.copy.table.model.Study;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by emma on 20/11/14.
 *
 * @author emma
 * <p>
 * Repository accessing Study entity object
 */



public interface StudyRepository extends JpaRepository<Study, Long>, JpaSpecificationExecutor {



    Page<Study> findByHousekeepingIsPublished(Pageable pageable, Boolean isPublished);


    Page<Study> findByAccessionId(String gcst, Pageable pageable);

    Optional<Study> findByAccessionId(String gcst);


    Page<Study> findById(Long studyId, Pageable pageable);



    Collection<Study> findByDiseaseTraitId(Long diseaseTraitId);

    Page<Study> findByDiseaseTraitId(Long diseaseTraitId, Pageable pageable);

    // THOR

    Collection<Study> findByPublicationIdPubmedId(String pubmedId);


    Collection<Study> findTop10ByPublicationIdPubmedId(String pubmedId);

    Page<Study> findByPublicationIdPubmedId(String pubmedId, Pageable pageable);


    // Pageable queries for filtering main page

    Page<Study> findByHousekeepingCurationStatusIdAndHousekeepingCuratorId(Long status,
                                                                           Long curator,
                                                                           Pageable pageable);


    Page<Study> findByHousekeepingCurationStatusId(Long status, Pageable pageable);


    Page<Study> findByHousekeepingCurationStatusIdNot(Long status, Pageable pageable);


    Page<Study> findByHousekeepingCuratorId(Long curator, Pageable pageable);


    // Custom query to find studies in reports table
    @Query("select s from Study s where s.housekeeping.curator.id like :curator and s.housekeeping.curationStatus.id like :status and EXTRACT(YEAR FROM (TRUNC(TO_DATE(s.publicationId.publicationDate), 'YEAR'))) = :year and EXTRACT(MONTH FROM (TRUNC(TO_DATE(s.publicationId.publicationDate), 'MONTH'))) = :month")
    Page<Study> findByPublicationDateAndCuratorAndStatus(@Param("curator") Long curator, @Param("status") Long status,
                                                         @Param("year") Integer year,
                                                         @Param("month") Integer month, Pageable pageable);

    // Queries for study types
    Page<Study> findByGxe(Boolean gxe, Pageable pageable);

    Page<Study> findByGxg(Boolean gxg, Pageable pageable);


    Page<Study> findByCnv(Boolean cnv, Pageable pageable);


    Page<Study> findByHousekeepingCheckedMappingErrorOrHousekeepingCurationStatusId(Boolean checkedMappingError,
                                                                                    Long status,
                                                                                    Pageable pageable);

    Page<Study> findByGenotypingTechnologiesGenotypingTechnology(String genotypingTechnology, Pageable pageable);


    List<Study> findStudyDistinctByAssociationsMultiSnpHaplotypeTrue(Sort sort);

    List<Study> findStudyDistinctByAssociationsSnpInteractionTrue(Sort sort);


    Collection<Study> findByEfoTraitsId(Long efoTraitId);


    Collection<Study> findByMappedBackgroundTraitsId(Long efoTraitId);


    // EFO trait query
    Page<Study> findByEfoTraitsId(Long efoTraitId, Pageable pageable);

    // Query housekeeping notes field

    Page<Study> findByHousekeepingNotesContainingIgnoreCase(String query, Pageable pageable);

    // Query note field

    Page<Study> findDistinctByNotesTextNoteContainingIgnoreCase(String query, Pageable pageable);


        //Removed Distinct because publicationDate is another table. With distinct returns just the Study attributes
    Page<Study> findByNotesTextNoteContainingIgnoreCase(String query, Pageable pageable);

    // THOR to change
    // Custom query to get list of study authors
    //@Query("select distinct s.author from Study s") List<String> findAllStudyAuthors(Sort sort);

    // THOR
    //Page<Study> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    Page<Study> findByPublicationIdFirstAuthorFullnameContainingIgnoreCase(String author, Pageable pageable);


    Page<Study> findByPublicationIdFirstAuthorFullnameStandardContainingIgnoreCase(String author, Pageable pageable);


    List<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull();


    List<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(Sort sort);


    Page<Study> findByHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(Pageable pageable);


    Page<Study> findByPublicationIdPubmedIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(String pubmedId, Pageable pageable);


    List<Study> findByAssociationsLociStrongestRiskAllelesSnpIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long snpId);


    List<Study> findByAssociationsIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long associationId);


    List<Study> findByDiseaseTraitIdAndHousekeepingCatalogPublishDateIsNotNullAndHousekeepingCatalogUnpublishDateIsNull(
            Long diseaseTraitId);


    List<Study> findByHousekeepingCatalogPublishDateIsNullOrHousekeepingCatalogUnpublishDateIsNotNull();

    Study findByAssociationsId(Long associationId);

    Page<Study> findByFullPvalueSet(Boolean fullPvalueSet, Pageable pageable);

    Page<Study> findByUserRequested(Boolean userRequested, Pageable pageable);


    Page<Study> findByOpenTargets(Boolean openTargets, Pageable pageable);



    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true, value = "update study s set s.publication_id=null where s.id=:studyId")
    void setPublicationIdNull(@Param("studyId") Long studyId);


    @Query(value = "SELECT 'GCST' || accession_seq.nextval FROM dual", nativeQuery =
            true)
    String getNextAccessionId();

    Page<Study> findAll(Pageable pageable);

}

