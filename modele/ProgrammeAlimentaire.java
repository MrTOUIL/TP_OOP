
package modele;

public class ProgrammeAlimentaire {

    private double quantity;
    private String typealiment;

    public ProgrammeAlimentaire() {
    }

    public ProgrammeAlimentaire(double quantity, String typealiment) {
        this.quantity = quantity;
        this.typealiment = typealiment;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getTypealiment() {
        return typealiment;
    }

    public void setTypealiment(String typealiment) {
        this.typealiment = typealiment;
    }
}