package uk.ac.ebi.spot.gwas.updateefo.report;

import uk.ac.ebi.spot.gwas.updateefo.domain.EfoTrait;

import java.util.concurrent.atomic.AtomicInteger;

public class ReportTemplate {

    private final AtomicInteger noObsolete = new AtomicInteger();
    private final AtomicInteger noUpdated = new AtomicInteger();
    private final AtomicInteger noErrors = new AtomicInteger();
    private final AtomicInteger noProcessed = new AtomicInteger();
    private final StringBuffer obsoleteSb = new StringBuffer();
    private final StringBuffer updatedSb = new StringBuffer();
    private final StringBuffer errorSb = new StringBuffer();

    public void addObsolete(EfoTrait oldEfo, EfoTrait newEfo) {
        noObsolete.incrementAndGet();
        obsoleteSb.append("(").append(oldEfo.getTrait()).append(", ").append(oldEfo.getShortForm()).append(") was obsoleted, replaced by ")
                  .append("(").append(newEfo.getTrait()).append(", ").append(newEfo.getShortForm()).append(")\n");
    }

    public void addUpdated(EfoTrait oldEfo, EfoTrait newEfo) {
        noUpdated.incrementAndGet();
        updatedSb.append("(").append(oldEfo.getTrait()).append(", ").append(oldEfo.getShortForm()).append(") was updated, changed to ")
                 .append("(").append(newEfo.getTrait()).append(", ").append(newEfo.getShortForm()).append(")\n");
    }

    public void addError(Exception e) {
        noErrors.incrementAndGet();
        errorSb.append(e.getMessage()).append("\n");
    }

    public void addProcessed() {
        noProcessed.incrementAndGet();
    }

    public String generateReport() {
        return "Processed: " + noProcessed + "\nObsoleted: " + noObsolete + "\nUpdated: " + noUpdated + "\nErrors: " + noErrors +
                "\n----------------------------\n" +
                obsoleteSb + updatedSb + errorSb;
    }

}
