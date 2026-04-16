package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.DiseaseTrait;
import uk.ac.ebi.spot.gwas.deposition.domain.EfoTrait;
import uk.ac.ebi.spot.gwas.model.*;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StudyAssemblyServiceImpl implements StudyAssemblyService {

    DiseaseTraitService diseaseTraitService;

    PlatformService platformService;

    GenotypingTechnologyService genotypingTechnologyService;

    HousekeepingService housekeepingService;

    EfoTraitService efoTraitService;


    public StudyAssemblyServiceImpl(DiseaseTraitService diseaseTraitService,
                                    PlatformService platformService,
                                    GenotypingTechnologyService genotypingTechnologyService,
                                    HousekeepingService housekeepingService,
                                    EfoTraitService efoTraitService
                                    ) {
        this.diseaseTraitService = diseaseTraitService;
        this.platformService = platformService;
        this.genotypingTechnologyService = genotypingTechnologyService;
        this.housekeepingService = housekeepingService;
        this.efoTraitService = efoTraitService;
    }

    public Study assemble(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy) {
        Study study = new Study();
        study.setAccessionId(mongoStudy.getAccession());
        study.setStudyTag(mongoStudy.getStudyTag());
        study.setImputed(mongoStudy.getImputation());
        study.setAgreedToCc0(mongoStudy.isAgreedToCc0());
        String traitId = mongoStudy.getDiseaseTrait();
        if(traitId != null) {
            DiseaseTrait mongoDiseaseTrait = diseaseTraitService.getMongoDiseaseTrait(traitId);
            uk.ac.ebi.spot.gwas.model.DiseaseTrait diseaseTrait = diseaseTraitService.getDiseaseTrait(mongoDiseaseTrait.getTrait());
            Optional.ofNullable(diseaseTrait).ifPresent(study::setDiseaseTrait);
        }
        log.info("Exiting DiseaseTrait block");
        String manufacturerString = mongoStudy.getArrayManufacturer();

        if(manufacturerString != null) {
            List<Platform> platformList = new ArrayList<>();
            String[] manufacturers = manufacturerString.split("\\||,");
            for (String manufacturer : manufacturers) {
                Platform platform = platformService.findByManufacturer(manufacturer.trim());
                if(platform != null) {
                    platformList.add(platform);
                }
            }
            study.setPlatforms(platformList);
        }

        String genoTypeTechnologiesString = mongoStudy.getGenotypingTechnology();
        if(genoTypeTechnologiesString != null) {
            List<GenotypingTechnology> technologyList = new ArrayList<>();
            String[]  genoTypeTechnologies = genoTypeTechnologiesString.split("\\||,");
            for (String genoTypeTechnology : genoTypeTechnologies) {
                log.info("Genotyping technology is {}", genoTypeTechnology);
                GenotypingTechnology genotypingTechnology = genotypingTechnologyService.findByGenotypingTechnology(this.transformGenotypingTechnology(genoTypeTechnology).trim());
                if(genotypingTechnology != null) {
                    technologyList.add(genotypingTechnology);
                }
            }
            study.setGenotypingTechnologies(technologyList);
        }
        Housekeeping housekeeping = housekeepingService.createHousekeeping();
        study.setHousekeeping(housekeeping);
        List<uk.ac.ebi.spot.gwas.model.EfoTrait> efoTraits =  Optional.ofNullable(mongoStudy.getEfoTraits())
                .map(efoList ->  efoList.stream()
                        .map(efoId -> efoTraitService.findByMongoId(efoId))
                        .filter(Objects::nonNull)
                        .map(EfoTrait::getShortForm)
                        .map(shortForm -> efoTraitService.findByShortForm(shortForm))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())).orElse(null);
        List<uk.ac.ebi.spot.gwas.model.EfoTrait> bgEfoTraits =  Optional.ofNullable(mongoStudy.getBackgroundEfoTraits())
                .map(efoList ->  efoList.stream()
                        .map(efoId -> efoTraitService.findByMongoId(efoId))
                        .filter(Objects::nonNull)
                        .map(EfoTrait::getShortForm)
                        .map(shortForm -> efoTraitService.findByShortForm(shortForm))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())).orElse(null);
        study.setEfoTraits(efoTraits);
        study.setMappedBackgroundTraits(bgEfoTraits);
        String bgTrait = mongoStudy.getBackgroundTrait();
        if(bgTrait != null) {
            DiseaseTrait bgDiseaseTrait = diseaseTraitService.getMongoDiseaseTraitByTrait(bgTrait);
            Optional.ofNullable(diseaseTraitService.getDiseaseTrait(bgDiseaseTrait.getTrait())).ifPresent(study::setBackgroundTrait);
        }
        if(mongoStudy.getVariantCount() != -1) {
            study.setSnpCount(mongoStudy.getVariantCount());
        }
        if(mongoStudy.getSummaryStatisticsFile() != null && !mongoStudy.getSummaryStatisticsFile().isEmpty() &&
                !mongoStudy.getSummaryStatisticsFile().equals("NR")) {
            study.setFullPvalueSet(true);
        }
        study.setStudyDesignComment(mongoStudy.getArrayInformation());
        study.setPooled(mongoStudy.getPooledFlag());
        study.setGxe(mongoStudy.getGxeFlag());
        study.setInitialSampleSize(mongoStudy.getInitialSampleDescription());
        study.setReplicateSampleSize(mongoStudy.getReplicateSampleDescription());
        return study;
    }


    public StudyExtension assembleStudyExtension(uk.ac.ebi.spot.gwas.deposition.domain.Study mongoStudy) {
        StudyExtension studyExtension = new StudyExtension();
        studyExtension.setStudyDescription(mongoStudy.getStudyDescription());
        studyExtension.setCohort(mongoStudy.getCohort());
        studyExtension.setCohortSpecificReference(mongoStudy.getCohortId());
        studyExtension.setStatisticalModel(mongoStudy.getStatisticalModel());
        studyExtension.setSummaryStatisticsFile(mongoStudy.getSummaryStatisticsFile());
        studyExtension.setSummaryStatisticsAssembly(mongoStudy.getSummaryStatisticsAssembly());
        return studyExtension;
    }


    private String transformGenotypingTechnology(String input) {
        if (input.equalsIgnoreCase("Whole genome sequencing")) {
            return "Genome-wide sequencing";
        }
        return input;
    }
}
