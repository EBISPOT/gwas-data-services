package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.List;
import java.util.Map;

public interface EFOTraitService {

  Map<String, String> findAllEfoTraits();

  Map<String, List<String>> loadParentChildEfo(List<String> shortForms);

  List<EfoTrait> saveParentEFOMapping(Map<String, List<String>> efoParentChildMap);
}
