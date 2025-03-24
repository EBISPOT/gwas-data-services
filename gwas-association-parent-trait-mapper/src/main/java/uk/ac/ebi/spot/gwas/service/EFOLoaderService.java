package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.List;

public interface EFOLoaderService {

    void loadAssociationsWithParentEfo(List<EfoTrait> parenEfos);

    void loadStudiesWithParentEfo(List<EfoTrait> parenEfos);
}
