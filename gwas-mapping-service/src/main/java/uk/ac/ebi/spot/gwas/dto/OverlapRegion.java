package uk.ac.ebi.spot.gwas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonPropertyOrder({
        "strand",
        "feature_type",
        "start",
        "assembly_name",
        "end",
        "id",
        "seq_region_name",
        "stain"
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OverlapRegion {

    @JsonProperty("strand")
    public Integer strand;

    @JsonProperty("feature_type")
    public String featureType;

    @JsonProperty("start")
    public Integer start;

    @JsonProperty("assembly_name")
    public String assemblyName;

    @JsonProperty("end")
    public Integer end;

    @JsonProperty("id")
    public String id;

    @JsonProperty("seq_region_name")
    public String seqRegionName;

    @JsonProperty("stain")
    public String stain;

    @JsonProperty("error")
    public String error;

}
