package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.EntrezGene;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.EntrezGeneRepository;

@Service
public class EntrezGeneQueryService {

    @Autowired
    private EntrezGeneRepository entrezGeneRepository;

    @Transactional(readOnly = true)
    public EntrezGene findByEntrezGeneId(String id) {
        EntrezGene entrezGene = entrezGeneRepository.findByEntrezGeneId(id);
        if (entrezGene != null) {
            loadAssociatedData(entrezGene);
        }
        return entrezGene;
    }

    public void loadAssociatedData(EntrezGene entrezGene) {
        entrezGene.getEntrezGeneId();
        entrezGene.getId();
        if (entrezGene.getGene() != null) {
            entrezGene.getGene();
            loadAssociatedGeneData(entrezGene.getGene());
        }
    }

    private void loadAssociatedGeneData(Gene gene) {
        if (gene.getEntrezGeneIds() != null) {
            gene.getEntrezGeneIds().size();
        }
    }
}
