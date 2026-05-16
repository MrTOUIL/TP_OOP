public class Environemental extends NUM {
    private double humidite;
    private double pluviometrie;
    public Environemental() {
        super();
    }
    public Environemental(double temperature, double humidite, double pluviometrie) {
        super(temperature);
        this.humidite = humidite;
        this.pluviometrie = pluviometrie;
    }
    public double getHumidite() {
        return humidite;
    }
    public void setHumidite(double humidite) {
        this.humidite = humidite;
    }
    public double getPluviometrie() {
        return pluviometrie;
    }
    public void setPluviometrie(double pluviometrie) {
        this.pluviometrie = pluviometrie;
    }
}
