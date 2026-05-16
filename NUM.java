public abstract class NUM extends Capteur {
    private double temperature;
    public NUM() {
        super();
    }
    public NUM(double temperature) {
        super();
        this.temperature = temperature;
    }
    public double getTemperature() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
