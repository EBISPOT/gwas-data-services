package uk.ac.ebi.spot.gwas.updateefo.runnable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTrait;
import uk.ac.ebi.spot.gwas.updateefo.domain.EfoTrait;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTraitPage;
import uk.ac.ebi.spot.gwas.updateefo.report.ReportTemplate;
import uk.ac.ebi.spot.gwas.updateefo.repository.EfoTraitRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class UpdateAndObsoleteTask implements Runnable {

    private final int start;
    private final int size;
    private final EfoTraitRepository efoTraitRepository;
    private final ReportTemplate reportTemplate;
    private final CountDownLatch latch;

    public UpdateAndObsoleteTask(int start, int size, EfoTraitRepository efoTraitRepository, CountDownLatch latch, ReportTemplate reportTemplate) {
        this.size = size;
        this.start = start;
        this.efoTraitRepository = efoTraitRepository;
        this.latch = latch;
        this.reportTemplate = reportTemplate;
    }

    @Override
    public void run() {
        try {
            Pageable pageable = PageRequest.of(start / size, size);
            Page<EfoTrait> page = efoTraitRepository.findAll(pageable);
            List<EfoTrait> efoTraits = page.get().collect(Collectors.toList());
            RestTemplate restTemplate = new RestTemplate();
            for (EfoTrait efoTrait : efoTraits) {
                try {
                    reportTemplate.addProcessed();
                    String efoEncodedUri = URLEncoder.encode(efoTrait.getUri(), StandardCharsets.UTF_8.toString());
                    OlsEfoTrait olsEfoTrait = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms/" + efoEncodedUri, OlsEfoTrait.class);
                    if (olsEfoTrait.isObsolete() && olsEfoTrait.getTermReplacedBy() != null) {
                        EfoTrait newEfo;
                        if (olsEfoTrait.getTermReplacedBy().startsWith("http")) {
                            efoEncodedUri = URLEncoder.encode(olsEfoTrait.getTermReplacedBy(), StandardCharsets.UTF_8.toString());
                            olsEfoTrait = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms/" + efoEncodedUri, OlsEfoTrait.class);
                        }
                        else {
                            olsEfoTrait = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms?short_form=" + olsEfoTrait.getTermReplacedBy(), OlsEfoTraitPage.class).get_embedded().getTerms().get(0);
                        }
                        newEfo = new EfoTrait(efoTrait.getId(), olsEfoTrait.getLabel(), olsEfoTrait.getShortForm(), olsEfoTrait.getIri(), efoTrait.getCreated(), efoTrait.getUpdated());
                        efoTraitRepository.save(newEfo);
                        reportTemplate.addObsolete(efoTrait, newEfo);
                    } else if (!olsEfoTrait.getLabel().equals(efoTrait.getTrait())) {
                        EfoTrait newEfo = new EfoTrait(efoTrait.getId(), olsEfoTrait.getLabel(), efoTrait.getShortForm(), efoTrait.getUri(), efoTrait.getCreated(), efoTrait.getUpdated());
                        efoTraitRepository.save(newEfo);
                        reportTemplate.addUpdated(efoTrait, newEfo);
                    }
                } catch (Exception e) {
                    reportTemplate.addError(e, efoTrait.getShortForm());
                }
            }
        }
        catch (Exception e) {
            reportTemplate.addError(e, null);
            throw new RuntimeException(e);
        }
        finally {
            latch.countDown();
        }
    }

}
