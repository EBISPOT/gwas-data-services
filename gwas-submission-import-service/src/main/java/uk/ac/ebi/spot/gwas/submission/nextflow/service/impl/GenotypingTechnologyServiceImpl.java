package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.model.GenotypingTechnology;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.GenotypingTechnologyRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.GenotypingTechnologyService;

@Service
public class GenotypingTechnologyServiceImpl implements GenotypingTechnologyService {

    GenotypingTechnologyRepository genotypingTechnologyRepository;

    public GenotypingTechnologyServiceImpl(GenotypingTechnologyRepository genotypingTechnologyRepository) {
        this.genotypingTechnologyRepository = genotypingTechnologyRepository;
    }

    public GenotypingTechnology findByGenotypingTechnology(String genotypingTechnology) {
        return genotypingTechnologyRepository.findByGenotypingTechnology(genotypingTechnology).orElse(null);
    }
}
