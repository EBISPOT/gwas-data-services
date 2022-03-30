package uk.ac.ebi.spot.gwas.zooma;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "study",
        "variant_id",
        "p_value",
        "chromosome",
        "base_pair_location",
        "effect_allele",
        "other_allele",
        "effect_allele_frequency",
        "beta",
        "standard_error"
})
public class Associationx {

    public Associationx(String study) {
        this.study = study;
    }

    @JsonProperty("STUDY")
    public String study;

    @JsonProperty("variant_id")
    public String variantId;

    @JsonProperty("BIOENTITY")
    public String bioEntity;

    @JsonProperty("PROPERTY_TYPE")
    public String propertyType;

    @JsonProperty("PROPERTY_VALUE")
    public String propertyValue;

    @JsonProperty("SEMANTIC_TAG")
    public String semanticTag;

    /*
    Zooma Data Export
    Europe PMC Data Export
    EBI Search Data Export
    Curation Queue Data Export
    NCBI Data Export
     */
}




