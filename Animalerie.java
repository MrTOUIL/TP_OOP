import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
public class Animalerie extends ZoneGeographique {
    private Set<Animal> kouri = new HashSet<>();
    private ProgrammeAlimentaire programmeAlimentaire;
    public Animalerie(String nom) {
        super(nom);
    }
    public Set<Animal> getKouri() {
        return Collections.unmodifiableSet(kouri);
    }
    public boolean ajouterAnimal(Animal animal) {
        if (animal == null) {
            return false;
        }
        boolean ajoute = kouri.add(animal);
        if (ajoute) {
            nb_especes = kouri.size();
        }
        return ajoute;
    }
    public boolean retirerAnimal(Animal animal) {
        if (animal == null) {
            return false;
        }
        boolean supprime = kouri.remove(animal);
        if (supprime) {
            nb_especes = kouri.size();
        }
        return supprime;
    }
    public ProgrammeAlimentaire getProgrammeAlimentaire() {
        return programmeAlimentaire;
    }
    public void setProgrammeAlimentaire(ProgrammeAlimentaire programmeAlimentaire) {
        this.programmeAlimentaire = programmeAlimentaire;
    }
}
