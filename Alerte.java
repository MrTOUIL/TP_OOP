import java.time.LocalDate;
public class Alerte {
    private Gravite grv;
    private LocalDate date;
    private Releve releve;
    private boolean acquittee;
    private String message;
    public Alerte(Gravite grv, LocalDate date) {
        this.grv = grv;
        this.date = date;
        this.acquittee = false;
    }
    public Gravite getGrv() {
        return grv;
    }
    public void setGrv(Gravite grv) {
        this.grv = grv;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public Releve getReleve() {
        return releve;
    }
    public void setReleve(Releve releve) {
        this.releve = releve;
    }
    public boolean isAcquittee() {
        return acquittee;
    }
    public void acquitter() {
        this.acquittee = true;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
