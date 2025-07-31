package uk.ac.ebi.spot.gwas.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.association.Association;
import uk.ac.ebi.spot.gwas.common.model.Locus;
import uk.ac.ebi.spot.gwas.common.model.RiskAllele;
import uk.ac.ebi.spot.gwas.common.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.common.projection.SnpGeneProjection;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SnpGeneMappingService {

    @Autowired
    SingleNucleotidePolymorphismQueryService singleNucleotidePolymorphismQueryService;

    @Autowired
    SnpGenomicContextMappingService snpGenomicContextMappingService;

    @Transactional(propagation = Propagation.SUPPORTS)
    public void updateSnpMappingGenes(Set<String> rsIds) {

        rsIds.forEach(rsId -> {
            SingleNucleotidePolymorphism snp = singleNucleotidePolymorphismQueryService.getSnp(rsId);
                if (snp != null) {
                    log.info("SnpId to be mapped with gene is is {}", snp.getRsId());
                    List<SnpGeneProjection> snpGeneProjections = singleNucleotidePolymorphismQueryService.findOverLappingGenes(snp.getId(), "Ensembl");
                    if (snpGeneProjections.isEmpty()) {
                        snpGeneProjections = singleNucleotidePolymorphismQueryService.findUpDownStreamGenes(snp.getId(), "Ensembl");
                    }
                    snpGenomicContextMappingService.updateSnpGeneMapping(snpGeneProjections);
                }
            });
        }
    }

