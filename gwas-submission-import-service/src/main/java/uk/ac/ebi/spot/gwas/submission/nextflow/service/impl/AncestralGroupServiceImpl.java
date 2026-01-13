package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.AncestralGroup;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.AncestralGroupRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AncestralGroupService;

@Service
public class AncestralGroupServiceImpl implements AncestralGroupService {

    AncestralGroupRepository ancestralGroupRepository;

    public AncestralGroupServiceImpl(AncestralGroupRepository ancestralGroupRepository) {
        this.ancestralGroupRepository = ancestralGroupRepository;
    }

    public  AncestralGroup findByAncestryGroup(String ancestryGroup) {
       return ancestralGroupRepository.findByAncestralGroup(ancestryGroup).orElse(null);
    }

}
