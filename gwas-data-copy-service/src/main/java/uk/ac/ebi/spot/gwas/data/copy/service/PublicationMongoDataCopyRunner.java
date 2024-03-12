package uk.ac.ebi.spot.gwas.data.copy.service;

import java.util.List;

public interface PublicationMongoDataCopyRunner {

    void executeRunner(List<Long> publications, String errorDir, String outputDir, String executorPool) ;
}
