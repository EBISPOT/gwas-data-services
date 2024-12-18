package uk.ac.ebi.spot.gwas.overlap_gene;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OverlapGene {

    public OverlapGene(String error) {
        this.error = error; // This is needed when API returns error only
    }

    @JsonProperty("start")
    public Integer start;

    @JsonProperty("strand")
    public Integer strand;

    @JsonProperty("assembly_name")
    public String assemblyName;

    @JsonProperty("description")
    public String description;

    @JsonProperty("source")
    public String source;

    @JsonProperty("feature_type")
    public String featureType;

    @JsonProperty("seq_region_name")
    public String seqRegionName;

    @JsonProperty("gene_id")
    public String geneId;

    @JsonProperty("logic_name")
    public String logicName;

    @JsonProperty("version")
    public Integer version;

    @JsonProperty("id")
    public String id;

    @JsonProperty("external_name")
    public String externalName;

    @JsonProperty("end")
    public Integer end;

    @JsonProperty("biotype")
    public String biotype;

    @JsonProperty("error")
    public String error;

}

