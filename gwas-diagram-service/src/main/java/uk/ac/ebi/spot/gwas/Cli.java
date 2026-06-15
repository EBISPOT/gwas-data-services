package uk.ac.ebi.spot.gwas;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import uk.ac.ebi.spot.gwas.diagram_loader.DiagramLoaderService;
import uk.ac.ebi.spot.gwas.solr.SolrService;
import uk.ac.ebi.spot.gwas.solr.dto.Operation;

import java.io.IOException;
import java.util.concurrent.Callable;

@Slf4j
@Component
public class Cli {

    @Autowired
    private DiagramLoaderService indexerService;

    @Autowired
    private SolrService solrService;

    @Bean
    CommandLineRunner commandLineRunner(CommandLine.IFactory factory, MyCommand myCommand) {
        return args -> new CommandLine(myCommand, factory).execute(args);
    }

    @Bean
    MyCommand myCommand() {
        return new MyCommand();
    }

    @CommandLine.Command(name = "gwas-diagram-service", mixinStandardHelpOptions = true,
            subcommands = {LoadCommand.class}, description = "GWAS diagram loader CLI API")
    static class MyCommand implements Runnable {
        public void run() {
            log.info("Use a subcommand! Try `load --mode server to load `");
        }
    }

    @CommandLine.Command(name = "start", description = "Cleans the diagram Solr data and reloads a fresh one")
    public class LoadCommand implements Callable<Integer> {

        @CommandLine.Option(names = {"-m", "--mode"}, description = "Mode to run", required = true)
        String mode;

        public Integer call() throws JSONException, IOException {
            log.info("Running in {} mode", mode);
            if (mode.equals("refresh-data")) {
                Operation.prettyPrint(solrService.cleanSolr());
                log.info("Deleted old Solr indexes \n Full indexing starting now");
                indexerService.indexFullData();
                System.exit(0);
            } else if (mode.equals("api-mode")) {
                log.info("App running in API server mode, visit /gwas/diagram-api/chromosomes/1");
            }else {
                log.info("Ensure you specify mode either --mode api-mode or --mode refresh-data");
                System.exit(0);
            }

            return 0;
        }
    }

}


// java -jar --spring.profiles.active=local gwas-diagram-service.jar start --mode refresh-data

// java -jar -Dspring.profiles.active=dev gwas-diagram-service.jar start --mode refresh-data

// java -jar --spring.profiles.active=dev gwas-diagram-service.jar >/dev/null 2>&1 &

/*

--mode refresh-data
--mode api-mode

Local Diagram Build:
--------------------
DB_URL=jdbc:oracle:thin:@//ora-spot-pub-hl.ebi.ac.uk:1521/SPOTPUB \
DB_USER=gwas_spotpub DB_PASSWORD=30Z3UaZs1VYV6kb0 \
java -Dspring.profiles.active=local -jar gwas-diagram-service.jar start --mode refresh-data

Dev Diagram Build:
--------------------
DB_URL=jdbc:oracle:thin:@//ora-spot-dev2-hl.ebi.ac.uk:1521/SPOTDV2 \
DB_USER=gwas_spotdv2 DB_PASSWORD=w1cRUcK7f6q5s2vf \
java -Dspring.profiles.active=dev -jar gwas-diagram-service.jar start --mode api-mode >/dev/null 2>&1 &


Pre-staging Diagram Build:
--------------------
DB_URL=jdbc:oracle:thin:@//ora-spot-pub-hl.ebi.ac.uk:1521/SPOTPUB \
DB_USER=gwas_spotpub DB_PASSWORD=30Z3UaZs1VYV6kb0 \
java -Dspring.profiles.active=prestaging -jar gwas-diagram-service.jar start --mode api-mode


DB_URL=jdbc:oracle:thin:@//ora-spot-pub-hl.ebi.ac.uk:1521/SPOTPUB \
DB_USER=gwas_spotpub DB_PASSWORD=30Z3UaZs1VYV6kb0 \
java -Dspring.profiles.active=staging -jar gwas-diagram-service.jar start --mode api-mode

Deploy UI to github -> snoopy, garfield - Then deploy bakcend to prod -> Ui to prod
 */

//Staging: http://ves-hx-7f.ebi.ac.uk:8989/gwas/diagram-api/chromosomes/1
