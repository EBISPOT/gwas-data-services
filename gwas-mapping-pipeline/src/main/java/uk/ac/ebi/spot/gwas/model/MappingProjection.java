package uk.ac.ebi.spot.gwas.model;

public interface MappingProjection {

    Long getAssociationId();
    Long getLocusId();
    String getGeneName();
    String getSnpRsid();
}
