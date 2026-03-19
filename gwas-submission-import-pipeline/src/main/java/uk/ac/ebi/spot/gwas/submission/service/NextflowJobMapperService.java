package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.annotation.nextflow.dto.NextflowJobDTO;

import java.util.List;

public interface NextflowJobMapperService {

    void writeJobMapFile(List<NextflowJobDTO> nextflowJobDTOs, String pmid, String submissionId) ;

}
