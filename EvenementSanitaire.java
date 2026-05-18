import java.time.LocalDate;
public class EvenementSanitaire {
    private Animal hayawan;
    private LocalDate date;
    private String description;
    private double variationPoids;
    
    public EvenementSanitaire() {
    }
    
    public EvenementSanitaire(Animal hayawan, LocalDate date, String description, double variationPoids) {
        this.hayawan = hayawan;
        this.date = date;
        this.description = description;
        this.variationPoids = variationPoids;
    }
    public Animal getAnimal() {
        return hayawan;
    }
    public void setAnimal(Animal hayawan) {
        this.hayawan = hayawan;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public double getVariationPoids() {
        return variationPoids;
    }
    public void setVariationPoids(double variationPoids) {
        this.variationPoids = variationPoids;
    }
}
