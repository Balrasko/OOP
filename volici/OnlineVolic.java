package volici;

import hlasovanie.*;

/**
 * Trieda {@code OnlineVolic} rozširuje základnú triedu {@code Volic} a špecializuje ju
 * pre voličov, ktorí uprednostňujú online hlasovanie.
 * <p>
 * V konštruktore sa inicializuje {@code OnlineVolic} s osobnými údajmi a nastaví
 * možnosť hlasovania prostredníctvom internetu.
 * </p>
 */
class OnlineVolic extends Volic {

    /**
     * Konštruktor pre {@code OnlineVolic}, ktorý inicializuje voliča s danými atribútmi
     * a pridáva metódu online hlasovania.
     * 
     * @param meno Meno voliča.
     * @param adresa Adresa voliča.
     * @param vek Vek voliča.
     * @param stat Štát, kde volič žije.
     * @param ID Identifikačné číslo voliča.
     * @param psc Poštové smerovacie číslo voliča.
     */
    public OnlineVolic(String meno, String adresa, int vek, String stat, String ID, int psc) {
        super(meno, adresa, vek, stat, ID, psc);
        addMetodaHlasovania(new OnlineHlasovanie());
    }
}