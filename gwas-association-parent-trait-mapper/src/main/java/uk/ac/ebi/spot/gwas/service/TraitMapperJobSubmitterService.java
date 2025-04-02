package uk.ac.ebi.spot.gwas.service;

import java.util.List;

public interface TraitMapperJobSubmitterService {

    void executePipeline(List<String> shortForms, String outDir, String errorDir, String executorPool, String parentEfo);
}
