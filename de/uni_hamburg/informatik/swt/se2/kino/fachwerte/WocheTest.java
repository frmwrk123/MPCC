package de.uni_hamburg.informatik.swt.se2.kino.fachwerte;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Jim Martens
 * @version 13.06.2013
 * @copyright 2013 Jim Martens
 */
public class WocheTest
{
    private List<Tag> _wochentageGueltig;
    private List<Tag> _wochentageUngueltig;
    
    @Before
    public void setUp()
    {
        _wochentageGueltig = new ArrayList<>(7);
        _wochentageGueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageGueltig.add(new Tag(new Datum(14, 6, 2013)));
        _wochentageGueltig.add(new Tag(new Datum(15, 6, 2013)));
        _wochentageGueltig.add(new Tag(new Datum(16, 6, 2013)));
        _wochentageGueltig.add(new Tag(new Datum(17, 6, 2013)));
        _wochentageGueltig.add(new Tag(new Datum(18, 6, 2013)));
        _wochentageGueltig.add(new Tag(new Datum(19, 6, 2013)));
        
        _wochentageUngueltig = new ArrayList<>(7);
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
        _wochentageUngueltig.add(new Tag(new Datum(13, 6, 2013)));
    }
    
    @Test
    public void testKonstruktor()
    {
        Woche woche = new Woche(_wochentageGueltig);
        assertSame(_wochentageGueltig, woche.getWochentage());
    }
    
    @Test
    public void testIstGueltig()
    {
        boolean gueltig = Woche.istGueltig(_wochentageGueltig);
        assertTrue(gueltig);
        gueltig = Woche.istGueltig(_wochentageUngueltig);
        assertFalse(gueltig);
    }
    
    @Test
    public void testDieseWoche()
    {
        assertNotNull(Woche.dieseWoche());
    }
    
    @Test
    public void testLetzteWoche()
    {
        Woche woche = Woche.dieseWoche();
        Woche letzteWoche = woche.letzteWoche();
        assertNotNull(letzteWoche);
    }
    
    @Test
    public void testNaechsteWoche()
    {
        Woche woche = Woche.dieseWoche();
        Woche naechsteWoche = woche.naechsteWoche();
        assertNotNull(naechsteWoche);
    }
    
    @Test
    public void testIstInWoche()
    {
        Woche woche = Woche.dieseWoche();
        Datum inWoche = woche.getWochentage().get(0).getDatum();
        Datum nichtInWoche = new Datum(1, 1, 1901);
        assertTrue(woche.istInWoche(inWoche));
        assertFalse(woche.istInWoche(nichtInWoche));
    }
    
    @Test
    public void testWocheMitDiesemTag()
    {
        assertNotNull(Woche.wocheMitDiesemTag(Datum.heute()));
    }
    
    @Test
    public void testEquals()
    {
        Woche woche = Woche.dieseWoche();
        Woche woche2 = Woche.dieseWoche();
        Woche woche3 = woche.letzteWoche();
        
        assertTrue(woche2.equals(woche));
        assertTrue(woche2.hashCode() == woche.hashCode());
        
        assertFalse(woche2.equals(woche3));
    }
}
