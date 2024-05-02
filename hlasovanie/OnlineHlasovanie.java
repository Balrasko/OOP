package hlasovanie;

import volici.Volic;

/**
 * Trieda {@code OnlineHlasovanie} implementuje rozhranie {@code Hlasovanie2}
 * a poskytuje metódu na hlasovanie online.
 * <p>
 * Táto implementácia jednoducho vypíše meno kandidáta, pre ktorého sa hlasuje,
 * na štandardný výstup.
 * </p>
 */
public class OnlineHlasovanie implements Hlasovanie2 {

    /**
     * Vykoná hlasovanie pre zadaného kandidáta.
     * <p>
     * Metóda prijíma meno kandidáta a vypisuje správu o tom, že sa pre daného kandidáta
     * hlasovalo online.
     * </p>
     * 
     * @param kandidat Meno kandidáta, pre ktorého sa hlasuje.
     */
    @Override
    public void hlasuj(String kandidat) {
        System.out.println("Online voting for " + kandidat);
    }
}