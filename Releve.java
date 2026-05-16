import java.time.LocalDateTime;
public abstract class Releve {
    private Alerte alt;
    private LocalDateTime horodatage;
    private Capteur capteur;
    public Releve() {
        this.horodatage = LocalDateTime.now();
    }
    public Releve(Capteur capteur, LocalDateTime horodatage) {
        this.capteur = capteur;
        this.horodatage = horodatage == null ? LocalDateTime.now() : horodatage;
    }
    public Alerte getAlt() {
        return alt;
    }
    public void setAlt(Alerte alt) {
        this.alt = alt;
    }
    public LocalDateTime getHorodatage() {
        return horodatage;
    }
    public void setHorodatage(LocalDateTime horodatage) {
        this.horodatage = horodatage;
    }
    public Capteur getCapteur() {
        return capteur;
    }
    public void setCapteur(Capteur capteur) {
        this.capteur = capteur;
    }
}
