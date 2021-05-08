package uk.ac.ebi.spot.gwas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MappingDto {

    private Boolean closestFound;
    private Set<String> geneNames;

    private List<String> snpRsIds;
    private List<String> reportedGenes;

    Collection<String> associationPipelineErrors;

    Integer threadSize;
    Integer batchSize;
    Integer totalPagesToMap;


}
