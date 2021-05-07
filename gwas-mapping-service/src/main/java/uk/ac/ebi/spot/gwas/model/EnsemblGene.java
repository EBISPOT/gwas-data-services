package uk.ac.ebi.spot.gwas.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Entity
public class EnsemblGene {

    @Id
    @GeneratedValue
    private Long id;

    private String ensemblGeneId;

    @ManyToOne
    @JoinTable(name = "GENE_ENSEMBL_GENE",
               joinColumns = @JoinColumn(name = "ENSEMBL_GENE_ID"),
               inverseJoinColumns = @JoinColumn(name = "GENE_ID"))
    private Gene gene;

    // JPA no-args constructor
    public EnsemblGene() {
    }

    public EnsemblGene(String ensemblGeneId, Gene gene) {
        this.ensemblGeneId = ensemblGeneId;
        this.gene = gene;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}
