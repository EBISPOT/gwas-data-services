package uk.ac.ebi.spot.gwas.ontology;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.ontology.dto.ParentTrait;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ParentTraitService {

    private List<ParentTrait> traits = new ArrayList<>();

    @PostConstruct
    public void loadTraits() {
        try (InputStream is = getClass().getResourceAsStream("/parent_traits.csv")) {
            if (is == null) {
                log.error("Could not find parent_traits.csv in resources");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                this.traits = reader.lines()
                        .skip(1) // Skip header
                        .filter(line -> line != null && !line.trim().isEmpty())
                        .map(line -> line.split(","))
                        .filter(parts -> parts.length >= 2)
                        .map(parts -> ParentTrait.builder()
                                .efoId(parts[0].trim())
                                .label(parts[1].trim())
                                .build())
                        .collect(Collectors.toList());
                log.info("Loaded {} parent traits from CSV", traits.size());
            }
        } catch (IOException e) {
            log.error("Error loading parent traits from CSV: {}", e.getMessage());
        }
    }

    public List<ParentTrait> getTraits() {
        return traits;
    }
}
