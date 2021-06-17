package uk.ac.ebi.spot.gwas.util;

import org.apache.commons.cli.*;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -m option to run mapping on contents of GWAS catalog";

    public static final String MAPPING_OPT = "m";
    public static final String MAPPING_LONG_OPT = "mapping";
    public static final String MAPPING_DESCRIPTION = "Maps all associations in the GWAS database. Mapping pipeline will map SNPs " +
            "in database and also validate the author reported gene linked to that SNP via the associations";

    public static final String NIGHT_OPT = "n";
    public static final String NIGHT_LONG_OPT = "night";
    public static final String NIGHT_DESCRIPTION = "Maps all associations in the GWAS database. Mapping pipeline will map SNPs " +
            "in database and also validate the author reported gene linked to that SNP via the associations";

    public static final String CACHE_OPT = "c";
    public static final String CACHE_LONG_OPT = "cache";
    public static final String CACHE_DESCRIPTION = "-c [thread-size] : Retrieves all the needed mapping dataset from Ensembl without mapping the associations";

    private CommandUtil() {
        // Hide implicit public constructor
    }

    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();

        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option mappingOption = new Option(MAPPING_OPT, MAPPING_LONG_OPT, false, MAPPING_DESCRIPTION);
        Option mappingOptionLSF = new Option(NIGHT_OPT, NIGHT_LONG_OPT, false, NIGHT_DESCRIPTION);
        Option cacheOption = new Option(CACHE_OPT, CACHE_LONG_OPT, false, CACHE_DESCRIPTION);

        mappingOption.setArgName("performer");
        mappingOptionLSF.setArgName("performer");
        cacheOption.setArgName("thread-size");

        mappingOption.setRequired(true);
        mappingOptionLSF.setRequired(true);

        modeGroup.addOption(mappingOption);
        modeGroup.addOption(mappingOptionLSF);

        options.addOption(helpOption);
        options.addOption(cacheOption);
        options.addOptionGroup(modeGroup);
        options.addOptionGroup(modeGroup);

        return options;
    }

}
