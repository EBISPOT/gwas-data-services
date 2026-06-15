package uk.ac.ebi.spot.gwas.ontology.ols_payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OLSTermApiDoc implements Serializable {

    @JsonProperty("_embedded")
    private OLSResponseDoc embedded;

    @JsonProperty("_links")
    private Links links;
}
