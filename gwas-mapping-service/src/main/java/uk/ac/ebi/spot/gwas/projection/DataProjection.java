package uk.ac.ebi.spot.gwas.projection;

public interface DataProjection {

    String getFullName();
    String getFullNameStandard();
    String getSort();
    String getOrcId();
    Long getPublicationId();
    Long getAssociationCount();

    String getAccessionId();
    Boolean getFullPvalueSet();

    String getAncestralGroup();
    String getCountryName();

    String getGenotypingTechnology();

}
