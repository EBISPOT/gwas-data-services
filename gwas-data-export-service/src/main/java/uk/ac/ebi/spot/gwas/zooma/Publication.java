package uk.ac.ebi.spot.gwas.zooma;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
public class Publication {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String pubmedId;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String submissionId;

    @Transient
    private boolean activeSubmission = false;

    @Transient
    private boolean openTargets = false;

    @Transient
    private boolean userRequested = false;


}
