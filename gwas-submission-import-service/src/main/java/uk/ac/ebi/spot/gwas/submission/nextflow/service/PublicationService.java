package uk.ac.ebi.spot.gwas.submission.nextflow.service;

import uk.ac.ebi.spot.gwas.model.Publication;

public interface PublicationService {

    Publication findByPmid(String pmid);

    void save(Publication publication);
}
