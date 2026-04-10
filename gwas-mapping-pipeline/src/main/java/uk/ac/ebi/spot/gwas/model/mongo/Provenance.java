package uk.ac.ebi.spot.gwas.model.mongo;

import lombok.*;
import org.joda.time.DateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Provenance {

    private DateTime timestamp;
    private String userId;

}
