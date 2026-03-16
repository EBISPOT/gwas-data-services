package uk.ac.ebi.spot.gwas.service.impl;

import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.service.MappingJobSubmitterService;
import uk.ac.ebi.spot.gwas.service.StudiesService;

import java.util.List;

@Service
public class StudiesServiceImpl implements StudiesService {

    StudyRepository studyRepository;

    MappingJobSubmitterService mappingJobSubmitterService;

    public StudiesServiceImpl(StudyRepository studyRepository,
                              MappingJobSubmitterService mappingJobSubmitterService) {
        this.studyRepository = studyRepository;
        this.mappingJobSubmitterService = mappingJobSubmitterService;
    }

    public void publishStudiesForPmid(String pubmedId, String submissionId,  String outputDir, String errorDir, String mode) {
       List<Long> studyIds = studyRepository.findStudiesByPmid(pubmedId);
        int executorIndex = 0;
        int partitionSize = 1000;
        if(mode.equals("approve-snps") || mode.equals("publish-studies")) {
            partitionSize = 100000;
        }
        for(List<Long> partStudyIds : ListUtils.partition(studyIds, partitionSize)) {
            mappingJobSubmitterService.executePipeline(partStudyIds, outputDir, errorDir, "executor-"+executorIndex, mode, submissionId);
            executorIndex++;
        }
    }
}
