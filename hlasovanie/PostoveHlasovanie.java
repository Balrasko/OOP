package hlasovanie;

import volici.Volic;  // Import triedy Volič, ktorá umožňuje správu voličov.

/**
 * Trieda {@code PostoveHlasovanie} implementuje rozhranie {@code Hlasovanie2}
 * a poskytuje metódu na hlasovanie poštou.
 * <p>
 * Táto implementácia jednoducho vypíše meno kandidáta, pre ktorého sa hlasuje poštou,
 * na štandardný výstup.
 * </p>
 */
public class PostoveHlasovanie implements Hlasovanie2 {

    /**
     * Vykoná hlasovanie pre zadaného kandidáta prostredníctvom pošty.
     * <p>
     * Metóda prijíma meno kandidáta a vypisuje správu o tom, že sa pre daného kandidáta
     * hlasovalo poštou.
     * </p>
     * 
     * @param kandidat Meno kandidáta, pre ktorého sa hlasuje.
     */
    @Override
    public void hlasuj(String kandidat) {
        System.out.println("Postal voting for " + kandidat);
    }
}