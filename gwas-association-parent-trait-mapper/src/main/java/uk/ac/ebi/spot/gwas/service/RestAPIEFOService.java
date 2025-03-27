package uk.ac.ebi.spot.gwas.service;

import java.util.Map;

public interface RestAPIEFOService {

    Map<String, String> callOlsRestAPI(String uri, Map<String, String> olsTerms, String efoId, Boolean next) ;
}
