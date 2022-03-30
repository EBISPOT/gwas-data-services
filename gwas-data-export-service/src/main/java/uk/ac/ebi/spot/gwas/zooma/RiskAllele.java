package uk.ac.ebi.spot.gwas.zooma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RiskAllele {

    @Id
    @GeneratedValue
    private Long id;

    private String riskAlleleName;

    private String riskFrequency;

    private Boolean genomeWide = false;

    private Boolean limitedList = false;

    @ManyToOne
    @JoinTable(name = "RISK_ALLELE_SNP",
               joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private SingleNucleotidePolymorphism snp;

    @ManyToMany
    @JoinTable(name = "RISK_ALLELE_PROXY_SNP",
               joinColumns = @JoinColumn(name = "RISK_ALLELE_ID"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID"))
    private Collection<SingleNucleotidePolymorphism> proxySnps;


    @ManyToMany(mappedBy = "strongestRiskAlleles")
    private Collection<Locus> loci;

}
