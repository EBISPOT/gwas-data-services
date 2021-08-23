package uk.ac.ebi.spot.gwas.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.spot.gwas.model.EnsemblGene;
import uk.ac.ebi.spot.gwas.model.Gene;
import uk.ac.ebi.spot.gwas.repository.EnsemblGeneRepository;

@Service
public class EnsemblGeneQueryService {

    @Autowired
    private EnsemblGeneRepository ensemblGeneRepository;

    @Transactional(readOnly = true)
    public EnsemblGene findByEnsemblGeneId(String id) {
        EnsemblGene ensemblGene = ensemblGeneRepository.findByEnsemblGeneId(id);

        if (ensemblGene != null) {
            loadAssociatedData(ensemblGene);
        }
        return ensemblGene;
    }

    public void loadAssociatedData(EnsemblGene ensemblGene) {
        ensemblGene.getEnsemblGeneId();
        ensemblGene.getId();
        if (ensemblGene.getGene() != null) {
            ensemblGene.getGene();
            loadAssociatedGeneData(ensemblGene.getGene());
        }
    }

    private void loadAssociatedGeneData(Gene gene) {
        if (gene.getEnsemblGeneIds() != null) {
            gene.getEnsemblGeneIds().size();
        }
    }
}
