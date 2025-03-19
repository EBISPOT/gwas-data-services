package uk.ac.ebi.spot.gwas.service;

import java.util.List;
import java.util.Map;

public interface EFOTraitService {

  Map<String, String> findAllEfoTraits();

  Map<String, List<String>> loadParentChildEfo(List<String> shortForms);

  void saveParentEFOMapping(Map<String, List<String>> efoParentChildMap);
}
