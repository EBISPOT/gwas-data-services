package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.model.GenomicContext;
import uk.ac.ebi.spot.gwas.model.Location;
import uk.ac.ebi.spot.gwas.model.SingleNucleotidePolymorphism;
import uk.ac.ebi.spot.gwas.repository.GeneRepository;
import uk.ac.ebi.spot.gwas.repository.GenomicContextRepository;

@Service
public class GenomicContextCreationService {

    @Autowired
    private GeneRepository geneRepository;
    @Autowired
    private GenomicContextRepository genomicContextRepository;

    public GenomicContext createGenomicContext(Boolean isIntergenic,
                                               Boolean isUpstream,
                                               Boolean isDownstream,
                                               Long distance,
                                               String source,
                                               String mappingMethod,
                                               String geneName,
                                               SingleNucleotidePolymorphism snpIdInDatabase,
                                               Boolean isClosestGene, Location location) {

        GenomicContext genomicContext = new GenomicContext();
        Gene gene = geneRepository.findByGeneName(geneName);
        genomicContext.setGene(gene);
        genomicContext.setIsIntergenic(isIntergenic);
        genomicContext.setIsDownstream(isDownstream);
        genomicContext.setIsUpstream(isUpstream);
        genomicContext.setDistance(distance);
        genomicContext.setSource(source);
        genomicContext.setMappingMethod(mappingMethod);
        genomicContext.setSnp(snpIdInDatabase);
        genomicContext.setIsClosestGene(isClosestGene);
        genomicContext.setLocation(location);

        // Save genomic context
        genomicContextRepository.save(genomicContext);

        return genomicContext;
    }

}
