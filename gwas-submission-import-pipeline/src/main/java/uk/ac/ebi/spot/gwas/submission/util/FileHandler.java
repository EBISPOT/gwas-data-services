package uk.ac.ebi.spot.gwas.submission.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.gwas.annotation.nextflow.dto.JobMapperDTO;
import uk.ac.ebi.spot.gwas.deposition.exception.FileProcessingException;
import uk.ac.ebi.spot.gwas.submission.config.NextFlowJobConfig;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class FileHandler {

    NextFlowJobConfig nextFlowJobConfig;


    public void writeToCsv(List<JobMapperDTO> jobMapperDTOS, String fileName) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName));
            for (JobMapperDTO jobMapperDTO : jobMapperDTOS) {
                bufferedWriter.write(jobMapperDTO.getJobId());
                bufferedWriter.write(",");
                bufferedWriter.write(jobMapperDTO.getJobCommand());
                bufferedWriter.write("\n");
            }
            bufferedWriter.close();
        } catch (Exception e) {
            log.error("Exception in writing file {}",fileName);
        }

    }

    public  byte[] serializePojoToTsv(List<?> pojoList) {
        CsvMapper csvMapper = new CsvMapper();
        List<Map<String, Object>> dataList = csvMapper.convertValue(pojoList, new TypeReference<Object>() {
        });
        List<List<String>> csvData = new ArrayList<>();
        List<String> csvHead = new ArrayList<>();
        AtomicInteger counter = new AtomicInteger();
        dataList.forEach(row -> {
            List<String> rowData = new ArrayList<>();
            row.forEach((key, value) -> {
                if(value.getClass().equals(String.class)){
                    log.info("Value inside String data type {}", value);
                    rowData.add(value.toString().replaceAll("\"\"",""));
                    log.info("Value inside String data type after replacing {}", value.toString().replaceAll("\"\"",""));
                }
                else {
                    rowData.add(String.valueOf(value));
                }
                if (counter.get() == 0) {
                    csvHead.add(key);
                }
            });
            csvData.add(rowData);
            counter.getAndIncrement();
        });
        CsvSchema.Builder builder = CsvSchema.builder();
        csvHead.forEach(builder::addColumn);
        CsvSchema schema = builder.build().withLineSeparator("\n").withColumnSeparator(',');
        //String result = "";
        byte[] result;
        try {
            // result = csvMapper.writer(schema).writeValueAsString(csvData);
            result = csvMapper.writer(schema).writeValueAsBytes(csvData);
        } catch (IOException e) {
            throw new FileProcessingException("Could not read the file");
        }
        return result;
    }

}
