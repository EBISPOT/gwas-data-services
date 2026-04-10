package uk.ac.ebi.spot.gwas.submission.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.annotation.nextflow.dto.JobMapperDTO;
import uk.ac.ebi.spot.gwas.annotation.nextflow.dto.NextflowJobDTO;
import uk.ac.ebi.spot.gwas.submission.config.NextFlowJobConfig;
import uk.ac.ebi.spot.gwas.submission.service.NextflowJobMapperService;
import uk.ac.ebi.spot.gwas.submission.util.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NextflowJobMapperServiceImpl implements NextflowJobMapperService {

    NextFlowJobConfig nextFlowJobConfig;

    FileHandler fileHandler;

    public NextflowJobMapperServiceImpl(NextFlowJobConfig nextFlowJobConfig,
                                        FileHandler fileHandler) {
        this.nextFlowJobConfig = nextFlowJobConfig;
        this.fileHandler = fileHandler;
    }

    public void writeJobMapFile(List<NextflowJobDTO> nextflowJobDTOs, String pmid, String submissionId) {
        log.info("Inside writeJobMapFile()");
        List<JobMapperDTO> jobMapperDTOS =  nextflowJobDTOs.stream().map(nextflowJobDTO -> {
                log.info(String.format("%s,%s %s %s %s %s %s", nextflowJobDTO.getStudyIds().split("_")[0], nextFlowJobConfig.getNextflowJobCommand(), nextflowJobDTO.getSubmissionId(),
                        nextflowJobDTO.getPmid(), nextflowJobDTO.getCuratorEmail(), nextflowJobDTO.getSubmissionType(), nextflowJobDTO.getStudyIds()));

                return new JobMapperDTO(nextflowJobDTO.getStudyIds().split("_")[0], String.format("%s %s %s %s %s %s", nextFlowJobConfig.getNextflowJobCommand(), nextflowJobDTO.getSubmissionId(),
                        nextflowJobDTO.getPmid(), nextflowJobDTO.getCuratorEmail(), nextflowJobDTO.getSubmissionType(), nextflowJobDTO.getStudyIds()));
        }).collect(Collectors.toList());
        //String jobMapperFile = String.format("%s/%s/job_map_%s.csv", nextFlowJobConfig.getSlurmLogsLocation(), submissionId, pmid);
        String jobMapperFile = String.format("%s/job_map_%s.csv", nextFlowJobConfig.getSlurmLogsLocation(), pmid);
        try {
            fileHandler.writeToCsv(jobMapperDTOS, jobMapperFile);
        } catch(Exception ex) {
            log.error("Inside IOException" +ex.getMessage(),ex);
        }
    }

}
