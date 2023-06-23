package uk.ac.ebi.spot.gwas.updateefo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTraitPage;
import uk.ac.ebi.spot.gwas.updateefo.report.ReportTemplate;
import uk.ac.ebi.spot.gwas.updateefo.repository.EfoTraitRepository;
import uk.ac.ebi.spot.gwas.updateefo.runnable.ObsoleteOnlyTask;
import uk.ac.ebi.spot.gwas.updateefo.runnable.UpdateAndObsoleteTask;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Executor implements ApplicationRunner {

    @Value("${db.batch.size}")
    private int dbBatchSize;
    @Value("${ols.batch.size}")
    private int olsBatchSize;
    private final EfoTraitRepository efoTraitRepository;
    private final EmailService emailService;
    private final ReportTemplate reportTemplate = new ReportTemplate();
    private final Environment environment;

    public Executor(EfoTraitRepository efoTraitRepository, EmailService emailService, Environment environment) {
        this.efoTraitRepository = efoTraitRepository;
        this.emailService = emailService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CountDownLatch latch = new CountDownLatch(0);
        if ("update".equals(environment.getActiveProfiles()[0])) {
            long count = efoTraitRepository.count();
            latch = new CountDownLatch((int) count / dbBatchSize);
            if (count % dbBatchSize != 0) {
                latch = new CountDownLatch((int) count / dbBatchSize + 1);
            }
            for (int i = 0; i < count; i += dbBatchSize) {
                executorService.execute(new UpdateAndObsoleteTask(i, dbBatchSize, efoTraitRepository, latch, reportTemplate));
            }
        } else if ("obsolete".equals(environment.getActiveProfiles()[0])) {
            RestTemplate restTemplate = new RestTemplate();
            OlsEfoTraitPage olsEfoTraitPage = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms?obsoletes=true&size=" + olsBatchSize, OlsEfoTraitPage.class);
            int totalPages = olsEfoTraitPage.getPage().getTotalPages();
            latch = new CountDownLatch(totalPages);
            for (int i = 0; i < totalPages; i++) {
                executorService.execute(new ObsoleteOnlyTask(i, olsBatchSize, efoTraitRepository, latch, reportTemplate));
            }
        }
        else {
            System.out.println("No profile is specified, please use 'update' Spring profile to handle changed and obsoleted EFOs," +
                    " or 'obsolete' to handle obsolete traits only (more optimized)");
        }

        latch.await();
        System.out.println(reportTemplate.generateReport());
        emailService.sendEmail("Update and obsolete EFO job report", reportTemplate.generateReport());
        executorService.shutdownNow();
    }
}
