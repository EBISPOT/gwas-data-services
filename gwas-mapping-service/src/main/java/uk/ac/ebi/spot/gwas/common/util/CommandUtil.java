package uk.ac.ebi.spot.gwas.common.util;

import org.apache.commons.cli.*;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -m option to run mapping on contents of GWAS catalog";

    public static final String EXEC_MODE_OPT = "m";
    public static final String EXEC_MODE_OPT_LONG = "mode";
    public static final String EXEC_MODE_OPT_DESC = "-m [execution-mode] Example below: \n " +
            "Map some associations in database: java -jar gwas-mapping-service.jar -m map-some-snp \n "+
            "Map all associations in database: java -jar gwas-mapping-service.jar -m map-all-snp \n"+
            "Cache Ensembl prior to full remap: java -jar gwas-mapping-service.jar -m cache-ensembl-data \n"+
            "Run in server & scheduler mode: java -jar gwas-mapping-service.jar -m server-mode \n";

    private CommandUtil() {
        // Hide implicit public constructor
    }

    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();

        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option mappingOptionLSF = new Option(EXEC_MODE_OPT, EXEC_MODE_OPT_LONG, false, EXEC_MODE_OPT_DESC);

        mappingOptionLSF.setArgName("performer");
        mappingOptionLSF.setRequired(true);

        modeGroup.addOption(mappingOptionLSF);

        options.addOption(helpOption);
        options.addOptionGroup(modeGroup);

        return options;
    }

}
