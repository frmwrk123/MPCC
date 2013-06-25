package de.uni_hamburg.informatik.swt.se2.kino.services.kino;

import java.util.ArrayList;
import java.util.List;

import de.uni_hamburg.informatik.swt.se2.kino.fachwerte.Datum;
import de.uni_hamburg.informatik.swt.se2.kino.fachwerte.FSK;
import de.uni_hamburg.informatik.swt.se2.kino.fachwerte.Reinigungszeit;
import de.uni_hamburg.informatik.swt.se2.kino.fachwerte.Uhrzeit;
import de.uni_hamburg.informatik.swt.se2.kino.fachwerte.Woche;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Film;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Kino;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Kinosaal;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Tagesplan;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Vorstellung;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Werbeblock;
import de.uni_hamburg.informatik.swt.se2.kino.materialien.Wochenplan;
import de.uni_hamburg.informatik.swt.se2.kino.services.AbstractObservableService;

public class KinoServiceImpl extends AbstractObservableService implements
        KinoService
{
    private Kino _kino;
    
    /**
     * Initialisiert den KinoService.
     * 
     * @param kino
     *            Das Kino für das Kinosystem.
     * 
     * @require kino != null
     */
    public KinoServiceImpl(Kino kino)
    {
        assert kino != null : "Vorbedingung verletzt: kino != null";
        _kino = kino;
    }
    
    @Override
    public boolean istFilmZeigenMoeglich(Film film, Werbeblock werbeblock,
            Reinigungszeit reinigungszeit, Kinosaal kinosaal, Datum datum,
            Uhrzeit startzeit)
    {
        assert istFilmVorraetig(film) : "Vorbedingung verletzt: istFilmVorraetig(film)";
        assert istKinosaalVorhanden(kinosaal) : "Vorbedingung verletzt: istKinosaalVorhanden(kinosaal)";
        assert datum != null : "Vorbedingung verletzt: datum != null";
        assert startzeit != null : "Vorbedingung verletzt: startzeit != null";
        
        Tagesplan tagesplan = _kino.getTagesplan(datum);
        List<Vorstellung> vorstellungen = tagesplan.getVorstellungen();
        
        Vorstellung testVorstellung = new Vorstellung(kinosaal, film,
                startzeit, datum, Vorstellung.TICKETPREIS,
                kinosaal.getReinigungszeit());
        if (werbeblock != null)
        {
            testVorstellung.setWerbeblock(werbeblock);
        }
        if (reinigungszeit != null)
        {
            testVorstellung.setReinigungszeit(reinigungszeit);
        }
        
        boolean result = true;
        
        // Check für Überschneidungen mit anderen Vorstellungen
        for (Vorstellung vorstellung : vorstellungen)
        {
            if (vorstellung.equals(testVorstellung))
            {
                result = false;
            }
        }
        
        // Check für FSK-Regelung
        if (film.getFSK() == FSK.FSK18)
        {
            Uhrzeit abHierErlaubt = new Uhrzeit(22, 00);
            // Wenn Startzeit vor erlaubter Startzeit liegt, dann darf der Film
            // nicht gezeigt werden.
            if (startzeit.compareTo(abHierErlaubt) < 0)
            {
                result = false;
            }
        }
        
        return result;
    }
    
    @Override
    public void fuegeVorstellungHinzu(Vorstellung vorstellung, Datum datum,
            Kinosaal kinosaal, Uhrzeit startzeit)
    {
        assert istHinzufuegenMoeglich(vorstellung) : "Vorbedingung verletzt: istHinzufuegenMoeglich(vorstellung)";
        assert datum != null : "Vorbedingung verletzt: datum != null";
        assert istKinosaalVorhanden(kinosaal) : "Vorbedingung verletzt: istKinosaalVorhanden(kinosaal)";
        assert startzeit != null : "Vorbedingung verletzt: startzeit != null";
        
        Tagesplan tagesplan = _kino.getTagesplan(datum);
        tagesplan.fuegeVorstellungHinzu(vorstellung);
        Wochenplan wochenplan = _kino.getWochenplan(kinosaal,
                Woche.wocheMitDiesemTag(datum));
        wochenplan.fuegeVorstellungHinzu(vorstellung);
        
        informiereUeberAenderung();
    }
    
    @Override
    public boolean istHinzufuegenMoeglich(Vorstellung vorstellung)
    {
        assert vorstellung != null : "Vorbedingung verletzt: vorstellung != null";
        
        if (istVorstellungVorhanden(vorstellung))
        {
            return false;
        }
        
        Film film = vorstellung.getFilm();
        Uhrzeit startzeit = vorstellung.getAnfangszeit();
        
        Werbeblock werbeblock = null;
        if (vorstellung.hatWerbeblock())
        {
            werbeblock = vorstellung.getWerbeblock();
        }
        
        Datum datum = vorstellung.getDatum();
        Kinosaal kinosaal = vorstellung.getKinosaal();
        Reinigungszeit reinigungszeit = null;
        if (vorstellung.hatReinigungszeit())
        {
            reinigungszeit = vorstellung.getReinigungszeit();
        }
        
        return istFilmZeigenMoeglich(film, werbeblock, reinigungszeit,
                kinosaal, datum, startzeit);
    }
    
    @Override
    public boolean istVorstellungVorhanden(Vorstellung vorstellung)
    {
        assert vorstellung != null : "Vorbedingung verletzt: vorstellung != null";
        Datum datum = vorstellung.getDatum();
        Tagesplan tagesplan = _kino.getTagesplan(datum);
        Wochenplan wochenplan = _kino.getWochenplan(vorstellung.getKinosaal(),
                Woche.wocheMitDiesemTag(datum));
        
        return tagesplan.istVorhanden(vorstellung)
                && wochenplan.istVorhanden(vorstellung);
    }
    
    @Override
    public void entferneVorstellung(Vorstellung vorstellung)
    {
        assert istVorstellungVorhanden(vorstellung) : "Vorbedingung verletzt: istVorstellungVorhanden(vorstellung)";
        
        Datum datum = vorstellung.getDatum();
        Tagesplan tagesplan = _kino.getTagesplan(datum);
        tagesplan.entferneVorstellung(vorstellung);
        Wochenplan wochenplan = _kino.getWochenplan(vorstellung.getKinosaal(),
                Woche.wocheMitDiesemTag(datum));
        wochenplan.entferneVorstellung(vorstellung);
        
        informiereUeberAenderung();
    }
    
    @Override
    public Vorstellung getVorstellung(Kinosaal saal, Datum datum,
            Uhrzeit startzeit)
    {
        assert istKinosaalVorhanden(saal) : "Vorbedingung verletzt: istKinosaalVorhanden(saal)";
        assert datum != null : "Vorbedingung verletzt: datum != null";
        assert startzeit != null : "Vorbedingung verletzt: startzeit != null";
        
        Wochenplan wochenplan = _kino.getWochenplan(saal,
                Woche.wocheMitDiesemTag(datum));
        Tagesplan tagesplan = wochenplan.getTagesplan(datum);
        List<Vorstellung> vorstellungen = tagesplan.getVorstellungen();
        
        Vorstellung result = null;
        
        boolean vorstellungVorhanden = false;
        for (Vorstellung vorstellung : vorstellungen)
        {
            if (vorstellung.getAnfangszeit().equals(startzeit))
            {
                result = vorstellung;
                vorstellungVorhanden = true;
                break;
            }
        }
        
        assert vorstellungVorhanden : "Vorbedingung verletzt: Eine Vorstellung in dem Kinosaal, an dem gegebenen Tag zur gegebenen Uhrzeit muss existieren.";
        
        return result;
    }
    
    @Override
    public boolean istKinosaalVorhanden(Kinosaal kinosaal)
    {
        assert kinosaal != null : "Vorbedingung verletzt: kinosaal != null";
        return _kino.hatKinosaal(kinosaal);
    }
    
    @Override
    public boolean istFilmVorraetig(Film film)
    {
        assert film != null : "Vorbedingung verletzt: film != null";
        return _kino.getFilme().contains(film);
    }
    
    @Override
    public List<Film> getFilme()
    {
        return new ArrayList<Film>(_kino.getFilme());
    }
    
    @Override
    public Tagesplan getTagesplan(Datum datum)
    {
        assert datum != null : "Vorbedingung verletzt: datum != null";
        return _kino.getTagesplan(datum);
    }
    
    @Override
    public Wochenplan getWochenplan(Kinosaal kinosaal, Woche woche)
    {
        assert istKinosaalVorhanden(kinosaal) : "Vorbedingung verletzt: istKinosaalVorhanden(kinosaal)";
        assert woche != null : "Vorbedingung verletzt: woche != null";
        
        return _kino.getWochenplan(kinosaal, woche);
    }
    
    @Override
    public void setWochenplan(Wochenplan wochenplan, Kinosaal kinosaal,
            Woche woche)
    {
        assert istKinosaalVorhanden(kinosaal) : "Vorbedingung verletzt: istKinosaalVorhanden(kinosaal)";
        assert woche != null : "Vorbedingung verletzt: woche != null";
        assert !istWochenplanVorhanden(kinosaal, woche) : "Vorbedingung verletzt: !istWochenplanVorhanden(kinosaal, woche)";
        
        _kino.setWochenplan(kinosaal, woche, wochenplan);
    }
    
    @Override
    public boolean istWochenplanVorhanden(Kinosaal kinosaal, Woche woche)
    {
        assert istKinosaalVorhanden(kinosaal) : "Vorbedingung verletzt: istKinosaalVorhanden(kinosaal)";
        assert woche != null : "Vorbedingung verletzt: woche != null";
        
        return _kino.istWochenplanVorhanden(kinosaal, woche);
    }
    
    @Override
    public List<Kinosaal> getKinosaele()
    {
        return new ArrayList<Kinosaal>(_kino.getKinosaele());
    }
    
    @Override
    public boolean istVorstellungEntfernenMoeglich(Vorstellung vorstellung)
    {
        assert istVorstellungVorhanden(vorstellung) : "Vorbedingung verletzt: istVorstellungVorhanden(vorstellung)";
        return vorstellung.getAnzahlVerkauftePlaetze() == 0;
    }
    
    @Override
    public int getWerbeblockMaximalDauer(Vorstellung vorstellung)
    {
        assert istVorstellungVorhanden(vorstellung) : "Vorbedingung verletzt: istVorstellungVorhanden(vorstellung)";
        Uhrzeit startzeit = vorstellung.getAnfangszeit();
        
        int dauer = 0;
        dauer += vorstellung.getFilm().getLaenge();
        if (vorstellung.hatReinigungszeit())
        {
            dauer += vorstellung.getReinigungszeit().getDauer();
        }
        
        int stunden = startzeit.getStunden();
        int maximalZeit = 0;
        
        Uhrzeit uhrzeit = null;
        switch (stunden)
        {
            case 11:
                uhrzeit = new Uhrzeit(15, 00);
                break;
            case 15:
                uhrzeit = new Uhrzeit(17, 30);
                break;
            case 17:
                uhrzeit = new Uhrzeit(20, 00);
                break;
            case 20:
                uhrzeit = new Uhrzeit(22, 30);
                break;
            case 22:
                uhrzeit = new Uhrzeit(11, 00);
                break;
        }
        maximalZeit = uhrzeit.minutenSeit(startzeit);
        maximalZeit = maximalZeit - dauer;
        
        return maximalZeit;
    }
    
    @Override
    public boolean istVorstellungErstellbar(Kinosaal kinosaal, Datum datum,
            Uhrzeit startzeit)
    {
        assert istKinosaalVorhanden(kinosaal) : "Vorbedingung verletzt: istKinosaalVorhanden(kinosaal)";
        assert datum != null : "Vorbedingung verletzt: datum != null";
        assert startzeit != null : "Vorbedingung verletzt: startzeit != null";
        
        Wochenplan wochenplan = getWochenplan(kinosaal,
                Woche.wocheMitDiesemTag(datum));
        Tagesplan tagesplan = wochenplan.getTagesplan(datum);
        
        boolean result = true;
        
        List<Vorstellung> vorstellungen = tagesplan.getVorstellungen();
        for (Vorstellung vorstellung : vorstellungen)
        {
            if (vorstellung.getAnfangszeit().compareTo(startzeit) < 0
                    && vorstellung.getEndzeit().compareTo(startzeit) > 0)
            {
                result = false;
                break;
            }
        }
        
        return result;
    }
    
}
