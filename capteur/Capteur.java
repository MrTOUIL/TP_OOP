package capteur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import zone.Suspendable;
import zone.ZoneGeographique;

public abstract class Capteur implements Suspendable {

    protected UUID id = UUID.randomUUID();
    protected boolean actif;
    protected Statut stat;
    protected List<Releve> rel = new ArrayList<>();
    protected ZoneGeographique zone;
    protected double seuilMin;
    protected double seuilMax;

    public Capteur() {
        this.actif = true;
        this.stat = Statut.Actif;
    }

    public UUID getId() {
        return id;
    }

    public boolean isActif() {
        return actif;
    }

    public Statut getStat() {
        return stat;
    }

    public void setStat(Statut stat) {
        this.stat = stat;
    }

    public List<Releve> getRel() {
        return Collections.unmodifiableList(rel);
    }

    public ZoneGeographique getZone() {
        return zone;
    }

    public void setZone(ZoneGeographique zone) {
        this.zone = zone;
    }

    public double getSeuilMin() {
        return seuilMin;
    }

    public double getSeuilMax() {
        return seuilMax;
    }

    public void configurerSeuils(double seuilMin, double seuilMax) {
        this.seuilMin = seuilMin;
        this.seuilMax = seuilMax;
    }

    public void ajouterReleve(Releve releve) {
        if (releve != null) {
            rel.add(releve);
        }
    }

    public boolean estHorsSeuil(double valeur) {
        return valeur < seuilMin || valeur > seuilMax;
    }

    @Override
    public void activer() {
        this.actif = true;
        this.stat = Statut.Actif;
    }

    @Override
    public void desactiver() {
        this.actif = false;
        this.stat = Statut.Suspendu;
    }

    public void defaillir() {
        this.actif = false;
        this.stat = Statut.Defaillant;
    }
}