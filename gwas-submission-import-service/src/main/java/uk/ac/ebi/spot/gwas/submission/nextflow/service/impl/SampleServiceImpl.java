package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.model.Ancestry;
import uk.ac.ebi.spot.gwas.model.AncestryExtension;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository.SampleMongoRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.AncestryExtensionRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.AncestryRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SampleAssemblyService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SampleService;

import java.util.List;
@Slf4j
@Service
public class SampleServiceImpl implements SampleService {

    SampleMongoRepository sampleMongoRepository;

    SampleAssemblyService sampleAssemblyService;

    AncestryRepository ancestryRepository;

    AncestryExtensionRepository ancestryExtensionRepository;


    public SampleServiceImpl(SampleMongoRepository sampleMongoRepository,
                             SampleAssemblyService sampleAssemblyService,
                             AncestryRepository ancestryRepository,
                             AncestryExtensionRepository ancestryExtensionRepository) {
        this.sampleMongoRepository = sampleMongoRepository;
        this.sampleAssemblyService = sampleAssemblyService;
        this.ancestryRepository = ancestryRepository;
        this.ancestryExtensionRepository = ancestryExtensionRepository;
    }

    public List<Sample> getSamples(String submissionId, String studyTag) {
        return sampleMongoRepository.findBySubmissionIdAndStudyTagIgnoreCase(submissionId, studyTag);
   }

   public void saveSamples(List<Sample> samples, Study study) {
        for (Sample mongoSample : samples) {
            log.info("Inside Sample {}", mongoSample.getAncestry());
            Ancestry ancestry = sampleAssemblyService.assemble(mongoSample);
            ancestry.setStudy(study);
            ancestryRepository.save(ancestry);
            AncestryExtension ancestryExtension = sampleAssemblyService.assembleAncestryExtension(mongoSample);
            ancestryExtension.setAncestry(ancestry);
            ancestryExtensionRepository.save(ancestryExtension);
            ancestry.setAncestryExtension(ancestryExtension);
            ancestryRepository.save(ancestry);
        }
   }
}
