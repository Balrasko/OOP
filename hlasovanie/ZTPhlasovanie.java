package hlasovanie;

import volici.Volic;  // Import triedy Volič, ktorá umožňuje správu voličov.

/**
 * Trieda {@code ZTPhlasovanie} implementuje rozhranie {@code Hlasovanie2}
 * a poskytuje prispôsobenú metódu na hlasovanie pre ľudí so špeciálnymi potrebami.
 * <p>
 * Táto implementácia zobrazuje na štandardný výstup meno kandidáta s príslušnou
 * poznámkou, že ide o hlasovanie pre ľudí so špeciálnymi potrebami.
 * </p>
 */
public class ZTPhlasovanie implements Hlasovanie2 {

    /**
     * Vykoná hlasovanie pre zadaného kandidáta s dôrazom na prístupnosť pre ľudí
     * so špeciálnymi potrebami.
     * <p>
     * Metóda prijíma meno kandidáta a vypisuje správu, ktorá upozorňuje na špecifickú
     * podporu pri hlasovaní pre daného kandidáta.
     * </p>
     * 
     * @param kandidat Meno kandidáta, pre ktorého sa hlasuje.
     */
    @Override
    public void hlasuj(String kandidat) {
        System.out.println("Voting for special needs: " + kandidat);
    }
}