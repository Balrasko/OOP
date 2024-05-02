package hlasovanie;

/**
 * Rozhranie Hlasovanie2 definuje základnú funkciu hlasovania pre rôzne typy hlasovania.
 */
public interface Hlasovanie2 {
    /**
     * Vykoná hlasovanie pre zadaného kandidáta.
     * @param kandidat Meno kandidáta, pre ktorého sa hlasuje.
     */
    void hlasuj(String kandidat);
}