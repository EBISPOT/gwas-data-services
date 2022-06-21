package uk.ac.ebi.spot.gwas.genomic_context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.common.model.*;
import uk.ac.ebi.spot.gwas.common.util.MappingUtil;
import uk.ac.ebi.spot.gwas.mapping.dto.EnsemblMappingResult;
import uk.ac.ebi.spot.gwas.mapping.dto.MappingDto;
import uk.ac.ebi.spot.gwas.overlap_gene.OverlapGene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GenomicContextService {

    @Value("${mapping.ensembl_source}")
    private String ensemblSource;

    @Value("${mapping.ncbi_source}")
    private String ncbiSource;

    @Value("${mapping.method}")
    private String mappingMethod;


    public MappingDto add(List<OverlapGene> geneList,
                          Location snpLocation,
                          String source,
                          String type,
                          EnsemblMappingResult mappingResult) {
        String closestGene = "";
        int closestDistance = 0;
        boolean intergenic = !type.equals("overlap");
        boolean upstream = type.equals("upstream");
        boolean downstream = type.equals("downstream");

        Integer position = snpLocation.getChromosomePosition();

        SingleNucleotidePolymorphism snpTmp = new SingleNucleotidePolymorphism();
        snpTmp.setRsId(mappingResult.getRsId());
        if (mappingResult.getRsId() == null) {
            throw new IllegalArgumentException("error, no RS ID found for location " + snpLocation.getId());
        }

        List<GenomicContext> genomicContexts = new ArrayList<>();

        // Get closest gene
        if (intergenic) {
            int pos = position;

            for (OverlapGene gene : geneList) {
                String geneName = gene.getExternalName();

                // If the source is NCBI, we parse the ID from the description:
                String geneId = source.equals(ncbiSource) ? MappingUtil.parseNCBIid(gene.getDescription(), geneName) : gene.getId();

                // Skip overlapping genes which also overlap upstream and/or downstream of the variant
                if (source.equals(ncbiSource)) {
                    if (geneName == null || mappingResult.getNcbiOverlappingGene().contains(geneName)) {
                        continue;
                    }
                } else {
                    if (geneName == null || mappingResult.getEnsemblOverlappingGene().contains(geneName)) {
                        continue;
                    }
                }

                int distance = 0;
                if (type.equals("upstream")) {
                    distance = pos - gene.getEnd();
                } else if (type.equals("downstream")) {
                    distance = gene.getStart() - pos;
                }

                if ((distance < closestDistance && distance > 0) || closestDistance == 0) {
                    closestGene = geneId;
                    closestDistance = distance;
                }
            }
        }

        for (OverlapGene gene : geneList) {
            String geneName = gene.getExternalName();

            // If the source is NCBI, we parse the ID from the description:
            String geneId = source.equals(ncbiSource) ? MappingUtil.parseNCBIid(gene.getDescription(), geneName) : gene.getId();
            String ncbiId = (source.equals(ncbiSource)) ? geneId : null;
            String ensemblId = (source.equals(ensemblSource)) ? geneId : null;
            int distance = 0;

            // Skip overlapping genes which also overlap upstream and/or downstream of the variant
            if (intergenic) {
                if (source.equals(ncbiSource)) {
                    if (geneName == null || mappingResult.getNcbiOverlappingGene().contains(geneName)) {
                        continue;
                    }
                } else {
                    if (geneName == null || mappingResult.getEnsemblOverlappingGene().contains(geneName)) {
                        continue;
                    }
                }

                int pos = position;
                if (type.equals("upstream")) {
                    distance = pos - gene.getEnd();
                } else if (type.equals("downstream")) {
                    distance = gene.getStart() - pos;
                }
            }
            Long dist = (long) distance;

            EntrezGene entrezGene = new EntrezGene();
            entrezGene.setEntrezGeneId(ncbiId);
            Collection<EntrezGene> entrezGenes = new ArrayList<>();
            entrezGenes.add(entrezGene);

            EnsemblGene ensemblGene = new EnsemblGene();
            ensemblGene.setEnsemblGeneId(ensemblId);
            Collection<EnsemblGene> ensemblGenes = new ArrayList<>();
            ensemblGenes.add(ensemblGene);

            // Check if the gene corresponds to the closest gene
            Gene geneObject = new Gene(geneName, entrezGenes, ensemblGenes);
            boolean isClosestGene = closestGene.equals(geneId) && !closestGene.equals("");
            genomicContexts.add(new GenomicContext(intergenic, upstream,
                    downstream, dist,
                    snpTmp, geneObject,
                    snpLocation, source,
                    mappingMethod, isClosestGene));
        }

        boolean closestFound = !closestGene.equals("");

        return MappingDto.builder()
                .closestFound(closestFound)
                .genomicContexts(genomicContexts).build();
    }

}
