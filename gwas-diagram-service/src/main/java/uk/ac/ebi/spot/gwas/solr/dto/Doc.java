package uk.ac.ebi.spot.gwas.solr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "resource_name",
        "cytogeneticRegion",
        "chromosome",
        "facet",
        "parentTrait",
        "category",
        "traits",
        "id"
})
public class Doc {

    private String id;

    @JsonProperty("cytogenetic_region")
    private String cytogeneticRegion;
    private String chromosome;
    private String facet; // full_data, by_parent_trait
    private List<String> traits;

    @JsonProperty("parent_trait")
    private String parentTrait;
    private String category;

    private String type;
    private String data;

    @JsonProperty("resource_name")
    private String resourceName; // full_data, by_parent_trait

}
