package uk.ac.ebi.spot.gwas.common.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.common.model.Gene;
import uk.ac.ebi.spot.gwas.common.repository.GeneRepository;

import java.util.List;

@Slf4j
@Service
public class GeneQueryService {

    @Autowired
    private GeneRepository geneRepository;

    ///@Transactional(readOnly = true)
    @Transactional(propagation = Propagation.SUPPORTS)
    public Gene findByGeneName(String geneName) {
        Gene gene = geneRepository.findByGeneName(geneName);
        if (gene != null) {
            loadAssociatedData(gene);
        }
        return gene;
    }

    public void loadAssociatedData(Gene gene) {
        if (gene.getEntrezGeneIds() != null) {
            gene.getEntrezGeneIds().size();
        }

        if (gene.getEnsemblGeneIds() != null) {
            gene.getEnsemblGeneIds().size();
        }
    }


    @Transactional(readOnly = true)
    public List<Gene> findGenesByIds(List<Long> mappedGeneIds) {
        return geneRepository.findAllById(mappedGeneIds);
    }

}
