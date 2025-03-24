package uk.ac.ebi.spot.gwas.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.gwas.model.EfoTrait;
import uk.ac.ebi.spot.gwas.model.Study;

import java.util.List;

public interface StudyService {


    Page<Study> findStudiesByShortForm(String shortForm, Pageable pageable);
}
