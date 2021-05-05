package uk.ac.ebi.spot.gwas.projection;

public interface MappingProjection {

    Long getAssociationId();
    Long getLocusId();
    String getGeneName();
    String getSnpRsid();
}
