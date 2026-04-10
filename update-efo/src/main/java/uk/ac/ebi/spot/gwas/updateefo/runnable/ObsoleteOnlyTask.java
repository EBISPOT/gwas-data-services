package uk.ac.ebi.spot.gwas.updateefo.runnable;

import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.spot.gwas.updateefo.domain.EfoTrait;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTrait;
import uk.ac.ebi.spot.gwas.updateefo.dto.OlsEfoTraitPage;
import uk.ac.ebi.spot.gwas.updateefo.report.ReportTemplate;
import uk.ac.ebi.spot.gwas.updateefo.repository.EfoTraitRepository;
import org.springframework.jdbc.core.JdbcTemplate;

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
    private final JdbcTemplate jdbcTemplate;

    public ObsoleteOnlyTask(int pageNumber, int olsBatchSize, EfoTraitRepository efoTraitRepository, CountDownLatch latch, ReportTemplate reportTemplate, JdbcTemplate jdbcTemplate) {
        this.pageNumber = pageNumber;
        this.olsPageSize = olsBatchSize;
        this.latch = latch;
        this.reportTemplate = reportTemplate;
        this.efoTraitRepository = efoTraitRepository;
        this.jdbcTemplate = jdbcTemplate;
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
                        jdbcTemplate.update("UPDATE EFO_TRAIT SET trait = ?, short_form = ?, uri = ? WHERE short_form = ?",
                                newEfo.getTrait(), newEfo.getShortForm(), newEfo.getUri(), efoTrait.getShortForm());
                        reportTemplate.addObsolete(efoTrait, newEfo);
                    }
                    else
                        reportTemplate.addError(new RuntimeException("No TermReplacedBy"), efoTrait.getShortForm() + "(termReplacedBy=null)");
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
