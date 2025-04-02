package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.repository.EFOTraitRepository;
import uk.ac.ebi.spot.gwas.service.EfoTraitRetrieveService;

import java.util.List;

@Service
public class EfoTraitRetrieveServiceImpl implements EfoTraitRetrieveService {

    EFOTraitRepository efoTraitRepository;

    public EfoTraitRetrieveServiceImpl(EFOTraitRepository efoTraitRepository) {
        this.efoTraitRepository = efoTraitRepository;
    }

    @Transactional(readOnly = true)
    public List<EfoTrait> findByShortForms(List<String> shortForms) {
        return efoTraitRepository.findByShortFormIn(shortForms);
    }

    @Transactional(readOnly = true)
    public EfoTrait findByShortForm(String shortForm) {
        return efoTraitRepository.findByShortForm(shortForm).orElse(null);
    }
}
