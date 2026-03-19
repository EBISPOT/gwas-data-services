package uk.ac.ebi.spot.gwas.submission.nextflow.service.impl;

import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.gwas.submission.nextflow.service.AssociationCalculationService;

import java.text.DecimalFormat;

@Service
public class AssociationCalculationServiceImpl implements AssociationCalculationService {

    public String setRange(double orpc_stderr, double orpc_num) {
        double delta = (100000 * orpc_stderr * 1.96) / 100000;
        double low = orpc_num - delta;
        double high = orpc_num + delta;
        String lowval, highval;

        if (low < 0.001) {
            DecimalFormat df = new DecimalFormat("#.#####");
            lowval = df.format(low);
            highval = df.format(high);
        }
        else if (low >= 0.001 && low < 0.01) {
            DecimalFormat df = new DecimalFormat("#.####");
            lowval = df.format(low);
            highval = df.format(high);
        }
        else if (low >= 0.01 && low < 0.1) {
            DecimalFormat df = new DecimalFormat("#.###");
            lowval = df.format(low);
            highval = df.format(high);
        }
        else {
            DecimalFormat df = new DecimalFormat("#.##");
            lowval = df.format(low);
            highval = df.format(high);
        }

        String orpc_range = ("[" + lowval + "-" + highval + "]");

        return orpc_range;
    }

}
