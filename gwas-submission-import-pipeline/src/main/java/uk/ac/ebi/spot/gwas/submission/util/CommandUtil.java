package uk.ac.ebi.spot.gwas.submission.util;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public class CommandUtil {

    public static final String HELP_OPT = "h";
    public static final String HELP_LONG_OPT = "help";
    public static final String HELP_DESCRIPTION = "Use -r option to run batch im[ort of studiesof GWAS catalog";

    public static final String EXEC_SUBMISSIONID = "s";
    public static final String EXEC_SUBMISSIONID_LONG = "submissionId";
    public static final String EXEC_SUBMISSIONID_DESC = "-s [submissionId to import] 684af87a88ee4e000190d923";
    public static final String EXEC_CURATOR_ID = "c";
    public static final String EXEC_CURATOR_ID_LONG = "curator";
    public static final String EXEC_CURATOR_ID_DESC = "-c [curator emailId] Curator Email Id";
    public static final String EXEC_PUBLICATION_ID = "p";
    public static final String EXEC_PUBLICATION_LONG = "publication";
    public static final String EXEC_PUBLICATION_DESC = "-p [pubmedId] pmid for the publication";




    public static Options bindOptions() {

        OptionGroup modeGroup = new OptionGroup();
        modeGroup.setRequired(false);
        Options options = new Options();

        Option helpOption = new Option(HELP_OPT, HELP_LONG_OPT, false, HELP_DESCRIPTION);
        Option submissionIdOption = new Option(EXEC_SUBMISSIONID, EXEC_SUBMISSIONID_LONG, true, EXEC_SUBMISSIONID_DESC);
        Option curatorOption = new Option(EXEC_CURATOR_ID, EXEC_CURATOR_ID_LONG, true, EXEC_CURATOR_ID_DESC);
        Option pmidOption = new Option(EXEC_PUBLICATION_ID, EXEC_PUBLICATION_LONG, true, EXEC_PUBLICATION_DESC);
        options.addOption(helpOption);
        options.addOption(submissionIdOption);
        options.addOption(curatorOption);
        options.addOption(pmidOption);
        return options;
    }
}
