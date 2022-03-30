package uk.ac.ebi.spot.gwas.mapping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.ac.ebi.spot.gwas.common.model.GenomicContext;
import uk.ac.ebi.spot.gwas.common.model.Location;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingDto {

    private List<GenomicContext> genomicContexts;
    private Boolean closestFound;
    private Set<String> geneNames;

    private List<String> snpRsIds;
    private List<String> reportedGenes;

    Map<String, Set<Location>> snpToLocationsMap;
    Collection<GenomicContext> allGenomicContexts;
    Collection<String> associationPipelineErrors;

    Integer threadSize;
    Integer batchSize;
    Integer totalPagesToMap;


}
