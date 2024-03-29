package uk.ac.ebi.spot.gwas.data.copy.table.model;

import javax.persistence.*;

/**
 * Created by emma on 21/07/2015.
 *
 * @author emma
 *         <p>
 *         A model object representing a gene from Entrez database
 */
@Entity
public class EntrezGene {

    @Id
    @GeneratedValue
    private Long id;

    private String entrezGeneId;

    @ManyToOne
    @JoinTable(name = "GENE_ENTREZ_GENE",
               joinColumns = @JoinColumn(name = "ENTREZ_GENE_ID"),
               inverseJoinColumns = @JoinColumn(name = "GENE_ID"))
    private Gene gene;

    // JPA no-args constructor
    public EntrezGene() {
    }


    public EntrezGene(String entrezGeneId, Gene gene) {
        this.entrezGeneId = entrezGeneId;
        this.gene = gene;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }


    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}
