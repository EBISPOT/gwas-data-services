package uk.ac.ebi.spot.gwas.service;

import java.util.List;

public interface SnpService {

     void updateSnpMappingGenes(List<Long> snpIds);
}
