package misc;

import java.util.Date;
import javax.swing.ImageIcon;
import terrains.Carte;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Collections d'informations permettant de définir une sauvegarde
 */
public class GameSave implements Serializable {
    private static final long serialVersionUID = 7876832580220608393L;
    private Date date;
    private int troopCount;
    private int minutesPlayed;
    private ImageIcon gameImg;
    private final int numSauvegarde;
    private String carteSaveName;

    /**
     * Construit une nouvelle sauvegarde
     * @param date la date de la sauvegarde
     * @param troopCount le nombre de troupes restante au joueur pour cette sauvegarde
     * @param minutesPlayed le nombre de minutes jouées dans cette sauvegarde
     * @param gameImg capture d'écran du jeu sauvegardé
     * @param c la carte à enregistrer
     */
    public GameSave(Date date, int troopCount, int minutesPlayed, ImageIcon gameImg, Carte c) {
        setDate(date);
        setTroopCount(troopCount);
        setMinutesPlayed(minutesPlayed);
        setGameImg(gameImg);

        String tmp = Parametres.getParametre("nbSauvegardes");
        if (tmp == null) numSauvegarde = 1;
        else numSauvegarde = Integer.valueOf(tmp) + 1;

        carteSaveName = enregistrementCarte(c, numSauvegarde);
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
    public ImageIcon getGameImg() {
        return this.gameImg;
    }

    /**
     * @param gameImg nouvelle capture d'écran du jeu sauvegardé
     */
    public void setGameImg (ImageIcon gameImg) {
        this.gameImg = gameImg;
    }

    /**
     * @return le numéro permettant d'idenfier le fichier de cette save
     */
    public int getNumSauvegarde() {
        return this.numSauvegarde;
    }

    /**
     * @return le nom du fichier sauvegarde de la carte associé
     */
    public String getCarteSaveName() {
        return this.carteSaveName;
    }

    /**
     * Enregistre la carte c
     * @param c carte a enregistrer
     * @param numSauvegarde le numéro de la sauvegarde
     * @return le nom du fichier crée ou null si l'enregistrement a échoué
     */
    private static String enregistrementCarte (Carte c, int numSauvegarde) {
        String fileName = "sauvegardeCarte" + numSauvegarde + ".save";
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(c);
                objectOutputStream.flush();
                return fileName;
            } catch (IOException|SecurityException|NullPointerException e) {
                System.err.println(e.getLocalizedMessage());
                return null;
            }
        } catch (SecurityException|IOException e) {
            System.err.println(e.getLocalizedMessage());
            return null;
        }
    }

    /**
     * Charge et renvoie la carte associé à la save courante
     * @return une carte
     * @see Carte
     */
    public Carte recuperationCarte () {
        try (InputStream fileInputStream = GameSave.class.getResourceAsStream("/" + carteSaveName)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (Carte) objectInputStream.readObject();
            } catch (IOException|SecurityException|ClassNotFoundException e) {
                return null;
            } catch (NullPointerException e) {
                // On cherche essaye avec FileInputStream
                try (InputStream fileInputStream2 = new FileInputStream(carteSaveName)) {
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream2)) {
                        return (Carte) objectInputStream.readObject();
                    } catch (Exception e2) {
                        return null;
                    }
                } catch (SecurityException|IOException e2) {
                    return null;
                }
            }
        } catch (SecurityException|IOException e) {
            return null;
        }
    }

    /**
     * Enregistre la save courante
     */
    public void enregistrement () {
        String fileName = "sauvegarde" + numSauvegarde + ".save";
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(this);
                objectOutputStream.flush();
                Parametres.setParametre("nbSauvegardes", String.valueOf(numSauvegarde));
            } catch (IOException|SecurityException|NullPointerException e) {
                System.err.println(e.getLocalizedMessage());
            }
        } catch (SecurityException|IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    /**
     * Charge et renvoi la save au nom donnée
     * @param fileName le nom de la save
     * @return une GameSave
     */
    public static GameSave recuperation (String fileName) {
        try (InputStream fileInputStream = GameSave.class.getResourceAsStream("/" + fileName)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                return (GameSave) objectInputStream.readObject();
            } catch (IOException|SecurityException|ClassNotFoundException e) {
                return null;
            } catch (NullPointerException e) {
                // On cherche essaye avec FileInputStream
                try (InputStream fileInputStream2 = new FileInputStream(fileName)) {
                    try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream2)) {
                        return (GameSave) objectInputStream.readObject();
                    } catch (Exception e2) {
                        return null;
                    }
                } catch (SecurityException|IOException e2) {
                    return null;
                }
            }
        } catch (SecurityException|IOException e) {
            return null;
        }
    }
}