package uk.ac.ebi.spot.gwas.association;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.ac.ebi.spot.gwas.model.Association;

import java.util.List;
import java.util.Map;

@Repository
public interface AssociationRepository extends JpaRepository<Association, Long> {

    @Query(value = "SELECT DISTINCT(R.NAME) AS REGION_NAME, R.ID AS REGION_ID " +
            "FROM LOCATION L INNER JOIN REGION R ON L.REGION_ID = R.ID " +
            "WHERE L.CHROMOSOME_NAME = ?1", nativeQuery = true)
    List<Map<String, String>> findRegionsByChromosomeName(String chromosomeName);


    @Query(value = "SELECT DISTINCT(ET.TRAIT) FROM LOCATION L " +
            "INNER JOIN SNP_LOCATION SL ON L.ID = SL.LOCATION_ID " +
            "INNER JOIN REGION R ON L.REGION_ID = R.ID " +
            "INNER JOIN ASSOCIATION_SNP_VIEW ASV on ASV.SNP_ID = SL.SNP_ID " +
            "INNER JOIN ASSOCIATION ASSOC on ASV.ASSOCIATION_ID = ASSOC.ID " +
            "INNER JOIN ASSOCIATION_EFO_TRAIT AET on ASSOC.ID = AET.ASSOCIATION_ID " +
            "INNER JOIN EFO_TRAIT ET on AET.EFO_TRAIT_ID = ET.ID " +
            "WHERE ASSOC.PVALUE_MANTISSA * POWER(10, ASSOC.PVALUE_EXPONENT) < 5 * POWER(10, -8) " +
            "AND L.CHROMOSOME_NAME = ?1 AND R.NAME= ?2 ", nativeQuery = true)
    List<String> findByChromosomeNameAndRegion(String chromosomeName, String region);

    @Query(value = "SELECT DISTINCT (ASV.SNP_ID), SNP.RS_ID, ASSOC.PVALUE_MANTISSA, ASSOC.PVALUE_EXPONENT, ET.SHORT_FORM  FROM LOCATION L " +

            "INNER JOIN SNP_LOCATION SL ON L.ID = SL.LOCATION_ID " +
            "INNER JOIN REGION R ON L.REGION_ID = R.ID " +
            "INNER JOIN ASSOCIATION_SNP_VIEW ASV on ASV.SNP_ID = SL.SNP_ID " +
            "INNER JOIN ASSOCIATION ASSOC on ASV.ASSOCIATION_ID = ASSOC.ID " +
            "INNER JOIN ASSOCIATION_EFO_TRAIT AET on ASSOC.ID = AET.ASSOCIATION_ID " +
            "INNER JOIN EFO_TRAIT ET on AET.EFO_TRAIT_ID = ET.ID " +
            "INNER JOIN SINGLE_NUCLEOTIDE_POLYMORPHISM SNP on SNP.ID = ASV.SNP_ID " +

            "WHERE  R.NAME=?1 AND ET.TRAIT=?2", nativeQuery = true)
    List<Map<String, String>> findByRegionAndEfoTrait(String region, String efoTrait);


    @Query(value = "SELECT STD.ACCESSION_ID, DT.TRAIT, SNP.RS_ID, PUB.PUBMED_ID, AUTHOR.FULLNAME FROM SINGLE_NUCLEOTIDE_POLYMORPHISM SNP " +

            "INNER JOIN STUDY_SNP_VIEW SNV ON SNV.SNP_ID = SNP.ID " +
            "INNER JOIN STUDY STD ON STD.ID = SNV.STUDY_ID " +

            "INNER JOIN STUDY_DISEASE_TRAIT SDT ON SDT.STUDY_ID = SNV.STUDY_ID " +
            "INNER JOIN DISEASE_TRAIT DT ON DT.ID = SDT.DISEASE_TRAIT_ID " +

            "INNER JOIN STUDY_EFO_TRAIT SEF ON SEF.STUDY_ID = SNV.STUDY_ID " +
            "INNER JOIN EFO_TRAIT ET on SEF.EFO_TRAIT_ID = ET.ID " +


            "INNER JOIN PUBLICATION PUB ON STD.PUBLICATION_ID = PUB.ID " +
            "INNER JOIN AUTHOR ON AUTHOR.ID = PUB.FIRST_AUTHOR_ID " +

            "WHERE  SNP.RS_ID IN (?1) AND ET.TRAIT=?2", nativeQuery = true)
    List<Map<String, String>> findByRsidsAndTrait(List<String> rsIds, String efoTrait);
    

}
