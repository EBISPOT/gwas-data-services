package uk.ac.ebi.spot.gwas.zooma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Locus {
    @Id
    @GeneratedValue
    private Long id;

    private Integer haplotypeSnpCount;

    private String description;

    @ManyToMany
    @JoinTable(name = "LOCUS_RISK_ALLELE",
               joinColumns = @JoinColumn(name = "LOCUS_ID"),
               inverseJoinColumns = @JoinColumn(name = "RISK_ALLELE_ID"))
    private Collection<RiskAllele> strongestRiskAlleles = new ArrayList<>();

    @ManyToOne
    @JoinTable(name = "ASSOCIATION_LOCUS",
               joinColumns = @JoinColumn(name = "LOCUS_ID"),
               inverseJoinColumns = @JoinColumn(name = "ASSOCIATION_ID"))
    private Association association;
}
