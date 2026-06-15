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
public class TermApiDoc implements Serializable {

    @JsonProperty("short_form")
    private String shortForm;
    @JsonProperty("label")
    private String label;

}
