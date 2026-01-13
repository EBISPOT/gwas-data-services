package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.model.Ancestry;
import uk.ac.ebi.spot.gwas.model.AncestryExtension;

public interface SampleAssemblyService {

    Ancestry assemble(Sample sample);

    AncestryExtension assembleAncestryExtension(Sample sample);
}
