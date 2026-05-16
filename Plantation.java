import java.time.LocalDate;
public class Plantation {
    private LocalDate date_plant;
    private LocalDate date_rec;
    private Stadedecroissance epan;
    private double phMax;
    private double phMin;
    private double humidite;
    public Plantation(LocalDate date_plant, LocalDate date_rec, Stadedecroissance epan, double phMax, double phMin, double humidite) {
        this.date_plant = date_plant;
        this.date_rec = date_rec;
        this.epan = epan;
        this.phMax = phMax;
        this.phMin = phMin;
        this.humidite = humidite;
    }
    public LocalDate getDate_plant() {
        return date_plant;
    }
    public void setDate_plant(LocalDate date_plant) {
        this.date_plant = date_plant;
    }
    public LocalDate getDate_rec() {
        return date_rec;
    }
    public void setDate_rec(LocalDate date_rec) {
        this.date_rec = date_rec;
    }
    public Stadedecroissance getEpan() {
        return epan;
    }
    public void setEpan(Stadedecroissance epan) {
        this.epan = epan;
    }
    public double getPhMax() {
        return phMax;
    }
    public void setPhMax(double phMax) {
        this.phMax = phMax;
    }
    public double getPhMin() {
        return phMin;
    }
    public void setPhMin(double phMin) {
        this.phMin = phMin;
    }
    public double getHumidite() {
        return humidite;
    }
    public void setHumidite(double humidite) {
        this.humidite = humidite;
    }
}
