package uk.ac.ebi.spot.gwas.association;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface SqlProjection {

    @JsonProperty("regionName")
    String getREGION_NAME();

    @JsonProperty("chromosomeName")
    String getCHROMOSOME_NAME();

}
