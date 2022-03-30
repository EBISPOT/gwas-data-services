package uk.ac.ebi.spot.gwas.common.projection;

public interface MappingProjection {

    Long getAssociationId();
    Long getLocusId();
    String getGeneName();
    String getSnpRsid();
}
