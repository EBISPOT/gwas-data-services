package uk.ac.ebi.spot.gwas.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class RestResponseResult {

    private String url;
    private String error;
    private String restResult;
    private long waitSeconds = 0;
    private int status;

    public boolean hasErorr() {
        return (this.getError() != null);
    }
}
