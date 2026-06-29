package uk.ac.ebi.spot.gwas.rabbitmq;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionRabbitMessage {

    private String submissionId;

    private String submissionType;

    private String event;

    private String email;

    private String result;
}
