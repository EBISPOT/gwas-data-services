package uk.ac.ebi.spot.gwas.chromosome;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "region",
        "chromosome",
        "facet",
        "parentTrait",
        "traits",
        "categories",
        "id"
})
public class ChromosomeDto {

    private String id;
    private String region;
    private String chromosome;
    private List<String> traits;
    private Map<String, String> categories;

    @JsonIgnore
    private String facet;
    @JsonIgnore
    private String parentTrait;

}
