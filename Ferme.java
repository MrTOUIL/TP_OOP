 
import zone.*;
import capteur.Alerte;
import capteur.Capteur;
import capteur.Gravite;
import capteur.ReleveGPS;
import capteur.Releve;
import capteur.ReleveNum;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import modele.Animal;
import modele.EvenementSanitaire;
import modele.Plantation;
import modele.Poisson;

public class Ferme {

    private final List<ZoneGeographique> zones = new ArrayList<>();
    private final List<EvenementSanitaire> evenementsSanitaires = new ArrayList<>();
    private final List<Alerte> historiqueAlertes = new ArrayList<>();

    public boolean ajouterZone(ZoneGeographique zone) {
        return zone != null && zones.add(zone);
    }

    public List<ZoneGeographique> getZones() {
        return Collections.unmodifiableList(zones);
    }

    public ZoneGeographique chercherZoneParId(UUID id) {
        if (id == null) {
            return null;
        }
        for (ZoneGeographique zone : zones) {
            if (id.equals(zone.getId())) {
                return zone;
            }
        }
        return null;
    }

    public void activerZone(UUID id) {
        ZoneGeographique zone = chercherZoneParId(id);
        if (zone != null) {
            zone.activer();
        }
    }

    public void suspendreZone(UUID id) {
        ZoneGeographique zone = chercherZoneParId(id);
        if (zone != null) {
            zone.desactiver();
        }
    }

    public boolean ajouterCapteur(ZoneGeographique zone, Capteur capteur) {
        return zone != null && zone.ajouterCapteur(capteur);
    }

    public boolean ajouterAnimal(Animalerie zone, Animal animal) {
        return zone != null && zone.ajouterAnimal(animal);
    }

    public boolean ajouterCulture(Culture zone, Plantation plantation) {
        return zone != null && zone.ajouterPlantation(plantation);
    }

    public boolean ajouterPoisson(Aquacole zone, Poisson poisson) {
        return zone != null && zone.ajouterPoisson(poisson);
    }

    public void enregistrerProduction(ZoneGeographique zone, double valeur) {
        if (zone != null) {
            zone.enregistrerProduction(valeur);
        }
    }

    public void enregistrerEvenementSanitaire(EvenementSanitaire evenement) {
        if (evenement != null) {
            evenementsSanitaires.add(evenement);
        }
    }

    public List<EvenementSanitaire> getEvenementsSanitaires() {
        return Collections.unmodifiableList(evenementsSanitaires);
    }

    public void enregistrerReleve(Capteur capteur, Releve releve) {
        if (capteur == null || releve == null || !capteur.isActif()) {
            return;
        }
        releve.setCapteur(capteur);
        capteur.ajouterReleve(releve);
        ZoneGeographique zone = capteur.getZone();
        if (zone != null) {
            zone.ajouterReleve(releve);
        }
        if (releve instanceof ReleveNum) {
            ReleveNum numerique = (ReleveNum) releve;
            if (capteur.estHorsSeuil(numerique.getValeur())) {
                declencherAlerte(zone, releve, "Valeur numerique hors seuil: " + numerique.getValeur());
            }
        }
        if (releve instanceof ReleveGPS) {
            enregistrerReleveGps(capteur, (ReleveGPS) releve);
        }
    }

    public boolean enregistrerReleveGps(Capteur capteur, ReleveGPS releveGps) {
        if (capteur == null || releveGps == null || !capteur.isActif()) {
            return false;
        }

        releveGps.setCapteur(capteur);
        capteur.ajouterReleve(releveGps);
        ZoneGeographique zone = capteur.getZone();
        if (zone != null) {
            zone.ajouterReleve(releveGps);
        }

        boolean latitudeHorsSeuil = capteur.estHorsSeuil(releveGps.getLatitude());
        boolean longitudeHorsSeuil = capteur.estHorsSeuil(releveGps.getLongitude());
        if (latitudeHorsSeuil || longitudeHorsSeuil) {
            declencherAlerte(zone, releveGps, "Position GPS hors seuil: lat=" + releveGps.getLatitude() + ", lon=" + releveGps.getLongitude());
            return true;
        }

        return false;
    }

    private void declencherAlerte(ZoneGeographique zone, Releve releve, String message) {
        Alerte alerte = new Alerte(Gravite.Critique, LocalDate.now());
        alerte.setReleve(releve);
        alerte.setMessage(message);
        releve.setAlt(alerte);
        historiqueAlertes.add(alerte);
        if (zone != null) {
            zone.ajouterAlerte(alerte);
        }
    }

    public List<Alerte> alertesActives() {
        List<Alerte> resultat = new ArrayList<>();
        for (Alerte alerte : historiqueAlertes) {
            if (!alerte.isAcquittee()) {
                resultat.add(alerte);
            }
        }
        resultat.sort((a, b) -> b.getGrv().compareTo(a.getGrv()));
        return resultat;
    }

    public void acquitterAlerte(Alerte alerte) {
        if (alerte != null) {
            alerte.acquitter();
        }
    }

    public void supprimerAlerte(ZoneGeographique zone, Alerte alerte) {
        if (zone != null && alerte != null) {
            zone.retirerAlerte(alerte);
        }
    }

    public String resumeZones() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : zones) {
            builder.append(zone.getId())
                   .append(" | ")
                   .append(zone.getNom())
                   .append(" | actif=")
                   .append(zone.isActif())
                   .append(" | entites=")
                   .append(zone.getNombreEntites())
                   .append(System.lineSeparator());
        }
        return builder.toString();
    }
}
