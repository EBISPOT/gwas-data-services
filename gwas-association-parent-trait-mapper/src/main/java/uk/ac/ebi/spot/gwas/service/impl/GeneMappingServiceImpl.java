package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.AssociationRepository;
import uk.ac.ebi.spot.gwas.repository.GeneRepository;
import uk.ac.ebi.spot.gwas.rest.projection.AssociationGeneProjection;
import uk.ac.ebi.spot.gwas.rest.projection.SnpGeneProjection;
import uk.ac.ebi.spot.gwas.service.AssociationUpdateService;
import uk.ac.ebi.spot.gwas.service.GeneMappingService;
import uk.ac.ebi.spot.gwas.service.GeneRetrieveService;
import uk.ac.ebi.spot.gwas.service.SnpUpdateService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GeneMappingServiceImpl implements GeneMappingService {

    AssociationRepository associationRepository;

    GeneRepository geneRepository;

    AssociationUpdateService associationUpdateService;

    GeneRetrieveService geneRetrieveService;

    SnpUpdateService snpUpdateService;


    @Autowired
    public GeneMappingServiceImpl(AssociationRepository associationRepository,
                                  GeneRepository geneRepository,
                                  AssociationUpdateService associationUpdateService,
                                  GeneRetrieveService geneRetrieveService,
                                  SnpUpdateService snpUpdateService) {
        this.associationRepository = associationRepository;
        this.geneRepository = geneRepository;
        this.associationUpdateService = associationUpdateService;
        this.geneRetrieveService = geneRetrieveService;
        this.snpUpdateService = snpUpdateService;
    }


    public void updateGeneMapping(List<AssociationGeneProjection> associationGeneProjections) {
        Map<Long, List<AssociationGeneProjection>> associationGeneMap = associationGeneProjections
                .stream()
                .collect(Collectors.groupingBy(AssociationGeneProjection::getAssociationId));
        associationGeneMap.keySet().forEach(associationId -> {
            //log.info("Updating gene mapping for association {}", associationId);
            List<Long> mappeGeneIds = associationGeneMap.get(associationId).stream()
                    .map(AssociationGeneProjection::getGeneId)
                    .collect(Collectors.toList());
            //mappeGeneIds.forEach(geneId -> log.info("Gene ID {}", geneId));
            List<Gene> mappedGenes = geneRetrieveService.findGenesByIds(mappeGeneIds);
            //mappedGenes.forEach(gene -> log.info("Mapped gene {}", gene.getGeneName()));
            associationUpdateService.updateAssociationMappedGene(associationId, mappedGenes);
        });

    }

    public void updateSnpGeneMapping(List<SnpGeneProjection> snpGeneProjections) {

        Map<Long, List<SnpGeneProjection>> snpGeneMap = snpGeneProjections.
                stream()
                .collect(Collectors.groupingBy(SnpGeneProjection::getSnpId));
        snpGeneMap.keySet().forEach(snpId -> {
            List<Long> mappedGeneIds = snpGeneMap.get(snpId).stream()
                    .map(SnpGeneProjection::getGeneId)
                    .collect(Collectors.toList());
            List<Gene> mappedGenes = geneRetrieveService.findGenesByIds(mappedGeneIds);
            snpUpdateService.updateSnpMappedGene(snpId, mappedGenes);
        });
    }
}
