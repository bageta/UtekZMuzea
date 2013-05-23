package game;

/**
 * Enum pro reprezentaci jednotlivých typů překážek a předmětů k jejich odstranění.
 * Hodnoty odpovídají jedntolivým typům a jsou jasně rozlišitelné dle názvů.
 * @author Pavel Pilař
 */
public enum ObstacleType {
    GLASS,
    DOG,
    FIRE,
    FLASH;
    
    public String toButtonText(){
        switch(this){
            case GLASS:
                return "Sklo";
            case DOG:
                return "Pes";
            case FIRE:
                return "Ohen";
            case FLASH:
                return "Blesk:";
            default:
                return "neplatný typ překážky";
        }
    }
}
