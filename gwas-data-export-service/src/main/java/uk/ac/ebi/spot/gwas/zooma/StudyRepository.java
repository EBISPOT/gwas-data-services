package uk.ac.ebi.spot.gwas.zooma;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyRepository extends JpaRepository<Study, Long> {

    @Query(value = "SELECT DISTINCT s.PUBMED_ID AS study, snp.RS_ID AS bioEntity, 'Disease or Phenotype' " +
            "AS propertyType, dt.TRAIT AS propertyValue, et.URI AS semanticTag FROM STUDY s " +
            "         JOIN ASSOCIATION a ON a.STUDY_ID = s.ID " +
            "         JOIN ASSOCIATION_LOCUS al ON al.ASSOCIATION_ID = a.ID " +
            "         JOIN LOCUS_RISK_ALLELE lra ON lra.LOCUS_ID = al.LOCUS_ID " +
            "         JOIN RISK_ALLELE_SNP ras ON ras.RISK_ALLELE_ID = lra.RISK_ALLELE_ID " +
            "         JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM snp ON snp.ID = ras.SNP_ID " +
            "         JOIN STUDY_DISEASE_TRAIT sdt ON sdt.STUDY_ID = s.ID " +
            "         JOIN DISEASE_TRAIT dt ON dt.ID=sdt.DISEASE_TRAIT_ID " +
            "         JOIN ASSOCIATION_EFO_TRAIT aet ON aet.ASSOCIATION_ID = a.ID " +
            "         JOIN EFO_TRAIT et ON et.ID = aet.EFO_TRAIT_ID", nativeQuery = true)
    List<ZoomaProjection> findAllActiveUsersNative(Pageable pageable);


    @Query("Select association from Association as association " +
            "JOIN association.study study " +
            "WHERE study.id = :studyId ")
    List<Associationx> findAssociationByStudyId(@Param("studyId") Long studyId);


    @Query("select distinct study.id as study FROM Study as study")
    List<Object> findAllWithFewAttributes(Pageable pageable);
}

