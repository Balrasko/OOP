package volici;
import hlasovanie.*;

/**
 * Trieda {@code VolicPostov} rozširuje základnú triedu {@code Volic}
 * a špecializuje sa na voličov, ktorí preferujú poštové hlasovanie.
 * <p>
 * Pri inicializácii trieda automaticky pridáva metódu poštového hlasovania do zoznamu dostupných
 * metód hlasovania pre voliča.
 * </p>
 */
class VolicPostov extends Volic {

    /**
     * Konštruktor pre {@code VolicPostov}, ktorý inicializuje voliča s danými atribútmi
     * a pridáva metódu poštového hlasovania.
     * 
     * @param meno Meno voliča.
     * @param adresa Adresa voliča.
     * @param vek Vek voliča.
     * @param stat Štát, kde volič žije.
     * @param ID Identifikačné číslo voliča.
     * @param psc Poštové smerovacie číslo voliča.
     */
    public VolicPostov(String meno, String adresa, int vek, String stat, String ID, int psc) {
        super(meno, adresa, vek, stat, ID, psc);
        addMetodaHlasovania(new PostoveHlasovanie());
    }
}