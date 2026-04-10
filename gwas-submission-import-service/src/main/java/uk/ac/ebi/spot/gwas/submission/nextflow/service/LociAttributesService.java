package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Locus;
import uk.ac.ebi.spot.gwas.model.RiskAllele;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;

import java.util.Collection;
import java.util.List;

public interface LociAttributesService {

    SingleNucleotidePolymorphism createSnp(String curatorEnteredSnp);

    RiskAllele createRiskAllele(String curatorEnteredRiskAllele, SingleNucleotidePolymorphism snp);

    List<Locus> saveLocusAttributes(Collection<Locus> loci);
}
