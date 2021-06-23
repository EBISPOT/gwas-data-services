package uk.ac.ebi.spot.gwas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Variation {

    @JsonProperty("name")
    public String name;

    @JsonProperty("ambiguity")
    public String ambiguity;

    @JsonProperty("source")
    public String source;

    @JsonProperty("mappings")
    public List<Mapping> mappings;

    @JsonProperty("var_class")
    public String varClass;

    @JsonProperty("minor_allele")
    public String minorAllele;

    @JsonProperty("evidence")
    public List<String> evidence;

    @JsonProperty("most_severe_consequence")
    public String mostSevereConsequence;

    @JsonProperty("MAF")
    public Double maf;

    @JsonProperty("synonyms")
    public List<String> synonyms;

    @JsonProperty("error")
    public String error;

    @JsonProperty("failed")
    public String failed;

}
