package uk.ac.ebi.spot.gwas.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public interface MappingJobSubmitterService {

    public void executePipeline(List<Long> asscnIds, String outDir, String errorDir);

}
