package uk.ac.ebi.spot.gwas.updateefo.runnable;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.updateefo.domain.EfoTrait;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTrait;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTraitPage;
import uk.ac.ebi.spot.gwas.updateefo.report.ReportTemplate;
import uk.ac.ebi.spot.gwas.updateefo.repository.EfoTraitRepository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ObsoleteOnlyTask implements Runnable{

    private final int pageNumber;
    private final int olsPageSize;
    private final CountDownLatch latch;
    private final ReportTemplate reportTemplate;
    private final EfoTraitRepository efoTraitRepository;

    public ObsoleteOnlyTask(int pageNumber, int olsBatchSize, EfoTraitRepository efoTraitRepository, CountDownLatch latch, ReportTemplate reportTemplate) {
        this.pageNumber = pageNumber;
        this.olsPageSize = olsBatchSize;
        this.latch = latch;
        this.reportTemplate = reportTemplate;
        this.efoTraitRepository = efoTraitRepository;
    }

    @Override
    public void run() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            List<OlsEfoTrait> olsEfoTraits = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms?obsoletes=true&page=" +
                    pageNumber + "&size=" + olsPageSize, OlsEfoTraitPage.class).get_embedded().getTerms();
            Map<String, OlsEfoTrait> olsEfoTraitMap = olsEfoTraits.stream().collect(Collectors.toMap(OlsEfoTrait::getShortForm, o -> o));
            List<EfoTrait> efoTraits = efoTraitRepository.findByShortFormIn(olsEfoTraits.stream().map(OlsEfoTrait::getShortForm).collect(Collectors.toList()));
            for (EfoTrait efoTrait : efoTraits) {
                try {
                    reportTemplate.addProcessed();
                    String termReplacedBy = olsEfoTraitMap.get(efoTrait.getShortForm()).getTermReplacedBy();
                    if (termReplacedBy != null) {
                        OlsEfoTrait olsEfoTrait;
                        EfoTrait newEfo;
                        if (termReplacedBy.startsWith("http")) {
                            String efoEncodedUri = URLEncoder.encode(termReplacedBy, StandardCharsets.UTF_8.toString());
                            olsEfoTrait = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms/" + efoEncodedUri, OlsEfoTrait.class);
                        }
                        else {
                            olsEfoTrait = restTemplate.getForObject("https://www.ebi.ac.uk/ols4/api/ontologies/efo/terms?short_form=" + termReplacedBy, OlsEfoTraitPage.class).get_embedded().getTerms().get(0);
                        }
                        newEfo = new EfoTrait(efoTrait.getId(), olsEfoTrait.getLabel(), olsEfoTrait.getShortForm(), olsEfoTrait.getIri(), efoTrait.getCreated(), efoTrait.getUpdated());
                        efoTraitRepository.save(newEfo);
                        reportTemplate.addObsolete(efoTrait, newEfo);
                    }
                }
                catch (Exception e) {
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
