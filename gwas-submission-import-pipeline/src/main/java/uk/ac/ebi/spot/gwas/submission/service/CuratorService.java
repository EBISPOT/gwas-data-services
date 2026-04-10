package uk.ac.ebi.spot.gwas.submission.service;

import uk.ac.ebi.spot.gwas.deposition.domain.Curator;

public interface CuratorService {

   Curator findById(String curatorId);

   Curator findByEmail(String email);
}
