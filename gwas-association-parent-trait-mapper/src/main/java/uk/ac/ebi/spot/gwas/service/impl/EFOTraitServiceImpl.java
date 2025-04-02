package uk.ac.ebi.spot.gwas.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.config.RestAPIConfiguration;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.EFOTraitRepository;
import uk.ac.ebi.spot.gwas.service.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EFOTraitServiceImpl implements EFOTraitService {

    EFOTraitRepository efoTraitRepository;

    RestAPIEFOService restAPIEFOService;

    RestAPIConfiguration restAPIConfiguration;

    AssociationService associationService;

    StudyService studyService;

    EfoTraitRetrieveService efoTraitRetrieveService;

    Map<String, String> efoTraitMap = new HashMap<>();

    @Autowired
    public EFOTraitServiceImpl(EFOTraitRepository efoTraitRepository,
                               RestAPIEFOService restAPIEFOService,
                               RestAPIConfiguration restAPIConfiguration,
                               AssociationService associationService,
                               StudyService studyService,
                               EfoTraitRetrieveService efoTraitRetrieveService) {
        this.efoTraitRepository = efoTraitRepository;
        this.restAPIEFOService = restAPIEFOService;
        this.restAPIConfiguration = restAPIConfiguration;
        this.associationService = associationService;
        this.studyService = studyService;
        this.efoTraitRetrieveService = efoTraitRetrieveService;
    }

    public Map<String, String> findAllEfoTraits() {

        Long count = efoTraitRepository.count();
        Long bucket = (count/1000);
        for(int i = 0; i <= bucket; i++) {
            Pageable pageable = PageRequest.of(i,1000);
            Page<EfoTrait> pages = efoTraitRepository.findAll(pageable);
            pages.forEach(efoTrait -> efoTraitMap.put(efoTrait.getShortForm(), efoTrait.getTrait()));
        }
        return efoTraitMap;
    }


    public Map<String, List<String>> loadParentChildEfo(List<String> shortForms) {
        Map<String, List<String>> efoParentChildMap = new HashMap<>();
        for ( String shortForm : shortForms ) {
            log.info("The shortform is {}", shortForm);
            Map<String, String> efoShortFormMap = new HashMap<>();
            efoShortFormMap =  restAPIEFOService.callOlsRestAPI(restAPIConfiguration.getOlaApiEndpoint(), efoShortFormMap, shortForm, false);
            if(efoShortFormMap != null) {
                List<String> childEFOTraits = efoShortFormMap.keySet().stream()
                        .filter(key ->
                                // log.info("The key in efoShortFormMap is {}", key);
                                efoTraitRepository.findByShortForm(key).isPresent())
                        .collect(Collectors.toList());
                // childEFOTraits.forEach(child -> log.info("childEFOTraits  {}", child));
                efoParentChildMap.put(shortForm, childEFOTraits);
            }
        }
        return efoParentChildMap;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<EfoTrait> saveParentEFOMapping(Map<String, List<String>> efoParentChildMap) {
        List<EfoTrait> efoTraitsToSave = new ArrayList<>();
        efoParentChildMap.keySet().forEach(shortForm -> {
            log.info("perent Efo is {}", shortForm);
            EfoTrait parentEfo = efoTraitRepository.findByShortForm(shortForm).orElse(null);
            List<String> childEfoShortForms = efoParentChildMap.get(shortForm);
            List<EfoTrait> childEfos = new ArrayList<>();
            for (List<String> partshortForms : ListUtils.partition(childEfoShortForms, 500)) {
                List<EfoTrait> partChildEfos = efoTraitRetrieveService.findByShortForms(partshortForms)
                        .stream()
                        .filter(Objects::nonNull).
                        collect(Collectors.toList());
                childEfos.addAll(partChildEfos);
            }
            if (parentEfo != null) {
                //log.info("parent Efo is {}", parentEfo);
                // log.info("inside efo children  empty block");
                //childEfos.forEach(child -> log.info("EFO children {}", child.getShortForm()));
                parentEfo.setParentChildEfoTraits(childEfos);
                efoTraitRepository.save(parentEfo);
                efoTraitsToSave.add(parentEfo);
            }
        });
        return efoTraitsToSave;
    }



}
