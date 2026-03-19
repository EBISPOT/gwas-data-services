package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.AncestralGroup;

public interface AncestralGroupService {

    AncestralGroup findByAncestryGroup(String ancestryGroup);
}
