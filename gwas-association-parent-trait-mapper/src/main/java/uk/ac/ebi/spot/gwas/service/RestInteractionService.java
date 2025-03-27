package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.ols.OLSTermApiResponse;

public interface RestInteractionService {

   OLSTermApiResponse callOlsRestAPI(String uri, String efoId, Boolean next);

}
