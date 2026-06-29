package uk.ac.ebi.spot.gwas.submission.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.dto.curation.SubmissionRabbitMessage;
import uk.ac.ebi.spot.gwas.submission.rabbitmq.SubmissionImportMQProducer;
import uk.ac.ebi.spot.gwas.submission.service.SubmissionImportMessageService;

@Service
public class SubmissionImportMessageServiceImpl implements SubmissionImportMessageService {

    SubmissionImportMQProducer submissionImportMQProducer;


    public SubmissionImportMessageServiceImpl(SubmissionImportMQProducer submissionImportMQProducer) {
        this.submissionImportMQProducer = submissionImportMQProducer;
    }

    @Override
    public void sendMessage(String submissionId, String submissionType, String event,String result, String email) {
        SubmissionRabbitMessage submissionRabbitMessage = SubmissionRabbitMessage.builder()
                .submissionId(submissionId)
                .submissionType(submissionType)
                .event(event)
                .email(email)
                .build();
        submissionImportMQProducer.send(submissionRabbitMessage);
    }
}
