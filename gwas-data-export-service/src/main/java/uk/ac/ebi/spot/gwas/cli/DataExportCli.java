package uk.ac.ebi.spot.gwas.cli;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.mapper.Mapper;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.zooma.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DataExportCli implements CommandLineRunner {

    private static final String APP_COMMAND = "java -jar gwas-mapping-service.jar -m automatic_mapping_process";
    private final CommandLineParser parser = new DefaultParser();
    private final HelpFormatter help = new HelpFormatter();
    private final Options options = Command.bindOptions();

    @Autowired
    private StudyRepository studyRepository;

//    @Autowired
//    private EnsemblRunnner ensemblRunnner;

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Override
    public void run(String... args) throws ParseException, IOException {
        log.info("Starting mapping service...");

        CommandLine commandLine = parser.parse(options, args, true);
        boolean isHelp = commandLine.hasOption(Command.HELP_OPT);
        boolean isRunCache = commandLine.hasOption(Command.CACHE_OPT);
        boolean isMapSomeSNPs = commandLine.hasOption(Command.MAP_SOME_SNPS_INDB_OPT);
        boolean isRemapAllSNPs = commandLine.hasOption(Command.MAP_ALL_SNPS_INDB_OPT);
        List<String> argList = commandLine.getArgList();

        if (isHelp) {
            help.printHelp(APP_COMMAND, options, true);
            System.exit(1);
        } else if (!argList.isEmpty()) {
            String performer = String.valueOf(commandLine.getArgList().get(0));
            if (isRemapAllSNPs) {
                log.info("Mapping -r {}", performer);
            } else if (isMapSomeSNPs) {
                log.info("Start Getting All");
                log.info("Mapping -m {}", performer);
                Page<Study> data = studyRepository.findAll(PageRequest.of(1, 10));
                List<Dto> dtos = new ArrayList<>();
                for (Study study : data.getContent()) {
                    Collection<EfoTrait> efoTraits = study.getEfoTraits();
                    study.getSnps().forEach(snp -> {
                        Dto dto = Dto.builder()
                                .study(study.getPublicationId().getPubmedId())
                                .propertyType("Disease or Phenotype")
                                .propertyValue(study.getDiseaseTrait().getTrait())
                                .semanticTag(efoTraits.stream().map(EfoTrait::getUri).collect(Collectors.toList()).get(0))
                                .bioEntity(String.valueOf(snp.getRsId()))
                                .build();
                        dtos.add(dto);
                    });
                }

                String fileName = String.format("/split-%s.csv", 45);
                this.saveToFile(fileName, serializePojoToCsv(dtos));
            } else if (isRunCache) {
                int threadSize = Integer.parseInt(commandLine.getArgList().get(1));
            }
            System.exit(1);
        }
        log.info("Application did not execute, ... shutting down");
        System.exit(0);
    }


    public void saveToFile(String fileN, String dataToSave) {
        String directory = "/Users/abayomi/Downloads";
        String fileName = directory + fileN;
        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
                writer.append(dataToSave);
                log.info("Data written to {}", fileName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String serializePojoToCsv(List<?> pojoList) throws IOException {
        CsvMapper csvMapper = new CsvMapper();
        List<Map<String, Object>> dataList = csvMapper.convertValue(pojoList, new TypeReference<List<Map<String, Object>>>() {
        });
        List<List<String>> csvData = new ArrayList<>();
        List<String> csvHead = new ArrayList<>();

        AtomicInteger counter = new AtomicInteger();
        dataList.forEach(row -> {
            List<String> rowData = new ArrayList<>();
            row.forEach((key, value) -> {
                rowData.add(String.valueOf(value));
                if (counter.get() == 0) {
                    csvHead.add(key);
                }
            });
            csvData.add(rowData);
            counter.getAndIncrement();
        });

        CsvSchema.Builder builder = CsvSchema.builder();
        csvHead.forEach(builder::addColumn);
        CsvSchema schema = builder.build().withHeader().withLineSeparator("\n").withColumnSeparator(',').withQuoteChar('"');
        return csvMapper.writer(schema).writeValueAsString(csvData);
    }
}


@JsonPropertyOrder({
        "STUDY",
        "BIOENTITY",
        "PROPERTY_TYPE",
        "PROPERTY_VALUE",
        "SEMANTIC_TAG"
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
class Dto {

    @JsonProperty("STUDY")
    private String study;

    @JsonProperty("BIOENTITY")
    private String bioEntity;

    @JsonProperty("PROPERTY_TYPE")
    private String propertyType;

    @JsonProperty("PROPERTY_VALUE")
    private String propertyValue;

    @JsonProperty("SEMANTIC_TAG")
    private String semanticTag;
}


// -m automatic_mapping_process 40
