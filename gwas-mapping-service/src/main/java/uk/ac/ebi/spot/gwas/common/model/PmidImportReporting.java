package uk.ac.ebi.spot.gwas.common.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class PmidImportReporting {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Publication publication;

    private Integer studiesImported;

    private Integer studiesTotal;

    private String associationMapping;

    private Integer associationApproved;

    private Integer studiesPublished;

    private Date startDate;

    private Date completionDate;

    private String submissionId;

    private String status;

    private String curatorEmail;

    private String submissionType;

    private Integer associationMapped;

    private Integer associationTotal;
}
