package volici;

import hlasovanie.Hlasovanie2;
import java.util.ArrayList;
import java.util.List;

/**
 * Trieda {@code Volic} predstavuje základný model voliča v hlasovacom systéme.
 * Umožňuje spravovať informácie o voličoch a ich metódy hlasovania.
 */
public class Volic {
    protected String meno;   // Meno voliča
    private String adresa;   // Adresa voliča
    private int vek;         // Vek voliča
    private String stat;     // Štát, v ktorom volič žije
    private String ID;       // Identifikačné číslo voliča
    private int psc;         // Poštové smerovacie číslo voliča
    protected List<Hlasovanie2> metodyHlasovania; // Zoznam metód hlasovania, ktoré môže volič použiť

    /**
     * Konštruktor pre {@code Volic} inicializuje voliča s potrebnými informáciami
     * a pripravuje zoznam metód hlasovania.
     *
     * @param meno Meno voliča
     * @param adresa Adresa voliča
     * @param vek Vek voliča
     * @param stat Štát, kde volič žije
     * @param ID Identifikačné číslo voliča
     * @param psc Poštové smerovacie číslo voliča
     */
    public Volic(String meno, String adresa, int vek, String stat, String ID, int psc) {
        this.meno = meno;
        this.adresa = adresa;
        this.vek = vek;
        this.stat = stat;
        this.ID = ID;
        this.psc = psc;
        this.metodyHlasovania = new ArrayList<>();
    }

    /**
     * Pridá metódu hlasovania do zoznamu dostupných metód pre voliča.
     * 
     * @param metoda Objekt implementujúci rozhranie {@code Hlasovanie2}, ktorý predstavuje metódu hlasovania.
     */
    public void addMetodaHlasovania(Hlasovanie2 metoda) {
        metodyHlasovania.add(metoda);
    }

    /**
     * Vykoná hlasovanie za voliča pomocou všetkých dostupných metód hlasovania.
     * Pre každú metódu v zozname volá metódu hlasuj pre zadaného kandidáta.
     *
     * @param kandidat Meno kandidáta, pre ktorého sa má hlasovať.
     */
    public void hlasuj(String kandidat) {
        for (Hlasovanie2 metoda : metodyHlasovania) {
            metoda.hlasuj(kandidat);
        }
    }
}