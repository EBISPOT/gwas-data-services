package uk.ac.ebi.spot.gwas.constant;

public enum DataType {

    VARIATION(Location.VARIATION_FILE),

    REPORTED_GENES(Location.REPORTED_GENES_FILE),

    CYTOGENETIC_BAND(Location.CYTOGENETIC_BAND_FILE),

    ASSEMBLY_INFO(Location.ASSEMBLY_INFO_FILE),

    ENSEMBL_OVERLAP_GENES(Location.ENSEMBL_OVERLAP_FILE),

    NCBI_OVERLAP_GENES(Location.NCBI_OVERLAP_FILE),

    ENSEMBL_UPSTREAM_GENES(Location.ENSEMBL_UPSTREAM_FILE),

    NCBI_UPSTREAM_GENES(Location.NCBI_UPSTREAM_FILE),

    ENSEMBL_DOWNSTREAM_GENES(Location.ENSEMBL_DOWNSTREAM_FILE),

    NCBI_DOWNSTREAM_GENES(Location.NCBI_DOWNSTREAM_FILE);

    private final String value;

    DataType(String value) {
        this.value = value;
    }

    public String getFileLocation() {
        return value;
    }
}
