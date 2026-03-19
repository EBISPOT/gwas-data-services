package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.deposition.domain.Sample;
import uk.ac.ebi.spot.gwas.model.AncestralGroup;
import uk.ac.ebi.spot.gwas.model.Ancestry;
import uk.ac.ebi.spot.gwas.model.AncestryExtension;
import uk.ac.ebi.spot.gwas.model.Country;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AncestralGroupService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.CountryService;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.SampleAssemblyService;

import java.util.*;

@Slf4j
@Service
public class SampleAssemblyServiceImpl implements SampleAssemblyService {

    CountryService countryService;

    AncestralGroupService ancestralGroupService;

    public SampleAssemblyServiceImpl(CountryService countryService,
                                     AncestralGroupService ancestralGroupService) {
        this.countryService = countryService;
        this.ancestralGroupService = ancestralGroupService;
    }

    public Ancestry assemble(Sample sample) {
        Ancestry ancestry = new Ancestry();
        if (sample.getStage().equalsIgnoreCase("Discovery")) {
            ancestry.setType("initial");
        } else if (sample.getStage().equalsIgnoreCase("Replication")) {
            ancestry.setType("replication");
        } else {
            log.error("Unknown Ancestry type: " + sample.getStage());
        }
        List<Country> countryList = new ArrayList<>();
        String countryOfRecruitment = sample.getCountryRecruitement();
        log.info("countryOfRecruitment is {}", countryOfRecruitment);
        if (countryOfRecruitment != null) {
            String[] countries = countryOfRecruitment.split("\\|");
            for (String country : countries) {
                //log.info("country is {}", country);
                Country country1 = countryService.findByCountryOfRecruitement(country);
                //log.info("country1 is {}", country1.getCountryName());
                if (country1 != null) {
                    countryList.add(country1);
                } else {
                    log.error("Unknown Country: " + country);
                }
            }
        }
        ancestry.setCountryOfRecruitment(countryList);
        if (sample.getSize() != -1) {
            ancestry.setNumberOfIndividuals(sample.getSize());
        }

        String ancestryCat = sample.getAncestryCategory();
        List<AncestralGroup> ancestryGroups = new ArrayList<>();
        if (ancestryCat != null) {
            String[] groups = ancestryCat.split("\\|");
            for (String group : groups) {
                AncestralGroup ancestralGroup = ancestralGroupService.findByAncestryGroup(group);
                if(ancestralGroup != null ) {
                    ancestryGroups.add(ancestralGroup);
                }
            }
        }
        ancestry.setAncestralGroups(ancestryGroups);
        return ancestry;
    }


    public AncestryExtension assembleAncestryExtension(Sample sample) {
        AncestryExtension ancestryExtension = new AncestryExtension();
        if(sample.getAncestry() != null) {
            ancestryExtension.setAncestryDescriptor(sample.getAncestry().replaceAll("\\|",", "));
        }
        ancestryExtension.setIsolatedPopulation(sample.getAncestryDescription());
        ancestryExtension.setNumberCases(sample.getCases());
        ancestryExtension.setNumberControls(sample.getControls());
        ancestryExtension.setSampleDescription(sample.getSampleDescription());
        return ancestryExtension;
    }

}
