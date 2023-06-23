package uk.ac.ebi.spot.gwas.updateefo.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Provenance {

    private final LocalDateTime timestamp;
    private final String userId;

    public Provenance(LocalDateTime timestamp, String userId) {
        this.timestamp = timestamp;
        this.userId = userId;
    }

}
