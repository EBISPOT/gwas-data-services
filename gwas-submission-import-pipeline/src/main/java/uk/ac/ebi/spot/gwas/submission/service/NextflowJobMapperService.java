package uk.ac.ebi.spot.gwas.submission.service;

import java.util.List;

public interface NextflowJobMapperService {

    void writeJobMapFile(List<String> studyIds);

}
