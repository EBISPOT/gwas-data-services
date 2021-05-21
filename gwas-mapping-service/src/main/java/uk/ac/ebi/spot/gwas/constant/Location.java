package uk.ac.ebi.spot.gwas.constant;

public class Location {

    public static final String VARIATION = "https://rest.ensembl.org/variation/homo_sapiens";

    public static final String REPORTED_GENES = "https://rest.ensembl.org/lookup/symbol/homo_sapiens";

    public static final String OVERLAP_BAND_REGION = "https://rest.ensembl.org/overlap/region/homo_sapiens/%s?feature=band";

    public static final String INFO_ASSEMBLY = "https://rest.ensembl.org/info/assembly/homo_sapiens/%s";

    public static final String OVERLAPPING_GENE_REGION = "https://rest.ensembl.org/overlap/region/homo_sapiens/%s?feature=gene";

    public static final String VARIATION_FILE = "variants.json";

    public static final String REPORTED_GENES_FILE = "reported-genes.json";

    public static final String CYTOGENETIC_BAND_FILE = "cytogenetic-band.json";

    public static final String ASSEMBLY_INFO_FILE = "gwas-assembly-info.json";


    private Location(){
        // Never called
    }


    // [https://rest.ensembl.org/overlap/region/homo_sapiens/{chromosome}:{pos_up}-{position}?feature=gene]

}
