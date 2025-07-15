package uk.ac.ebi.spot.gwas.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.model.Gene;

import java.util.List;

public interface AssociationService {


    Page<Association> findAssociationByShortForm(String shortForm, Pageable pageable);

    void updateAssociationMappingGenes(List<Long> associationIds);



}
