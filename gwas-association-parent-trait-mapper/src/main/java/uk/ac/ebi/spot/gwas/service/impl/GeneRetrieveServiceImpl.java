package uk.ac.ebi.spot.gwas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.GeneRepository;
import uk.ac.ebi.spot.gwas.service.GeneRetrieveService;

import java.util.List;

@Service
public class GeneRetrieveServiceImpl implements GeneRetrieveService {

    GeneRepository geneRepository;

    @Autowired
    public GeneRetrieveServiceImpl(GeneRepository geneRepository) {
        this.geneRepository = geneRepository;
    }

    @Transactional(readOnly = true)
   public List<Gene> findGenesByIds(List<Long> mappedGeneIds) {
        return geneRepository.findAllById(mappedGeneIds);
   }


}
