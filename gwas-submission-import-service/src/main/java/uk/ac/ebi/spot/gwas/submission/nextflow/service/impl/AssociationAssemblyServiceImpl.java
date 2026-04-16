package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AssociationAssemblyService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AssociationCalculationService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.LociAttributesService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SnpService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
public class AssociationAssemblyServiceImpl implements AssociationAssemblyService {

    LociAttributesService lociAttributesService;

    AssociationCalculationService associationCalculationService;

    SnpService snpService;

    public AssociationAssemblyServiceImpl(LociAttributesService lociAttributesService,
                                          AssociationCalculationService associationCalculationService,
                                          SnpService snpService) {
        this.lociAttributesService = lociAttributesService;
        this.associationCalculationService = associationCalculationService;
        this.snpService = snpService;
    }

    public Association assemble(uk.ac.ebi.spot.gwas.deposition.domain.Association mongoAssociation) {
        Association association = new Association();
        association.setSnpInteraction(false);
        Collection<Locus> loci = new ArrayList<>();
        Locus locus = new Locus();
        association.setMultiSnpHaplotype(false);
        association.setSnpType("novel");
        locus.setDescription("Single variant");
        if(mongoAssociation.getStandardError() != null) {
            association.setStandardError(mongoAssociation.getStandardError().floatValue());
        }
        String pValue = mongoAssociation.getPvalue();
        if (pValue != null && pValue.toLowerCase().contains("e")) {
            String[] pValues = pValue.toLowerCase().split("e");
            int exponent = Integer.valueOf(pValues[1]);

            int mantissa = (int) Math.round(Double.valueOf(pValues[0]));
            if (mantissa == 10) {
                mantissa = 1;
                exponent = exponent + 1;
            }
            association.setPvalueExponent(exponent);
            association.setPvalueMantissa(mantissa);
        }
        association.setPvalueDescription(mongoAssociation.getPvalueText());
        String rsId = mongoAssociation.getVariantId();
        if(StringUtils.isNotBlank(rsId)) {

            SingleNucleotidePolymorphism snp = lociAttributesService.createSnp(rsId);
            log.info("Creating Snp {}.", rsId);
            RiskAllele riskAllele = lociAttributesService.createRiskAllele(String.format("%s-%s", rsId, mongoAssociation.getEffectAllele()), snp);
            log.info("Creating Risk Allelle {}", riskAllele.getRiskAlleleName());
            if(StringUtils.isNotBlank(mongoAssociation.getProxyVariant())) {
                List<SingleNucleotidePolymorphism> proxySnps = new ArrayList<>();
                proxySnps.add(lociAttributesService.createSnp(mongoAssociation.getProxyVariant()));
                riskAllele.setProxySnps(proxySnps);
            }
            List<RiskAllele> alleleList = new ArrayList<>();
            alleleList.add(riskAllele);
            locus.setStrongestRiskAlleles(alleleList);
            loci.add(locus);
            association.setLoci(loci);
        } else {
            log.error("no Rsid provided");
        }
        if(mongoAssociation.getEffectAlleleFrequency() != null && mongoAssociation.getEffectAlleleFrequency().intValue() != -1)  {
            association.setRiskFrequency(mongoAssociation.getEffectAlleleFrequency().toString());
        } else {
            association.setRiskFrequency("NR");
        }
        if(mongoAssociation.getStandardError() != null ) {
            association.setStandardError(mongoAssociation.getStandardError().floatValue());
        }
        if(mongoAssociation.getOddsRatio() != null ) {
            association.setOrPerCopyNum(mongoAssociation.getOddsRatio().floatValue());
        }
        if(mongoAssociation.getBeta() != null) {
            Double betaValue = mongoAssociation.getBeta();
            if (betaValue < 0) {
                association.setBetaDirection("decrease");
            } else {
                association.setBetaDirection("increase");
            }
            association.setBetaNum(Math.abs(betaValue.floatValue()));
            association.setBetaUnit(mongoAssociation.getBetaUnit());
        }
        if(mongoAssociation.getCiLower() != null && mongoAssociation.getCiUpper() != null) {
            association.setRange(String.format("[%s-%s]", mongoAssociation.getCiLower(), mongoAssociation.getCiUpper()));
        } else {
            if(mongoAssociation.getOddsRatio() != null && mongoAssociation.getStandardError() != null) {
                association.setRange(associationCalculationService.setRange(mongoAssociation.getStandardError(),
                        Math.abs(mongoAssociation.getOddsRatio())));
            } else if(mongoAssociation.getBeta() != null && mongoAssociation.getStandardError() != null) {
                association.setRange(associationCalculationService.setRange(mongoAssociation.getStandardError(),
                        Math.abs(mongoAssociation.getBeta())));
            }
        }

        return association;
    }

    public AssociationExtension assembleAssociationExtension(uk.ac.ebi.spot.gwas.deposition.domain.Association mongoAssociation) {
        AssociationExtension associationExtension = new AssociationExtension();
        associationExtension.setEffectAllele(mongoAssociation.getEffectAllele());
        if(StringUtils.isNotBlank(mongoAssociation.getOtherAllele())) {
            associationExtension.setOtherAllele(mongoAssociation.getOtherAllele());
        }
        return associationExtension;
    }
}
