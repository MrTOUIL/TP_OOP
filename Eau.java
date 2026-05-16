public class Eau extends NUM {
    private double oxygene;
    public Eau() {
        super();
    }
    public Eau(double temperature, double oxygene) {
        super(temperature);
        this.oxygene = oxygene;
    }
    public double getOxygene() {
        return oxygene;
    }
    public void setOxygene(double oxygene) {
        this.oxygene = oxygene;
    }
}
