public class Biometrique extends NUM {
    private GPS loc;
    private int niveau_activite;
    public Biometrique() {
        super();
    }
    public Biometrique(double temperature, GPS loc, int niveau_activite) {
        super(temperature);
        this.loc = loc;
        this.niveau_activite = niveau_activite;
    }
    public GPS getLoc() {
        return loc;
    }
    public void setLoc(GPS loc) {
        this.loc = loc;
    }
    public int getNiveau_activite() {
        return niveau_activite;
    }
    public void setNiveau_activite(int niveau_activite) {
        this.niveau_activite = niveau_activite;
    }
}
