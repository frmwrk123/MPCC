package de.uni_hamburg.informatik.swt.se2.kino.werkzeuge.kasse;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import de.uni_hamburg.informatik.swt.se2.kino.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Tagesplan;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Vorstellung;
import de.uni_hamburg.informatik.swt.se2.kino.services.ServiceObserver;
import de.uni_hamburg.informatik.swt.se2.kino.services.kino.KinoService;
import de.uni_hamburg.informatik.swt.se2.kino.werkzeuge.datumsauswaehler.DatumAuswaehlWerkzeug;
import de.uni_hamburg.informatik.swt.se2.kino.werkzeuge.platzverkauf.PlatzVerkaufsWerkzeug;
import de.uni_hamburg.informatik.swt.se2.kino.werkzeuge.vorstellungsauswaehler.VorstellungsAuswaehlWerkzeug;

/**
 * Das Kassenwerkzeug. Mit diesem Werkzeug kann die Benutzerin oder der Benutzer
 * eine Vorstellung auswählen und Karten für diese Vorstellung verkaufen und
 * stornieren.
 * 
 * @author SE2-Team
 * @version SoSe 2013
 */
public class KassenWerkzeug implements Observer
{
    // Der Service dieses Werkzeugs
    private KinoService _kinoService;
    
    // UI dieses Werkzeugs
    private KassenWerkzeugUI _ui;
    
    // Die Subwerkzeuge
    private PlatzVerkaufsWerkzeug _platzVerkaufsWerkzeug;
    private DatumAuswaehlWerkzeug _datumAuswaehlWerkzeug;
    private VorstellungsAuswaehlWerkzeug _vorstellungAuswaehlWerkzeug;
    
    /**
     * Initialisiert das Kassenwerkzeug.
     * 
     * @param kinoService
     *            Der KinoService, mit dem das Werkzeug arbeitet.
     * 
     * @require kinoService != null
     */
    public KassenWerkzeug(KinoService kinoService)
    {
        assert kinoService != null : "Vorbedingung verletzt: kinoService != null";
        
        _kinoService = kinoService;
        
        // Subwerkzeuge erstellen
        _platzVerkaufsWerkzeug = new PlatzVerkaufsWerkzeug();
        _datumAuswaehlWerkzeug = new DatumAuswaehlWerkzeug();
        _vorstellungAuswaehlWerkzeug = new VorstellungsAuswaehlWerkzeug();
        
        // als Observer registrieren
        _datumAuswaehlWerkzeug.addObserver(this);
        _vorstellungAuswaehlWerkzeug.addObserver(this);
        _kinoService.registriereBeobachter(new ServiceObserver()
        {
            @Override
            public void reagiereAufAenderung()
            {
                aktualisiereVorstellungsAuswahl();
            }
        });
        
        // UI erstellen (mit eingebetteten UIs der direkten Subwerkzeuge)
        _ui = new KassenWerkzeugUI(_platzVerkaufsWerkzeug.getUIPanel(),
                _datumAuswaehlWerkzeug.getUIPanel(),
                _vorstellungAuswaehlWerkzeug.getUIPanel());
        
        setzeTagesplanFuerAusgewaehltesDatum();
        setzeAusgewaehlteVorstellung();
    }
    
    /**
     * Gibt das Haupt-Panel der UI zurück.
     * 
     * @ensure result != null
     */
    public JPanel getUIPanel()
    {
        return _ui.getUIPanel();
    }
    
    /**
     * Aktualisiert den Tagesplan.
     */
    public void aktualisiereTagesplan()
    {
        _vorstellungAuswaehlWerkzeug.aktualisiereVorstellungen();
    }
    
    /**
     * Setzt den in diesem Werkzeug angezeigten Tagesplan basierend auf dem
     * derzeit im DatumsAuswaehlWerkzeug ausgewählten Datum.
     */
    private void setzeTagesplanFuerAusgewaehltesDatum()
    {
        Tagesplan tagesplan = _kinoService
                .getTagesplan(getAusgewaehltesDatum());
        _vorstellungAuswaehlWerkzeug.setTagesplan(tagesplan);
    }
    
    /**
     * Passt die Anzeige an, wenn eine andere Vorstellung gewählt wurde.
     */
    private void setzeAusgewaehlteVorstellung()
    {
        _platzVerkaufsWerkzeug.setVorstellung(getAusgewaehlteVorstellung());
    }
    
    /**
     * Gibt das derzeit gewählte Datum zurück.
     */
    private Datum getAusgewaehltesDatum()
    {
        return _datumAuswaehlWerkzeug.getSelektiertesDatum();
    }
    
    /**
     * Gibt die derzeit ausgewaehlte Vorstellung zurück. Wenn keine Vorstellung
     * ausgewählt ist, wird <code>null</code> zurückgegeben.
     */
    private Vorstellung getAusgewaehlteVorstellung()
    {
        return _vorstellungAuswaehlWerkzeug.getAusgewaehlteVorstellung();
    }
    
    /**
     * Aktualisiert die Vorstellungsauswahl.
     */
    private void aktualisiereVorstellungsAuswahl()
    {
        setzeTagesplanFuerAusgewaehltesDatum();
    }
    
    @Override
    public void update(Observable observable, Object object)
    {
        if (observable instanceof DatumAuswaehlWerkzeug)
        {
            setzeTagesplanFuerAusgewaehltesDatum();
            setzeAusgewaehlteVorstellung();
        }
        else if (observable instanceof VorstellungsAuswaehlWerkzeug)
        {
            setzeAusgewaehlteVorstellung();
        }
    }
}
