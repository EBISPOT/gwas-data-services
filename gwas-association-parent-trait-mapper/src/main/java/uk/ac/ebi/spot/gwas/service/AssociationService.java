package uk.ac.ebi.spot.gwas.service;

import uk.ac.ebi.spot.gwas.model.EfoTrait;

import java.util.List;

public interface AssociationService {

    void loadAssociationsWithParentEfo(EfoTrait parenEfo);
}
