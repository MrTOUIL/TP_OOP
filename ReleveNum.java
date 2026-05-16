import java.time.LocalDateTime;
public class ReleveNum extends Releve {
    private double valeur;
    private String unite;
    public ReleveNum() {
        super();
    }
    public ReleveNum(Capteur capteur, double valeur, String unite, LocalDateTime horodatage) {
        super(capteur, horodatage);
        this.valeur = valeur;
        this.unite = unite;
    }
    public double getValeur() {
        return valeur;
    }
    public void setValeur(double valeur) {
        this.valeur = valeur;
    }
    public String getUnite() {
        return unite;
    }
    public void setUnite(String unite) {
        this.unite = unite;
    }
}
