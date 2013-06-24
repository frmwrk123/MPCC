package de.uni_hamburg.informatik.swt.se2.kino.services;

/**
 * Interface für Beobachter, die sich für Änderungen eines ObservableService
 * interessieren.
 * 
 * @author SE2-Team
 * @version SoSe 2013
 */
public interface ServiceObserver
{

    /**
     * Diese Operation wird aufgerufen, sobald sich an an dem beobachteten
     * Service etwas relevantes geändert hat.
     * 
     * Implementierende Klassen müssen in dieser Operation auf die Aenderung
     * reagieren.
     */
    void reagiereAufAenderung();
}
