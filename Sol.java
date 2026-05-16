public class Sol extends NUM {
    private double PH;
    private double humidite;
    private double Azote;
    public Sol() {
        super();
    }
    public Sol(double temperature, double PH, double humidite, double azote) {
        super(temperature);
        this.PH = PH;
        this.humidite = humidite;
        this.Azote = azote;
    }
    public double getPH() {
        return PH;
    }
    public void setPH(double PH) {
        this.PH = PH;
    }
    public double getHumidite() {
        return humidite;
    }
    public void setHumidite(double humidite) {
        this.humidite = humidite;
    }
    public double getAzote() {
        return Azote;
    }
    public void setAzote(double azote) {
        Azote = azote;
    }
}
