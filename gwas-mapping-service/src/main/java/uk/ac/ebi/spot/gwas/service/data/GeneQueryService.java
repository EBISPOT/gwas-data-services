package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.GeneRepository;

@Service
public class GeneQueryService {

    @Autowired
    private GeneRepository geneRepository;

    @Transactional(readOnly = true)
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
}
