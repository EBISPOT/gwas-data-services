package uk.ac.ebi.spot.gwas.solr.dto;

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
public class SolrData {

    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader;

    @JsonProperty("response")
    private Response response;

}



