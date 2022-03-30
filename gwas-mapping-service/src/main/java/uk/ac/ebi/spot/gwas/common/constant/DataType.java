package uk.ac.ebi.spot.gwas.common.constant;

public enum DataType {

    VARIATION(Uri.VARIATION_FILE),

    REPORTED_GENES(Uri.REPORTED_GENES_FILE),

    CYTOGENETIC_BAND(Uri.CYTOGENETIC_BAND_FILE),

    ASSEMBLY_INFO(Uri.ASSEMBLY_INFO_FILE),

    ENSEMBL_OVERLAP_GENES(Uri.ENSEMBL_OVERLAP_FILE),

    NCBI_OVERLAP_GENES(Uri.NCBI_OVERLAP_FILE),

    ENSEMBL_UPSTREAM_GENES(Uri.ENSEMBL_UPSTREAM_FILE),

    NCBI_UPSTREAM_GENES(Uri.NCBI_UPSTREAM_FILE),

    ENSEMBL_DOWNSTREAM_GENES(Uri.ENSEMBL_DOWNSTREAM_FILE),

    NCBI_DOWNSTREAM_GENES(Uri.NCBI_DOWNSTREAM_FILE);

    private final String value;

    DataType(String value) {
        this.value = value;
    }

    public String getFileLocation() {
        return value;
    }
}
