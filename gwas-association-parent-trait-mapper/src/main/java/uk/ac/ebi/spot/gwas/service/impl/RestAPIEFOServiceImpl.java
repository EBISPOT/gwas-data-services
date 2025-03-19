package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.ols.Links;
import uk.ac.ebi.spot.gwas.ols.OLSResponse;
import uk.ac.ebi.spot.gwas.ols.OLSTermApiResponse;
import uk.ac.ebi.spot.gwas.ols.TermApi;
import uk.ac.ebi.spot.gwas.service.RestAPIEFOService;
import uk.ac.ebi.spot.gwas.service.RestInteractionService;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RestAPIEFOServiceImpl implements RestAPIEFOService {


    RestInteractionService restInteractionService;


    @Autowired
    public RestAPIEFOServiceImpl(RestInteractionService restInteractionService) {
        this.restInteractionService = restInteractionService;
    }

    public Map<String, String> callOlsRestAPI(String uri, Map<String, String> olsTerms, String efoId) {
        OLSTermApiResponse olsTermApiResponse = restInteractionService.callOlsRestAPI(uri, efoId);
        if(olsTermApiResponse != null ) {
            Map<String, String> olsEFOids = retreiveOlsTerms(olsTermApiResponse);
            if(olsEFOids != null && !olsEFOids.isEmpty()) {
                olsEFOids.forEach((k,v) -> {
                    //log.info("Efo id from OLS Api is {}",k);
                    olsTerms.put(k, v);
                });
            }
            Links links = olsTermApiResponse.getLinks();
            while(links != null && links.getNext() != null) {
                return callOlsRestAPI(links.getNext().getHref(), olsTerms, efoId);
            }
            if(links != null && links.getNext() == null) {
                return olsTerms;
            }
        }
        return null;
        }


    private Map<String, String> retreiveOlsTerms(OLSTermApiResponse olsTermApiResponse) {
        OLSResponse olsResponse = olsTermApiResponse.getOlsResponse();
        if(olsResponse == null) {
            return null;
        } else {
            return  Optional.ofNullable(olsResponse.getTerms())
                    .map(terms -> terms.stream()
                            .collect(Collectors.toMap(TermApi::getShortForm,TermApi::getLabel,
                                    (existing, replacement) -> existing)))
                    .orElse(null);
        }
    }

}
