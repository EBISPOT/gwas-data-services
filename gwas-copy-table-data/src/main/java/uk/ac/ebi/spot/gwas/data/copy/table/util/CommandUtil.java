package uk.ac.ebi.spot.gwas.data.copy.table.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -r option to run mapping on contents of GWAS catalog";

    public static final String EXEC_MODE_OPT = "m";

    public static final String EXEC_MODE_OPT_LONG = "mode";
    public static final String EXEC_MODE_OPT_DESC = "-m [mode] Example below: \n " +
            "-m {file, rsid}";

    public static final String EXEC_MAP_RSIDS = "r";

    public static final String EXEC_MAP_RSIDS_LONG = "rsids";

    public static final String EXEC_MAP_RSIDS_DESC = "-r [rsids to map] rs123456,rs";
    public static final String EXEC_INPUT_FILE = "i";

    public static final String EXEC_INPUT_FILE_LONG = "inputFileDir";

    public static final String EXEC_INPUT_FILE_DESC = "-i [inputfile] file which has the rsids to map ";
    public static final String EXEC_OUT_FILE = "o";
    public static final String EXEC_OUT_FILE_LONG = "outputfileDir";

    public static final String EXEC_OUT_FILE_DESC = "-o [outputfile Dir] for LSF jobs of Mapping Pipeline";
    public static final String EXEC_ERR_FILE = "e";
    public static final String EXEC_ERR_FILE_LONG = "errFileDir";

    public static final String EXEC_ERR_FILE_DESC = "-e [Error File] for LSF Jobs of Mapping Pipeline";

    public static final String EXEC_PUBIDS = "p";

    public static final String EXEC_PUBIDS_DESC = "-p [PubIds] List of Pubids seperate by commas ";

    public static final String EXEC_PUBIDS_LONG = "Pmids";

    private CommandUtil() {
        // Hide implicit public constructor
    }

    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();

        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option mappingOptionLSF = new Option(EXEC_MODE_OPT, EXEC_MODE_OPT_LONG, true, EXEC_MODE_OPT_DESC);
        Option rsidOption = new Option(EXEC_MAP_RSIDS, EXEC_MAP_RSIDS_LONG, true, EXEC_MAP_RSIDS);
        Option inputFileOption = new Option(EXEC_INPUT_FILE, EXEC_INPUT_FILE_LONG, true, EXEC_INPUT_FILE_DESC);
        Option outputfileDirOption  = new Option(EXEC_OUT_FILE, EXEC_OUT_FILE_LONG, true, EXEC_OUT_FILE_DESC);
        Option errfileDirOption =  new Option(EXEC_ERR_FILE, EXEC_ERR_FILE_LONG, true, EXEC_ERR_FILE_DESC);
        Option pubIdsOption = new Option(EXEC_PUBIDS, EXEC_PUBIDS_LONG, true, EXEC_PUBIDS_DESC );
        //pubIdsOption.setArgs(Option.UNLIMITED_VALUES);

        //mappingOptionLSF.setArgName("performer");
        //mappingOptionLSF.setRequired(true);

        modeGroup.addOption(mappingOptionLSF);

        options.addOption(helpOption);

        options.addOption(inputFileOption);

        options.addOption(rsidOption);

        options.addOption(outputfileDirOption);

        options.addOption(errfileDirOption);

        options.addOption(pubIdsOption);

        options.addOptionGroup(modeGroup);



        return options;
    }

}
