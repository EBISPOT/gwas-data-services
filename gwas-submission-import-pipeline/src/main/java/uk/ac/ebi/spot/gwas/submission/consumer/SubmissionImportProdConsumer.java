package uk.ac.ebi.spot.gwas.submission.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.SubmissionRabbitMessage;

@Slf4j
@Component
@Profile({"cluster","fallback"})
public class SubmissionImportProdConsumer {


    public void listen(SubmissionRabbitMessage submissionRabbitMessage) {

    }
}
