package uk.ac.ebi.spot.gwas.service;

public interface PublicationService {

    void updateCurationStatus(String pubmedId, String curatorEmail);
}
