package zone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modele.Plantation;
import modele.Stadedecroissance;

public class Culture extends ZoneGeographique {

    private List<Plantation> terre = new ArrayList<>();
    private double seuil_abs;
    private double seuil_ord;

    public Culture(String nom) {
        super(nom);
    }

    public List<Plantation> getTerre() {
        return Collections.unmodifiableList(terre);
    }

    public double getSeuil_abs() {
        return seuil_abs;
    }

    public void setSeuil_abs(double seuil_abs) {
        this.seuil_abs = seuil_abs;
    }

    public double getSeuil_ord() {
        return seuil_ord;
    }

    public void setSeuil_ord(double seuil_ord) {
        this.seuil_ord = seuil_ord;
    }

    public boolean ajouterPlantation(Plantation plantation) {
        if (plantation == null) {
            return false;
        }
        boolean ajoute = terre.add(plantation);
        if (ajoute) {
            nb_especes = terre.size();
        }
        return ajoute;
    }

    public boolean retirerPlantation(Plantation plantation) {
        if (plantation == null) {
            return false;
        }
        boolean supprime = terre.remove(plantation);
        if (supprime) {
            nb_especes = terre.size();
        }
        return supprime;
    }

    public void mettreAJourStade(Plantation plantation, Stadedecroissance stade) {
        if (plantation != null) {
            plantation.setEpan(stade);
        }
    }
}