package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.List;

public interface EfoTraitRetrieveService {

     List<EfoTrait> findByShortForms(List<String> shortForms);

     EfoTrait findByShortForm(String shortForm);
}
