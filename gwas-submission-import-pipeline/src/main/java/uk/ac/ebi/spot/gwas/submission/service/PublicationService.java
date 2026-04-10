package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Publication;

public interface PublicationService {

   Publication findByPublicationId(String pubId);
}
