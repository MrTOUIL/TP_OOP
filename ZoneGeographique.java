
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
public abstract class ZoneGeographique implements Suspendable {
    protected UUID id = UUID.randomUUID();
    protected String nom;
    protected boolean actif;
    protected Set<Capteur> maintenance = new HashSet<>();    //ykhdem b les capteurs (Uniques)
    protected List<Double> production = new ArrayList<>();
    protected List<Releve> rel = new ArrayList<>();
    protected List<Alerte> alt = new ArrayList<>();
    protected int nb_especes;
    public ZoneGeographique(String nom) {
        this.nom = nom;
        this.actif = true;
    }
    public UUID getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public boolean isActif() {
        return actif;
    }
    public Set<Capteur> getMaintenance() {
        return Collections.unmodifiableSet(maintenance);
    }
    public List<Double> getProduction() {
        return Collections.unmodifiableList(production);
    }
    public List<Releve> getRel() {
        return Collections.unmodifiableList(rel);   //d'apres la documentation
    }
    public List<Alerte> getAlt() {        
        return Collections.unmodifiableList(alt);
    }
    public int getNb_especes() {
        return nb_especes;
    }
    public void setNb_especes(int nb_especes) {
        this.nb_especes = nb_especes;
    }
    public boolean ajouterCapteur(Capteur capteur) {
        if (capteur == null) {
            return false;
        }
        boolean ajoute = maintenance.add(capteur);     //chouf el cahier
        if (ajoute) {
            capteur.setZone(this);
        }
        return ajoute;
    }
    public boolean retirerCapteur(Capteur capteur) {
        if (capteur == null) {
            return false;
        }
        boolean supprime = maintenance.remove(capteur);     //kifkif
        if (supprime) {
            capteur.setZone(null);
        }
        return supprime;
    }
    public void ajouterReleve(Releve releve) {
        if (releve != null) {
            rel.add(releve);
        }
    }
    public void ajouterAlerte(Alerte alerte) {
        if (alerte != null) {
            alt.add(alerte);
        }
    }
    public boolean retirerAlerte(Alerte alerte) {
        return alerte != null && alt.remove(alerte);
    }
    public void enregistrerProduction(double valeur) {
        production.add(valeur);
    }
    public int getNombreEntites() {
        return nb_especes;
    }
    @Override
    public void activer() {
        this.actif = true;
        for (Capteur capteur : maintenance) {
            capteur.activer();       // ta3 el interface
        }
    }
    @Override
    public void desactiver() {
        this.actif = false;
        for (Capteur capteur : maintenance) {   //kifkif
            capteur.desactiver();
        }
    }
}
