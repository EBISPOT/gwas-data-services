package uk.ac.ebi.spot.gwas.service;

import java.util.List;

public interface ParentMapperService {

    void executeParentMapper(String outputDir, String inputDir, String executionMode);

    void executeFileBasedParentMapper(String outputDir, String inputDir, List<String> efoShortForms, String executionMode);

    void executeLargeEFOParentMapper(String outputDir, String inputDir, String executionMode);
}
