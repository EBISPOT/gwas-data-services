package uk.ac.ebi.spot.gwas.ontology.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonPropertyOrder({
        "responseHeader",
        "response"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChildTrait {

    @JsonProperty("key")
    private String key;

    @JsonProperty("label")
    private String label;

}



