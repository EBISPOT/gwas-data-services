package uk.ac.ebi.spot.gwas;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.config.AppConfig;
import uk.ac.ebi.spot.gwas.dto.*;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.AssociationReport;
import uk.ac.ebi.spot.gwas.repository.AssociationReportRepository;
import uk.ac.ebi.spot.gwas.service.loader.DataLoadingService;
import uk.ac.ebi.spot.gwas.service.mapping.DataSavingService;
import uk.ac.ebi.spot.gwas.service.mapping.EnsemblService;
import uk.ac.ebi.spot.gwas.util.CommandUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
@Component
public class Cli implements CommandLineRunner {

    @Autowired
    private AppConfig config;
    @Autowired
    private EnsemblService ensemblService;
    @Autowired
    private DataLoadingService dataService;
    @Autowired
    private DataSavingService dataSavingService;
    @Autowired
    private AssociationReportRepository associationReportRepository;

    private static final Integer DB_BATCH_SIZE = 1000;
    private static final Integer THREAD_SIZE = 40;
    private static final Integer MAPPING_THREAD_SIZE = 1;
    private static final String APP_COMMAND = "java -jar gwas-mapping-service.jar -m automatic_mapping_process";
    private final CommandLineParser parser = new DefaultParser();
    private final HelpFormatter help = new HelpFormatter();
    private final Options options = CommandUtil.bindOptions();


    @Override
    public void run(String... args) throws ParseException, InterruptedException, ExecutionException, IOException {
        log.info("Starting mapping service...");

        CommandLine commandLine = parser.parse(options, args, true);
        boolean viewHelp = commandLine.hasOption(CommandUtil.HELP_OPT);
        boolean runMapping = commandLine.hasOption(CommandUtil.MAPPING_OPT);
        boolean runCache = commandLine.hasOption(CommandUtil.CACHE_OPT);
        boolean runNight = commandLine.hasOption(CommandUtil.NIGHT_OPT);
        String performer = String.valueOf(commandLine.getArgList().get(0));

        if (viewHelp) {
            help.printHelp(APP_COMMAND, options, true);

        } else if (runMapping) {
            Object report = ""; //this.fullEnsemblRemapping();
            log.info(String.valueOf(report));

        } else if (runNight) {
            log.info("Night -n {}", performer);

        } else if (runCache) {
            int threadSize = Integer.parseInt(commandLine.getArgList().get(0));
            log.info("Caching Ensembl with {} thread size", threadSize);
            Object report = "";
            log.info(String.valueOf(report));
        }
        log.info("Application executed successfully!");
    }

    public Object fullEnsemblRemapping() throws ExecutionException, InterruptedException, IOException {
        log.info("Full remap commenced");
        long start = System.currentTimeMillis();

        MappingDto mappingDto = dataService.getSnpsLinkedToLocus(THREAD_SIZE, DB_BATCH_SIZE, CommandUtil.MAPPING_OPT);
        EnsemblData ensemblData = ensemblService.cacheEnsemblData(mappingDto);
        List<Association> associations = dataService.getAssociationObjects(THREAD_SIZE,
                                                                           DB_BATCH_SIZE,
                                                                           mappingDto.getTotalPagesToMap(),
                                                                           CommandUtil.MAPPING_OPT);

        List<MappingDto> mappingDtoList = new ArrayList<>();

        int count = MAPPING_THREAD_SIZE;
        for (List<Association> associationList : ListUtils.partition(associations, MAPPING_THREAD_SIZE)) {
            try {
                List<CompletableFuture<MappingDto>> futureList =
                        associationList.stream()
                                .map(association -> ensemblService.mapAndSaveData(association, ensemblData))
                                .collect(Collectors.toList());
                CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
                for (CompletableFuture<MappingDto> future : futureList) {
                    mappingDtoList.add(future.get());
                }
            } catch (Exception e) {
                log.error("Association was not mapped due to error {}", e.getMessage());
            }
            count += MAPPING_THREAD_SIZE;
            log.info("Finished Processing {} Association", count);
        }

        log.info("Total Association mapping time {}", (System.currentTimeMillis() - start));
        this.reportCheck(associations);
        // dataSavingService.saveRestHistory(ensemblData, config.getERelease(), THREAD_SIZE)
        return mappingDtoList;
    }

    public void reportCheck(List<Association> associations){
        List<Association> noErrorAssociations = new ArrayList<>();
        AtomicInteger counta = new AtomicInteger(1);

        associations.forEach(association -> {
            AssociationReport existingReport = associationReportRepository.findByAssociationId(association.getId());
            if (existingReport == null) {
                noErrorAssociations.add(association);
            }
            log.info("Checked Report for {}, {} of {}", association.getId(), counta.getAndIncrement(), associations.size());
        });

        AtomicInteger index = new AtomicInteger(1);
        noErrorAssociations.forEach(association -> {
            try {
                dataSavingService.createErrors(association);
                log.info("Saved Error Report {} : {}", index.get(), association.getId());
            }catch (Exception e){
                log.info("{} could not be saved {}", association.getId(), e.getMessage());
            }
            index.getAndIncrement();
        });
    }
}


//        try {
//        } catch (Exception e) {
//            log.info("No argument was supplied ( {} )", e.getMessage());
//            help.printHelp(APP_COMMAND, options, true);
//        }



// save History
// full db Mapping
//
