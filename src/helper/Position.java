package helper;

import com.jme3.math.Vector3f;

/**
 * Třída obsahující statické metody pro určování pozice,které se mohou hodit v různých
 * místech kódu a nechceme aby byli závislé na konkrétní instanci nějaké třídy.
 * @author Pavel Pilař
 */
public class Position {
    
    /**
     * Metoda určující zda jsou dva objekty v určité vzdálenosti od sebe. V rámci
     * os x a z. Kvůli výkonu se nepočítá vzdálenost, ale obě souřadnice musí
     * být dostatečně blízko.
     * @param position1 pozice prvního objektu
     * @param position2 pozice druhého objektu
     * @param diff požadovaná vzdálenost
     * @return true, pokud jsou objekty dostatečně blízko, jinak false
     */
    public static boolean isClose(Vector3f position1, Vector3f position2, float diff){
        return (Math.abs(position1.x-position2.x)<diff &&
                Math.abs(position1.z - position2.z)<diff);
    }
    
}