package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Author;
import uk.ac.ebi.spot.gwas.model.GenotypingTechnology;
import uk.ac.ebi.spot.gwas.model.Study;
import uk.ac.ebi.spot.gwas.submission.nextflow.oracle.repository.StudyRepository;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.StudiesRetrieveService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class StudiesRetrieveServiceImpl implements StudiesRetrieveService {

    StudyRepository studyRepository;

    public StudiesRetrieveServiceImpl(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
    }

    @Transactional(readOnly = true)
    public  Page<Study> getStudies(Long publicationId, Pageable pageable) {
        Page<Study> pageStudies = studyRepository.findByPublicationIdId(publicationId, pageable);
        //pageStudies.forEach(this::deepLoadStudiesData);
        return pageStudies;
    }

    //@Transactional(readOnly = true)
    public Long countStudies(Long publicationId) {
        return studyRepository.countStudiesByPublicationIdId(publicationId);
    }

    @Transactional(readOnly = true)
    public List<Study> findByAccessionIds(List<String> accessionIds) {
        return studyRepository.findByAccessionIdIn(accessionIds);
    }


    public  void deepLoadStudiesData(Study study) {
        int efoTraitCount = study.getEfoTraits().size();
        int associationCount = study.getAssociations().size();
        int ancestryCount = study.getAncestries().size();
        study.getAncestries().forEach( ancestry -> {
            int groupCount = ancestry.getAncestralGroups().size();
            int coo = ancestry.getCountryOfOrigin().size();
            int cor = ancestry.getCountryOfRecruitment().size();
        });
        Collection<Author> authorArrayList = new ArrayList<>();
        study.getPublicationId().getPublicationAuthors().forEach(publicationAuthor ->{
            authorArrayList.add(publicationAuthor.getAuthor());
        });
        int genotypingCount = study.getGenotypingTechnologies().size();

        Collection<GenotypingTechnology> genotypingTechnologiesList = new ArrayList<>();
        study.getGenotypingTechnologies().forEach( genotypingTechnology ->{
            genotypingTechnologiesList.add(genotypingTechnology);
        });
        int platformCount = study.getPlatforms().size();
        Date publishDate = study.getHousekeeping().getCatalogPublishDate();
    }
}
