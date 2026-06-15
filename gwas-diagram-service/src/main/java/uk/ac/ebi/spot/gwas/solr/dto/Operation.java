package uk.ac.ebi.spot.gwas.solr.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Component
public class Operation {

    private Operation() {
        // Hide implicit constructor
    }

    public static String generateUUId() {
        LocalDate date = LocalDate.now();
        return String.format("%s-%s_%s_%s", UUID.randomUUID(), date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    public static String prettyPrint(Object data) {
        ObjectMapper mapper = new ObjectMapper();
        String pretty;
        try {
            pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            pretty = e.getMessage();
        }
        log.info(String.format("\n%s", pretty));
        return pretty;
    }
}
