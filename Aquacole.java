import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Aquacole extends ZoneGeographique {
    private List<Poisson> aquarium = new ArrayList<>();
    private ProgrammeAlimentaire programmeAlimentaire;
    
    
    public Aquacole(String nom) {
        super(nom);
    }
    public List<Poisson> getAquarium() {
        return Collections.unmodifiableList(aquarium);    //chouf el cour ta3 les collections tfhm
    }
    public boolean ajouterPoisson(Poisson poisson) {
        if (poisson == null) {
            return false;
        }
        boolean ajoute = aquarium.add(poisson);
        if (ajoute) {
            nb_especes = aquarium.size();     // psq protectred
        }
        return ajoute;
    }
    public boolean retirerPoisson(Poisson poisson) {
        if (poisson == null) {
            return false;
        }
        boolean supprime = aquarium.remove(poisson);
        if (supprime) {
            nb_especes = aquarium.size();   //kifkif kho
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
