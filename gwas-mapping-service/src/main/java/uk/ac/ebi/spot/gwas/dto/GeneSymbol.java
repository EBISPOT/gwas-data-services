package uk.ac.ebi.spot.gwas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneSymbol {

    @JsonProperty("biotype")
    private String biotype;

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("id")
    private String id;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("version")
    private Integer version;

    @JsonProperty("logic_name")
    private String logicName;

    @JsonProperty("source")
    private String source;

    @JsonProperty("db_type")
    private String dbType;

    @JsonProperty("strand")
    private Integer strand;

    @JsonProperty("description")
    private String description;

    @JsonProperty("seq_region_name")
    private String seqRegionName;

    @JsonProperty("end")
    private Integer end;

    @JsonProperty("object_type")
    private String objectType;

    @JsonProperty("assembly_name")
    private String assemblyName;

    @JsonProperty("species")
    private String species;

    @JsonProperty("error")
    public String error;

    @JsonProperty("failed")
    public String failed;

}
