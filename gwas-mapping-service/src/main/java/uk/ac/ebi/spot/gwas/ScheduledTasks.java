package uk.ac.ebi.spot.gwas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.service.mapping.EnsemblRunnner;

import java.util.concurrent.ExecutionException;

@Component
public class ScheduledTasks {

    @Autowired
    private EnsemblRunnner ensemblRunnner;
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    @Scheduled(fixedDelay = 18000000)
    public void scheduleTaskWithFixedDelay() throws ExecutionException, InterruptedException {
        log.info("Fixed Delay Task :: Starting Mapping Pipeline");
        String performer = "automatic_mapping_process";
        ensemblRunnner.mapSomeAssociations(performer);
    }

}
