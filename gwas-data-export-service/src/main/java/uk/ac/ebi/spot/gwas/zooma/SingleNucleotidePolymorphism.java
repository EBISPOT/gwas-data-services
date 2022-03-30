package uk.ac.ebi.spot.gwas.zooma;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SingleNucleotidePolymorphism {

    @Id
    @GeneratedValue
    private Long id;

    private String rsId;

    private Long merged;

    private String functionalClass;

    private Date lastUpdateDate;

    @OneToMany(mappedBy = "snp")
    private Collection<RiskAllele> riskAlleles;

    @ManyToOne
    @JoinTable(name = "SNP_MERGED_SNP",
               joinColumns = @JoinColumn(name = "SNP_ID_MERGED"),
               inverseJoinColumns = @JoinColumn(name = "SNP_ID_CURRENT"))
    private SingleNucleotidePolymorphism currentSnp;

    @ManyToMany
    @JoinTable(name = "ASSOCIATION_SNP_VIEW",
               joinColumns = @JoinColumn(name = "SNP_ID"),
               inverseJoinColumns = @JoinColumn(name = "ASSOCIATION_ID"))
    private Collection<Association> associations;


    @ManyToMany
    @JoinTable(name = "STUDY_SNP_VIEW",
               joinColumns = @JoinColumn(name = "SNP_ID"),
               inverseJoinColumns = @JoinColumn(name = "STUDY_ID"))
    private List<Study> studies;

}
