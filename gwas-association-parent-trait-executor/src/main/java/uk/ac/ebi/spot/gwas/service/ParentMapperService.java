package uk.ac.ebi.spot.gwas.service;

import java.util.List;

public interface ParentMapperService {

    void executeParentMapper(String outputDir, String inputDir);

    void executeFileBasedParentMapper(String outputDir, String inputDir, List<String> efoShortForms);
}
