package uk.ac.ebi.spot.gwas.submission.util;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.deposition.messaging.email.EmailService;
import uk.ac.ebi.spot.gwas.submission.config.NextFlowJobConfig;

@Component
public class EmailHelperUtil {

    NextFlowJobConfig nextFlowJobConfig;

    EmailService emailService;

    public EmailHelperUtil(NextFlowJobConfig nextFlowJobConfig,
                           EmailService emailService) {
        this.nextFlowJobConfig = nextFlowJobConfig;
        this.emailService = emailService;
    }

    public Pair<String, String> getBody(String pmid, String submissionId, String status) {
        String subject = String.format("Submission import [ %s | %s] - outcome: %s", submissionId, pmid, status);
        String body = String.format(" - Submission: %s \n  - PMID: %s \n - Outcome: %s \n ---------------------", submissionId, pmid, status);
        return Pair.of(subject, body);
    }

    public void sendMessage( String subject, String body) {
        for(String emailAddress : nextFlowJobConfig.getToAddress().split(",")) {
            emailService.sendMessage(emailAddress, subject, body, false);
        }
    }

}
