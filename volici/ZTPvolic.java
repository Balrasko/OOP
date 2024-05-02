package volici;
import hlasovanie.*;

/**
 * Trieda {@code ZTPvolic} rozširuje základnú triedu {@code Volic}
 * a špecializuje sa na voličov so špeciálnymi potrebami, ktorí potrebujú prístupné hlasovacie metódy.
 * <p>
 * Pri inicializácii trieda automaticky pridáva metódu hlasovania pre ľudí so špeciálnymi potrebami
 * do zoznamu dostupných metód hlasovania pre voliča.
 * </p>
 */
class ZTPvolic extends Volic {

    /**
     * Konštruktor pre {@code ZTPvolic}, ktorý inicializuje voliča s danými atribútmi
     * a pridáva metódu hlasovania určenú pre voličov so špeciálnymi potrebami.
     * 
     * @param meno Meno voliča.
     * @param adresa Adresa voliča.
     * @param vek Vek voliča.
     * @param stat Štát, kde volič žije.
     * @param ID Identifikačné číslo voliča.
     * @param psc Poštové smerovacie číslo voliča.
     */
    public ZTPvolic(String meno, String adresa, int vek, String stat, String ID, int psc) {
        super(meno, adresa, vek, stat, ID, psc);
        addMetodaHlasovania(new ZTPhlasovanie());
    }
}