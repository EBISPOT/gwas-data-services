package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.rabbitmq.SubmissionImportMQProducer;
import uk.ac.ebi.spot.gwas.rabbitmq.SubmissionRabbitMessage;
import uk.ac.ebi.spot.gwas.service.SubmissionImportMessageService;


@Service
public class SubmissionImportMessageServiceImpl implements SubmissionImportMessageService {

    SubmissionImportMQProducer submissionImportMQProducer;


    public SubmissionImportMessageServiceImpl(SubmissionImportMQProducer submissionImportMQProducer) {
        this.submissionImportMQProducer = submissionImportMQProducer;
    }

    @Override
    public void sendMessage(String submissionId, String submissionType, String event, String result, String email) {
        uk.ac.ebi.spot.gwas.rabbitmq.SubmissionRabbitMessage submissionRabbitMessage = SubmissionRabbitMessage.builder()
                .submissionId(submissionId)
                .submissionType(submissionType)
                .event(event)
                .email(email)
                .result(result)
                .build();
        submissionImportMQProducer.send(submissionRabbitMessage);
    }
}
