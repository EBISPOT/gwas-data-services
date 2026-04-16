package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Association;
import uk.ac.ebi.spot.gwas.model.Locus;
import uk.ac.ebi.spot.gwas.model.RiskAllele;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.LocusRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.LociAttributesService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.RiskAlleleService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SnpService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class LociAttributesServiceImpl implements LociAttributesService {


    SnpService snpService;

    RiskAlleleService riskAlleleService;

    LocusRepository locusRepository;


    public LociAttributesServiceImpl(SnpService snpService,
                                     RiskAlleleService riskAlleleService,
                                     LocusRepository locusRepository) {
        this.snpService = snpService;
        this.riskAlleleService = riskAlleleService;
        this.locusRepository = locusRepository;
    }

    public SingleNucleotidePolymorphism createSnp(String curatorEnteredSnp) {
            if(snpService.getSnp(curatorEnteredSnp) != null ) {
                log.info("Inside existing Snp block {}",curatorEnteredSnp);
                return snpService.getSnp(curatorEnteredSnp);
            } else {
                SingleNucleotidePolymorphism newSnp = new SingleNucleotidePolymorphism();
                newSnp.setRsId(tidyCuratorEnteredString(curatorEnteredSnp));
                return newSnp;
            }
     }


     public RiskAllele createRiskAllele(String curatorEnteredRiskAllele,SingleNucleotidePolymorphism snp) {
         RiskAllele newRiskAllele = new RiskAllele();
         newRiskAllele.setRiskAlleleName(tidyCuratorEnteredString(curatorEnteredRiskAllele));
         newRiskAllele.setSnp(snp);
         return newRiskAllele;
     }

    private String tidyCuratorEnteredString(String anyString) {
         String newString = anyString.trim();
         String newLine = System.lineSeparator();
         if(newString.contains(newLine)) {
             newString = newString.replace(newLine, "");
         }
         if(newString.startsWith("Rs")) {
             newString = newString.toLowerCase();
         }
         return newString;
    }

    public List<Locus> saveLocusAttributes(Collection<Locus> loci) {
        List<Locus> savedLoci = new ArrayList<>();
        loci.forEach(locus -> {
            List<RiskAllele> strongRiskAlleles = this.saveRiskAlleles(locus.getStrongestRiskAlleles());
            locus.setStrongestRiskAlleles(strongRiskAlleles);
            savedLoci.add(locusRepository.save(locus));
        });
        return savedLoci;
    }

    public List<RiskAllele> saveRiskAlleles(Collection<RiskAllele> strongestRiskAlleles) {
        List<RiskAllele> alleles = new ArrayList<>();
        strongestRiskAlleles.forEach(riskAllele ->  {
            SingleNucleotidePolymorphism snp = snpService.saveSnp(riskAllele.getSnp());
            riskAllele.setSnp(snp);
            if(riskAllele.getProxySnps() != null && !riskAllele.getProxySnps().isEmpty()) {
                riskAllele.getProxySnps().forEach(proxySnp -> snpService.saveSnp(proxySnp));
            }
            alleles.add(riskAlleleService.saveRiskAllele(riskAllele));
        });
        return alleles;
    }



}
