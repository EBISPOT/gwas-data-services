package uk.ac.ebi.spot.gwas.association;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({
        "snp",
        "pValueMantissa",
        "pValueExponent",
        "efoMapping",
        "gwasTraits",
        "study"
})
public class AssociationDto {

    private String snp;
    private String pValueMantissa;
    private String pValueExponent;
    private String efoMapping;
    private String gwasTraits;
    private String study;
    private String efoId;
    private String pubmedId;
    private String author;

}
