package uk.ac.ebi.spot.gwas.zooma;

public interface ZoomaProjection {

    Long getStudy();
    String getBioEntity();
    String getPropertyType();
    String getPropertyValue();
    String getSemanticTag();

}
