package uk.ac.ebi.spot.gwas.model;

public interface Trackable {
    /**
     * Add event to an objects current collection of events
     *
     * @param event the event to add to study
     */
    void addEvent(Event event);
}
