package uk.ac.ebi.spot.gwas.model;

public interface MappingProjection {

    String getChromosomeName();
    Integer getChromosomePosition();
    String getRegionName();
    String getGeneName();
    Long getGeneId();

    Boolean getIsDownstream();

    Boolean getIsUpstream();

    Boolean getIsIntergenic();

    Boolean getIsClosestGene();

    Long getDistance();



}
