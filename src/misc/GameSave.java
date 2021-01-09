package misc;

import java.util.Date;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.*;

/**
 * Collections d'informations permettant de définir une sauvegarde
 */
public class GameSave {
    private Date date;
    private int troopCount;
    private int minutesPlayed;
    private BufferedImage gameImg;

    /**
     * Construit une nouvelle sauvegarde
     * @param date la date de la sauvegarde
     * @param troopCount le nombre de troupes restante au joueur pour cette sauvegarde
     * @param minutesPlayed le nombre de minutes jouées dans cette sauvegarde
     * @param gameImg capture d'écran du jeu sauvegardé
     */
    public GameSave(Date date, int troopCount, int minutesPlayed, BufferedImage gameImg) {
        setDate(date);
        setTroopCount(troopCount);
        setMinutesPlayed(minutesPlayed);
        setGameImg(gameImg);
    }
    
    /**
     * @return la date de la sauvegarde
     */
    public Date getDate() {
        return this.date;
    }
    
    /**
     * @param date la nouvelle date de sauvegarde
     */
    public void setDate(Date date) {
        this.date = date;
    }
    
    /**
     * @return le nombre de troupes restante au joueur pour cette sauvegarde
     */
    public int getTroopCount() {
        return this.troopCount;
    }
    
    /**
     * @param troopCount le nombre de troupes restante au joueur pour cette sauvegarde
     */
    public void setTroopCount(int troopCount) {
        this.troopCount = troopCount;
    }
    
    /**
     * @return le nombre de minutes jouées dans cette sauvegarde
     */
    public int getMinutesPlayed() {
        return this.minutesPlayed;
    }
    
    /**
     * @param minutesPlayed le nouveau nombre de minutes jouées dans cette sauvegarde
     */
    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    /**
     * @return l'image du jeu lors de la sauvegarde
     */
    public BufferedImage getGameImg() {
        return this.gameImg;
    }

    /**
     * @param gameImg nouvelle capture d'écran du jeu sauvegardé
     */
    public void setGameImg (BufferedImage gameImg) {
        if (this.gameImg != null) this.gameImg.flush();
        this.gameImg = gameImg;
    }
}