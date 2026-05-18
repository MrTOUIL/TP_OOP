import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Ferme {

    private final List<ZoneGeographique> zones = new ArrayList<>();
    private final List<EvenementSanitaire> evenementsSanitaires = new ArrayList<>();
    private final List<Alerte> historiqueAlertes = new ArrayList<>();

    public boolean ajouterZone(ZoneGeographique zone) throws FermeException {
        if (zone == null) {
            throw new FermeException("Erreur: La zone ne peut pas être null");
        }
        return zones.add(zone);
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

    public boolean ajouterCapteur(ZoneGeographique zone, Capteur capteur) throws FermeException {
        if (zone == null) {
            throw new FermeException("Erreur: La zone ne peut pas être null");
        }
        if (capteur == null) {
            throw new FermeException("Erreur: Le capteur ne peut pas être null");
        }
        if (capteur.getSeuilMin() >= capteur.getSeuilMax()) {
            throw new FermeException("Erreur: Le seuil minimum doit être inférieur au seuil maximum");
        }
        return zone.ajouterCapteur(capteur);
    }

    public boolean ajouterAnimal(Animalerie zone, Animal animal) throws FermeException {
        if (zone == null) {
            throw new FermeException("Erreur: La zone animalerie ne peut pas être null");
        }
        if (animal == null) {
            throw new FermeException("Erreur: L'animal ne peut pas être null");
        }
        if (animal.getAge() < 0) {
            throw new FermeException("Erreur: L'âge de l'animal ne peut pas être négatif");
        }
        if (animal.getPoids() <= 0) {
            throw new FermeException("Erreur: Le poids de l'animal doit être positif");
        }
        return zone.ajouterAnimal(animal);
    }

    public boolean ajouterCulture(Culture zone, Plantation plantation) throws FermeException {
        if (zone == null) {
            throw new FermeException("Erreur: La zone culture ne peut pas être null");
        }
        if (plantation == null) {
            throw new FermeException("Erreur: La plantation ne peut pas être null");
        }
        if (plantation.getDate_plant() == null || plantation.getDate_rec() == null) {
            throw new FermeException("Erreur: Les dates de plantation et de récolte ne peuvent pas être null");
        }
        if (plantation.getDate_plant().isAfter(plantation.getDate_rec())) {
            throw new FermeException("Erreur: La date de plantation doit être antérieure à la date de récolte");
        }
        return zone.ajouterPlantation(plantation);
    }

    public boolean ajouterPoisson(Aquacole zone, Poisson poisson) throws FermeException {
        if (zone == null) {
            throw new FermeException("Erreur: La zone aquacole ne peut pas être null");
        }
        if (poisson == null) {
            throw new FermeException("Erreur: Le poisson ne peut pas être null");
        }
        return zone.ajouterPoisson(poisson);
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

    public void enregistrerReleve(Capteur capteur, Releve releve) throws FermeException {
        if (capteur == null) {
            throw new FermeException("Erreur: Le capteur ne peut pas être null");
        }
        if (releve == null) {
            throw new FermeException("Erreur: Le relevé ne peut pas être null");
        }
        if (!capteur.isActif()) {
            throw new FermeException("Erreur: Le capteur n'est pas actif");
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

    public boolean enregistrerReleveGps(Capteur capteur, ReleveGPS releveGps) throws FermeException {
        if (capteur == null) {
            throw new FermeException("Erreur: Le capteur ne peut pas être null");
        }
        if (releveGps == null) {
            throw new FermeException("Erreur: Le relevé GPS ne peut pas être null");
        }
        if (!capteur.isActif()) {
            throw new FermeException("Erreur: Le capteur n'est pas actif");
        }
        
        // Valider les coordonnées GPS
        if (releveGps.getLatitude() < -90 || releveGps.getLatitude() > 90) {
            throw new FermeException("Erreur: Latitude invalide (doit être entre -90 et 90)");
        }
        if (releveGps.getLongitude() < -180 || releveGps.getLongitude() > 180) {
            throw new FermeException("Erreur: Longitude invalide (doit être entre -180 et 180)");
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

    public List<Alerte> getHistoriqueAlertes() {
        return Collections.unmodifiableList(historiqueAlertes);
    }

    public List<Releve> getHistoriqueReleves() {
        List<Releve> releves = new ArrayList<>();
        for (ZoneGeographique zone : zones) {
            releves.addAll(zone.getRel());
        }
        return releves;
    }

    // ============================================================
    // MAIN ET MENU INTERACTIF
    // ============================================================

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Ferme ferme = new Ferme();
        
        afficherBienvenue();

        boolean continuer = true;
        while (continuer) {
            afficherMenuPrincipal();
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    menuGestionZones(scanner, ferme);
                    break;
                case "2":
                    menuGestionCultures(scanner, ferme);
                    break;
                case "3":
                    menuGestionAnimaux(scanner, ferme);
                    break;
                case "4":
                    menuGestionCapteurs(scanner, ferme);
                    break;
                case "5":
                    menuGestionAlertes(scanner, ferme);
                    break;
                case "6":
                    afficherResume(ferme);
                    break;
                case "0":
                    System.out.println("\n╔════════════════════════════════════════╗");
                    System.out.println("║    Merci d'avoir utilisé Smart Farm!    ║");
                    System.out.println("╚════════════════════════════════════════╝");
                    continuer = false;
                    break;
                default:
                    System.out.println(" Choix invalide. Veuillez réessayer.");
            }
        }
        scanner.close();
    }

    private static void afficherBienvenue() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║     BIENVENUE DANS SMART FARMING v1.0              ║");
        System.out.println("║  Système de Gestion Intelligent de Ferme           ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }

    private static void afficherMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              MENU PRINCIPAL                         ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Gérer les zones et entités                      ║");
        System.out.println("║ 2. Gérer les cultures                              ║");
        System.out.println("║ 3. Gérer les animaux                               ║");
        System.out.println("║ 4. Gérer les capteurs et relevés                   ║");
        System.out.println("║ 5. Gérer les alertes                               ║");
        System.out.println("║ 6. Afficher le résumé de la ferme                  ║");
        System.out.println("║ 0. Quitter                                         ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.print("→ Votre choix: ");
    }

    // ========== GESTION DES ZONES ==========
    private static void menuGestionZones(Scanner scanner, Ferme ferme) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║          GESTION DES ZONES                          ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Ajouter une zone                                ║");
            System.out.println("║ 2. Lister toutes les zones                         ║");
            System.out.println("║ 3. Activer/Désactiver une zone                     ║");
            System.out.println("║ 4. Afficher détails d'une zone                     ║");
            System.out.println("║ 5. Enregistrer production                          ║");
            System.out.println("║ 0. Retour au menu principal                        ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("→ Votre choix: ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1":
                    ajouterZoneInteractif(scanner, ferme);
                    break;
                case "2":
                    listerZones(ferme);
                    break;
                case "3":
                    gererEtatZone(scanner, ferme);
                    break;
                case "4":
                    afficherDetailsZone(scanner, ferme);
                    break;
                case "5":
                    enregistrerProductionInteractif(scanner, ferme);
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
    }

    private static void ajouterZoneInteractif(Scanner scanner, Ferme ferme) {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     AJOUTER UNE ZONE                    ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Type de zone:");
        System.out.println("  1. Culture (cultures légumières, céréales)");
        System.out.println("  2. Animalerie (ruminants, volailles)");
        System.out.println("  3. Aquacole (bassins de poissons)");
        System.out.print("→ Choix: ");
        
        try {
            String type = scanner.nextLine().trim();
            System.out.print("Nom de la zone: ");
            String nom = scanner.nextLine().trim();
            
            if (nom == null || nom.isEmpty()) {
                System.out.println("❌ Le nom de la zone ne peut pas être vide.");
                return;
            }

            ZoneGeographique zone = null;
            if ("1".equals(type)) {
                zone = new Culture(nom);
                System.out.println("✓ Zone Culture créée: " + nom);
            } else if ("2".equals(type)) {
                zone = new Animalerie(nom);
                System.out.println("✓ Zone Animalerie créée: " + nom);
            } else if ("3".equals(type)) {
                zone = new Aquacole(nom);
                System.out.println("✓ Zone Aquacole créée: " + nom);
            } else {
                System.out.println("❌ Type de zone invalide.");
                return;
            }

            if (ferme.ajouterZone(zone)) {
                System.out.println("✓ Zone ajoutée avec succès (ID: " + zone.getId() + ")");
            }
        } catch (FermeException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void listerZones(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        if (zones.isEmpty()) {
            System.out.println("\n⚠ Aucune zone disponible.");
            return;
        }

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              LISTE DES ZONES                        ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        
        int index = 1;
        for (ZoneGeographique zone : zones) {
            String type = zone.getClass().getSimpleName();
            String etat = zone.isActif() ? "🟢 ACTIF" : "🔴 SUSPENDU";
            System.out.println(String.format("%d. %s (%s) - %s - %d entité(s)",
                index, zone.getNom(), type, etat, zone.getNombreEntites()));
            index++;
        }
    }

    private static void gererEtatZone(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null) return;

        System.out.println("\nÉtat actuel: " + (zone.isActif() ? "🟢 ACTIF" : "🔴 SUSPENDU"));
        System.out.println("1. Activer");
        System.out.println("2. Désactiver");
        System.out.print("→ Choix: ");
        
        String choix = scanner.nextLine().trim();
        if ("1".equals(choix)) {
            zone.activer();
            System.out.println("✓ Zone activée");
        } else if ("2".equals(choix)) {
            zone.desactiver();
            System.out.println("✓ Zone suspendue");
        }
    }

    private static void afficherDetailsZone(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null) return;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              DÉTAILS DE LA ZONE                     ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println("Nom: " + zone.getNom());
        System.out.println("ID: " + zone.getId());
        System.out.println("Type: " + zone.getClass().getSimpleName());
        System.out.println("État: " + (zone.isActif() ? "🟢 ACTIF" : "🔴 SUSPENDU"));
        System.out.println("Nombre d'entités: " + zone.getNombreEntites());
        System.out.println("Production enregistrée: " + zone.getProduction().size() + " fois");

        if (zone instanceof Culture) {
            Culture culture = (Culture) zone;
            System.out.println("\n--- Cultures ---");
            if (culture.getTerre().isEmpty()) {
                System.out.println("Aucune culture.");
            } else {
                int idx = 1;
                for (Plantation p : culture.getTerre()) {
                    System.out.println(idx + ". Plantation - Semis: " + p.getDate_plant() + 
                        " | Récolte: " + p.getDate_rec() + " | Stade: " + p.getEpan());
                    idx++;
                }
            }
        } else if (zone instanceof Animalerie) {
            Animalerie animalerie = (Animalerie) zone;
            System.out.println("\n--- Animaux ---");
            if (animalerie.getKouri().isEmpty()) {
                System.out.println("Aucun animal.");
            } else {
                int idx = 1;
                for (Animal a : animalerie.getKouri()) {
                    System.out.println(idx + ". " + a.getGen() + " - Âge: " + a.getAge() + 
                        "ans | Poids: " + a.getPoids() + "kg | État: " + a.getSante());
                    idx++;
                }
            }
        } else if (zone instanceof Aquacole) {
            Aquacole aquacole = (Aquacole) zone;
            System.out.println("\n--- Poissons ---");
            if (aquacole.getAquarium().isEmpty()) {
                System.out.println("Aucun poisson.");
            } else {
                int idx = 1;
                for (Poisson p : aquacole.getAquarium()) {
                    System.out.println(idx + ". " + p.getEspece() + " - Alimentation: " + 
                        p.getPg().getTypealiment());
                    idx++;
                }
            }
        }

        System.out.println("\n--- Capteurs ---");
        if (zone.getMaintenance().isEmpty()) {
            System.out.println("Aucun capteur.");
        } else {
            int idx = 1;
            for (Capteur c : zone.getMaintenance()) {
                System.out.println(idx + ". " + c.getClass().getSimpleName() + 
                    " - " + (c.isActif() ? "🟢" : "🔴") + " | Seuils: " + c.getSeuilMin() + "-" + c.getSeuilMax());
                idx++;
            }
        }
    }

    private static void enregistrerProductionInteractif(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null) return;

        System.out.print("Valeur de production: ");
        double valeur = lireDouble(scanner.nextLine());
        ferme.enregistrerProduction(zone, valeur);
        System.out.println("✓ Production enregistrée: " + valeur + " unités");
    }

    // ========== GESTION DES CULTURES ==========
    private static void menuGestionCultures(Scanner scanner, Ferme ferme) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║          GESTION DES CULTURES                       ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Ajouter une culture                             ║");
            System.out.println("║ 2. Mettre à jour le stade de croissance             ║");
            System.out.println("║ 3. Afficher le stade de croissance                  ║");
            System.out.println("║ 4. Générer un rapport des cultures                  ║");
            System.out.println("║ 0. Retour                                          ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("→ Votre choix: ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1":
                    ajouterCultureInteractif(scanner, ferme);
                    break;
                case "2":
                    mettreAJourStadeCultureInteractif(scanner, ferme);
                    break;
                case "3":
                    afficherStadesCultures(ferme);
                    break;
                case "4":
                    genererRapportCultures(ferme);
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
    }

    private static void ajouterCultureInteractif(Scanner scanner, Ferme ferme) {
        Culture culture = (Culture) choisirZoneParType(scanner, ferme, Culture.class);
        if (culture == null) return;

        try {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     AJOUTER UNE CULTURE                 ║");
            System.out.println("╚════════════════════════════════════════╝");
            
            System.out.print("Date de plantation (yyyy-MM-dd): ");
            LocalDate plant = lireDate(scanner.nextLine());
            System.out.print("Date de récolte (yyyy-MM-dd): ");
            LocalDate recolte = lireDate(scanner.nextLine());
            System.out.print("Stade (Semis/Croissance/Maturite/Recolte): ");
            Stadedecroissance stade = lireStade(scanner.nextLine());
            System.out.print("pH min: ");
            double phMin = lireDouble(scanner.nextLine());
            System.out.print("pH max: ");
            double phMax = lireDouble(scanner.nextLine());
            System.out.print("Humidité (%): ");
            double humidite = lireDouble(scanner.nextLine());

            Plantation plantation = new Plantation(plant, recolte, stade, phMax, phMin, humidite);
            if (ferme.ajouterCulture(culture, plantation)) {
                System.out.println("✓ Culture ajoutée avec succès");
            }
        } catch (FermeException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void listerCultures(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        boolean trouve = false;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              LISTE DES CULTURES                     ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        int idx = 1;
        for (ZoneGeographique zone : zones) {
            if (zone instanceof Culture) {
                Culture culture = (Culture) zone;
                System.out.println("\nZone: " + culture.getNom());
                if (culture.getTerre().isEmpty()) {
                    System.out.println("  Aucune culture");
                } else {
                    for (Plantation p : culture.getTerre()) {
                        System.out.println("  " + idx + ". Plantation - Semis: " + p.getDate_plant() + 
                            " | Récolte: " + p.getDate_rec() + " | pH: " + p.getPhMin() + "-" + 
                            p.getPhMax() + " | Stade: " + p.getEpan());
                        idx++;
                    }
                }
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("⚠ Aucune zone de culture disponible.");
        }
    }

    private static void mettreAJourStadeCultureInteractif(Scanner scanner, Ferme ferme) {
        Culture culture = (Culture) choisirZoneParType(scanner, ferme, Culture.class);
        if (culture == null) {
            return;
        }

        if (culture.getTerre().isEmpty()) {
            System.out.println("⚠ Aucune plantation dans cette zone.");
            return;
        }

        System.out.println("\nPlantations disponibles dans la zone " + culture.getNom() + ":");
        List<Plantation> plantations = new ArrayList<>(culture.getTerre());
        for (int i = 0; i < plantations.size(); i++) {
            Plantation plantation = plantations.get(i);
            System.out.println((i + 1) + ". Semis: " + plantation.getDate_plant() +
                " | Récolte: " + plantation.getDate_rec() + " | Stade actuel: " + plantation.getEpan());
        }

        System.out.print("→ Sélectionner une plantation: ");
        int index = lireInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= plantations.size()) {
            System.out.println("❌ Sélection invalide.");
            return;
        }

        System.out.print("Nouveau stade (Semis/Croissance/Maturite/Recolte): ");
        Stadedecroissance nouveauStade = lireStade(scanner.nextLine());
        plantations.get(index).setEpan(nouveauStade);
        System.out.println("✓ Stade de croissance mis à jour: " + nouveauStade);
    }

    private static void afficherStadesCultures(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        boolean trouve = false;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║        STADES DE CROISSANCE DES CULTURES           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        for (ZoneGeographique zone : zones) {
            if (zone instanceof Culture) {
                Culture culture = (Culture) zone;
                System.out.println("\nZone: " + culture.getNom());
                if (culture.getTerre().isEmpty()) {
                    System.out.println("  Aucune plantation");
                } else {
                    int idx = 1;
                    for (Plantation plantation : culture.getTerre()) {
                        System.out.println("  " + idx + ". Semis: " + plantation.getDate_plant() +
                            " | Récolte: " + plantation.getDate_rec() +
                            " | Stade: " + plantation.getEpan());
                        idx++;
                    }
                }
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("⚠ Aucune zone de culture disponible.");
        }
    }

    private static void genererRapportCultures(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        boolean trouve = false;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║       RAPPORT DE L'ÉTAT DES CULTURES               ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        for (ZoneGeographique zone : zones) {
            if (zone instanceof Culture) {
                Culture culture = (Culture) zone;
                System.out.println("\nZone: " + culture.getNom() +
                    " | Statut: " + (culture.isActif() ? "ACTIF" : "SUSPENDU") +
                    " | Plantations: " + culture.getTerre().size());

                if (culture.getTerre().isEmpty()) {
                    System.out.println("  Aucune plantation enregistrée.");
                } else {
                    int idx = 1;
                    for (Plantation plantation : culture.getTerre()) {
                        System.out.println("  " + idx + ". Semis: " + plantation.getDate_plant() +
                            " | Récolte: " + plantation.getDate_rec() +
                            " | Stade: " + plantation.getEpan() +
                            " | pH: " + plantation.getPhMin() + "-" + plantation.getPhMax() +
                            " | Humidité: " + plantation.getHumidite() + "%");
                        idx++;
                    }
                }
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("⚠ Aucune zone de culture disponible.");
        }
    }

    // ========== GESTION DES ANIMAUX ==========
    private static void menuGestionAnimaux(Scanner scanner, Ferme ferme) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║          GESTION DES ANIMAUX                        ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Ajouter un animal                               ║");
            System.out.println("║ 2. Ajouter un poisson                              ║");
            System.out.println("║ 3. Lister les animaux                              ║");
            System.out.println("║ 4. Afficher le programme d'alimentation            ║");
            System.out.println("║ 5. Enregistrer événement sanitaire                 ║");
            System.out.println("║ 0. Retour                                          ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("→ Votre choix: ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1":
                    ajouterAnimalInteractif(scanner, ferme);
                    break;
                case "2":
                    ajouterPoissonInteractif(scanner, ferme);
                    break;
                case "3":
                    listerAnimaux(ferme);
                    break;
                case "4":
                    afficherProgrammesAlimentaires(ferme);
                    break;
                case "5":
                    enregistrerEvenementSanitaireInteractif(scanner, ferme);
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
    }

    private static void ajouterAnimalInteractif(Scanner scanner, Ferme ferme) {
        Animalerie animalerie = (Animalerie) choisirZoneParType(scanner, ferme, Animalerie.class);
        if (animalerie == null) return;

        try {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     AJOUTER UN ANIMAL                   ║");
            System.out.println("╚════════════════════════════════════════╝");

            System.out.println("Espèce: 1=Ruminant  2=Volaille");
            System.out.print("→ Choix: ");
            espece type = "1".equals(scanner.nextLine().trim()) ? espece.Ruminant : espece.Volaille;

            System.out.print("Âge (années): ");
            double age = lireDouble(scanner.nextLine());
            if (age < 0) {
                throw new FermeException("L'âge ne peut pas être négatif");
            }
            
            System.out.print("Poids (kg): ");
            double poids = lireDouble(scanner.nextLine());
            if (poids <= 0) {
                throw new FermeException("Le poids doit être positif");
            }
            
            System.out.print("Quantité d'alimentation quotidienne (kg): ");
            double quantite = lireDouble(scanner.nextLine());
            if (quantite < 0) {
                throw new FermeException("La quantité d'alimentation ne peut pas être négative");
            }
            
            System.out.print("Type d'alimentation: ");
            String typeAliment = scanner.nextLine().trim();
            if (typeAliment == null || typeAliment.isEmpty()) {
                throw new FermeException("Le type d'alimentation ne peut pas être vide");
            }
            
            System.out.print("Etat de sante: ");
            etatdesante etatSante = etatdesante.valueOf(scanner.nextLine().trim());

            Animal animal = new Animal(
                new ProgrammeAlimentaire(quantite, typeAliment),
                type, age, poids, etatSante, null
            );

            if (ferme.ajouterAnimal(animalerie, animal)) {
                System.out.println("✓ Animal ajouté: " + type + " (ID: " + animal.getId() + ")");
            }
        } catch (FermeException e) {
            System.out.println("❌ " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("❌ État de santé invalide: " + e.getMessage());
        }
    }

    private static void ajouterPoissonInteractif(Scanner scanner, Ferme ferme) {
        Aquacole aquacole = (Aquacole) choisirZoneParType(scanner, ferme, Aquacole.class);
        if (aquacole == null) return;

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     AJOUTER UN POISSON                  ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.print("Espèce: ");
        String espece = scanner.nextLine().trim();
        System.out.print("Quantité d'alimentation (kg/jour): ");
        double quantite = lireDouble(scanner.nextLine());

        Poisson poisson = new Poisson(espece, new ProgrammeAlimentaire(quantite, "Granule"));
        if (ferme.ajouterPoisson(aquacole, poisson)) {
            System.out.println("✓ Poisson ajouté: " + espece);
        }
    }

    private static void listerAnimaux(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        boolean trouve = false;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║              LISTE DES ANIMAUX                      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        for (ZoneGeographique zone : zones) {
            if (zone instanceof Animalerie) {
                Animalerie animalerie = (Animalerie) zone;
                System.out.println("\nZone: " + animalerie.getNom());
                if (animalerie.getKouri().isEmpty()) {
                    System.out.println("  Aucun animal");
                } else {
                    int idx = 1;
                    for (Animal a : animalerie.getKouri()) {
                        System.out.println("  " + idx + ". " + a.getGen() + " - Âge: " + a.getAge() + 
                            "ans | Poids: " + a.getPoids() + "kg | État: " + a.getSante() +
                            " | Alimentation: " + formatProgrammeAlimentaire(a.getPg()));
                        idx++;
                    }
                }
                trouve = true;
            } else if (zone instanceof Aquacole) {
                Aquacole aquacole = (Aquacole) zone;
                System.out.println("\nZone aquacole: " + aquacole.getNom());
                if (aquacole.getAquarium().isEmpty()) {
                    System.out.println("  Aucun poisson");
                } else {
                    int idx = 1;
                    for (Poisson p : aquacole.getAquarium()) {
                        System.out.println("  " + idx + ". " + p.getEspece() + 
                            " | Alimentation: " + p.getPg().getQuantity() + "kg de " + 
                            p.getPg().getTypealiment());
                        idx++;
                    }
                }
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("⚠ Aucune zone d'élevage disponible.");
        }
    }

    private static void afficherProgrammesAlimentaires(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        boolean trouve = false;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║       PROGRAMMES D'ALIMENTATION DES ANIMAUX        ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        for (ZoneGeographique zone : zones) {
            if (zone instanceof Animalerie) {
                Animalerie animalerie = (Animalerie) zone;
                System.out.println("\nZone: " + animalerie.getNom());
                if (animalerie.getKouri().isEmpty()) {
                    System.out.println("  Aucun animal");
                } else {
                    int idx = 1;
                    for (Animal animal : animalerie.getKouri()) {
                        System.out.println("  " + idx + ". " + animal.getGen() +
                            " | Programme: " + formatProgrammeAlimentaire(animal.getPg()));
                        idx++;
                    }
                }
                trouve = true;
            }
        }

        if (!trouve) {
            System.out.println("⚠ Aucune zone d'élevage disponible.");
        }
    }

    private static String formatProgrammeAlimentaire(ProgrammeAlimentaire programme) {
        if (programme == null) {
            return "non renseigné";
        }
        return programme.getQuantity() + " kg de " + programme.getTypealiment();
    }

    private static void enregistrerEvenementSanitaireInteractif(Scanner scanner, Ferme ferme) {
        Animalerie animalerie = (Animalerie) choisirZoneParType(scanner, ferme, Animalerie.class);
        if (animalerie == null || animalerie.getKouri().isEmpty()) {
            System.out.println("⚠ Aucun animal disponible.");
            return;
        }

        System.out.println("\nAnimaux disponibles:");
        List<Animal> animaux = new ArrayList<>(animalerie.getKouri());
        for (int i = 0; i < animaux.size(); i++) {
            Animal a = animaux.get(i);
            System.out.println((i + 1) + ". " + a.getGen() + " (ID: " + a.getId() + ")");
        }

        System.out.print("→ Sélectionner un animal: ");
        int idx = lireInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= animaux.size()) {
            System.out.println("❌ Sélection invalide.");
            return;
        }

        Animal animal = animaux.get(idx);
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Variation de poids (kg): ");
        double variation = lireDouble(scanner.nextLine());

        EvenementSanitaire evt = new EvenementSanitaire(animal, LocalDate.now(), description, variation);
        ferme.enregistrerEvenementSanitaire(evt);
        System.out.println("✓ Événement sanitaire enregistré");
    }

    // ========== GESTION DES CAPTEURS ==========
    private static void menuGestionCapteurs(Scanner scanner, Ferme ferme) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║        GESTION DES CAPTEURS                         ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Ajouter un capteur                              ║");
            System.out.println("║ 2. Lister les capteurs d'une zone                  ║");
            System.out.println("║ 3. Enregistrer un relevé                           ║");
            System.out.println("║ 4. Afficher l'historique des relevés               ║");
            System.out.println("║ 0. Retour                                          ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("→ Votre choix: ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1":
                    ajouterCapteurInteractif(scanner, ferme);
                    break;
                case "2":
                    listerCapteursZone(scanner, ferme);
                    break;
                case "3":
                    enregistrerReleveInteractif(scanner, ferme);
                    break;
                case "4":
                    afficherHistoriqueReleves(ferme);
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
    }

    private static void ajouterCapteurInteractif(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null) return;

        try {
            System.out.println("\n╔════════════════════════════════════════╗");
            System.out.println("║     AJOUTER UN CAPTEUR                  ║");
            System.out.println("╚════════════════════════════════════════╝");
            System.out.println("Type:");
            System.out.println("  1. Environnemental (température)");
            System.out.println("  2. Sol (pH, humidité)");
            System.out.println("  3. Eau (qualité)");
            System.out.println("  4. GPS (position)");
            System.out.println("  5. Biométrique (animaux)");
            System.out.print("→ Choix: ");

            String type = scanner.nextLine().trim();
            Capteur capteur = null;

            if ("1".equals(type)) capteur = new Environemental();
            else if ("2".equals(type)) capteur = new Sol();
            else if ("3".equals(type)) capteur = new Eau();
            else if ("4".equals(type)) capteur = new GPS();
            else if ("5".equals(type)) capteur = new Biometrique();
            else {
                System.out.println("❌ Type invalide.");
                return;
            }

            System.out.print("Seuil minimum: ");
            double min = lireDouble(scanner.nextLine());
            System.out.print("Seuil maximum: ");
            double max = lireDouble(scanner.nextLine());
            
            if (min >= max) {
                throw new FermeException("Le seuil minimum doit être inférieur au seuil maximum");
            }
            
            capteur.configurerSeuils(min, max);

            if (ferme.ajouterCapteur(zone, capteur)) {
                System.out.println("✓ Capteur " + capteur.getClass().getSimpleName() + " ajouté");
                System.out.println("  Seuils: " + min + " - " + max);
            }
        } catch (FermeException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void listerCapteursZone(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null) return;

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║         CAPTEURS DE " + zone.getNom().toUpperCase() + "                          ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        if (zone.getMaintenance().isEmpty()) {
            System.out.println("⚠ Aucun capteur.");
            return;
        }

        int idx = 1;
        for (Capteur c : zone.getMaintenance()) {
            System.out.println(idx + ". " + c.getClass().getSimpleName() + 
                " - " + (c.isActif() ? "🟢 ACTIF" : "🔴 SUSPENDU") + 
                " | Seuils: " + c.getSeuilMin() + " à " + c.getSeuilMax());
            idx++;
        }
    }

    private static void enregistrerReleveInteractif(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null || zone.getMaintenance().isEmpty()) {
            System.out.println("⚠ Aucun capteur disponible.");
            return;
        }

        try {
            Capteur[] capteurs = zone.getMaintenance().toArray(new Capteur[0]);
            System.out.println("\nCapteurs disponibles:");
            for (int i = 0; i < capteurs.length; i++) {
                System.out.println((i + 1) + ". " + capteurs[i].getClass().getSimpleName());
            }

            System.out.print("→ Sélectionner un capteur: ");
            int idx = lireInt(scanner.nextLine()) - 1;
            if (idx < 0 || idx >= capteurs.length) {
                System.out.println("❌ Sélection invalide.");
                return;
            }

            Capteur capteur = capteurs[idx];
            if (!capteur.isActif()) {
                System.out.println("⚠ Ce capteur est suspendu.");
                return;
            }

            if (capteur instanceof GPS) {
                System.out.print("Latitude: ");
                double lat = lireDouble(scanner.nextLine());
                System.out.print("Longitude: ");
                double lon = lireDouble(scanner.nextLine());
                ReleveGPS releveGps = new ReleveGPS(capteur, lat, lon, LocalDateTime.now());
                boolean alerte = ferme.enregistrerReleveGps(capteur, releveGps);
                System.out.println("✓ Relevé GPS enregistré");
                if (alerte) System.out.println("⚠ Alerte GPS déclenchée!");
            } else {
                System.out.print("Valeur: ");
                double valeur = lireDouble(scanner.nextLine());
                System.out.print("Unité: ");
                String unite = scanner.nextLine().trim();
                if (unite == null || unite.isEmpty()) {
                    throw new FermeException("L'unité du relevé ne peut pas être vide");
                }
                ReleveNum releve = new ReleveNum(capteur, valeur, unite, LocalDateTime.now());
                ferme.enregistrerReleve(capteur, releve);
                System.out.println("✓ Relevé enregistré: " + valeur + " " + unite);
            }
        } catch (FermeException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private static void afficherHistoriqueReleves(Ferme ferme) {
        List<Releve> releves = ferme.getHistoriqueReleves();

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║        HISTORIQUE DES RELEVÉS                       ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        if (releves.isEmpty()) {
            System.out.println("⚠ Aucun relevé.");
            return;
        }

        for (int i = 0; i < releves.size(); i++) {
            Releve releve = releves.get(i);
            String type = releve.getClass().getSimpleName();
            String capteur = releve.getCapteur() == null ? "?" : releve.getCapteur().getClass().getSimpleName();

            if (releve instanceof ReleveNum) {
                ReleveNum num = (ReleveNum) releve;
                System.out.println((i + 1) + ". " + type + " | Capteur: " + capteur + 
                    " | Valeur: " + num.getValeur() + " " + num.getUnite() + 
                    " | Date: " + releve.getHorodatage());
            } else if (releve instanceof ReleveGPS) {
                ReleveGPS gps = (ReleveGPS) releve;
                System.out.println((i + 1) + ". " + type + " | GPS: (" + gps.getLatitude() + 
                    ", " + gps.getLongitude() + ") | Date: " + releve.getHorodatage());
            }
        }
    }

    // ========== GESTION DES ALERTES ==========
    private static void menuGestionAlertes(Scanner scanner, Ferme ferme) {
        boolean continuer = true;
        while (continuer) {
            System.out.println("\n╔════════════════════════════════════════════════════╗");
            System.out.println("║          GESTION DES ALERTES                        ║");
            System.out.println("╠════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Afficher alertes actives                        ║");
            System.out.println("║ 2. Acquitter une alerte                            ║");
            System.out.println("║ 3. Supprimer une alerte                            ║");
            System.out.println("║ 4. Afficher historique complet                     ║");
            System.out.println("║ 0. Retour                                          ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.print("→ Votre choix: ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1":
                    afficherAlertesActives(ferme);
                    break;
                case "2":
                    acquitterAlerteInteractif(scanner, ferme);
                    break;
                case "3":
                    supprimerAlerteInteractif(scanner, ferme);
                    break;
                case "4":
                    afficherHistoriqueAlertes(ferme);
                    break;
                case "0":
                    continuer = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide.");
            }
        }
    }

    private static void afficherAlertesActives(Ferme ferme) {
        List<Alerte> alertes = ferme.alertesActives();

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║         ALERTES ACTIVES (triées par gravité)        ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        if (alertes.isEmpty()) {
            System.out.println("✓ Aucune alerte active.");
            return;
        }

        for (int i = 0; i < alertes.size(); i++) {
            Alerte alerte = alertes.get(i);
            String emoji = "Critique".equals(alerte.getGrv().toString()) ? "🔴" : "🟡";
            System.out.println((i + 1) + ". " + emoji + " " + alerte.getGrv() + 
                " | " + alerte.getMessage() + " | Date: " + alerte.getDate());
        }
    }

    private static void acquitterAlerteInteractif(Scanner scanner, Ferme ferme) {
        List<Alerte> alertes = ferme.alertesActives();
        if (alertes.isEmpty()) {
            System.out.println("⚠ Aucune alerte active.");
            return;
        }

        afficherAlertesActives(ferme);
        System.out.print("→ Numéro de l'alerte à acquitter: ");
        int idx = lireInt(scanner.nextLine()) - 1;

        if (idx >= 0 && idx < alertes.size()) {
            ferme.acquitterAlerte(alertes.get(idx));
            System.out.println("✓ Alerte acquittée");
        }
    }

    private static void supprimerAlerteInteractif(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZoneInteractif(scanner, ferme);
        if (zone == null || zone.getAlt().isEmpty()) {
            System.out.println("⚠ Aucune alerte dans cette zone.");
            return;
        }

        System.out.println("\nAlertes de cette zone:");
        List<Alerte> alertes = new ArrayList<>(zone.getAlt());
        for (int i = 0; i < alertes.size(); i++) {
            Alerte a = alertes.get(i);
            System.out.println((i + 1) + ". " + a.getGrv() + " - " + a.getMessage());
        }

        System.out.print("→ Numéro à supprimer: ");
        int idx = lireInt(scanner.nextLine()) - 1;

        if (idx >= 0 && idx < alertes.size()) {
            ferme.supprimerAlerte(zone, alertes.get(idx));
            System.out.println("✓ Alerte supprimée");
        }
    }

    private static void afficherHistoriqueAlertes(Ferme ferme) {
        List<Alerte> alertes = ferme.getHistoriqueAlertes();

        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║        HISTORIQUE COMPLET DES ALERTES               ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        if (alertes.isEmpty()) {
            System.out.println("⚠ Aucune alerte.");
            return;
        }

        for (int i = 0; i < alertes.size(); i++) {
            Alerte alerte = alertes.get(i);
            String etat = alerte.isAcquittee() ? "✓ ACQUITTÉE" : "⚠ ACTIVE";
            System.out.println((i + 1) + ". " + alerte.getGrv() + " | " + etat + 
                " | " + alerte.getMessage() + " | Date: " + alerte.getDate());
        }
    }

    // ========== AFFICHER RÉSUMÉ ==========
    private static void afficherResume(Ferme ferme) {
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.println("║           RÉSUMÉ DE LA FERME                        ║");
        System.out.println("╚════════════════════════════════════════════════════╝");

        List<ZoneGeographique> zones = ferme.getZones();
        if (zones.isEmpty()) {
            System.out.println("⚠ Aucune zone.");
            return;
        }

        System.out.println("\n📊 ZONES GÉOGRAPHIQUES: " + zones.size());
        int nbCultures = 0, nbAnimaux = 0, nbPoissons = 0, nbCapteurs = 0;

        for (ZoneGeographique zone : zones) {
            System.out.println("\n  📍 " + zone.getNom() + " (" + zone.getClass().getSimpleName() + ")");
            System.out.println("     État: " + (zone.isActif() ? "🟢 ACTIF" : "🔴 SUSPENDU"));
            System.out.println("     Entités: " + zone.getNombreEntites());
            System.out.println("     Capteurs: " + zone.getMaintenance().size());
            System.out.println("     Alertes: " + zone.getAlt().size());

            if (zone instanceof Culture) {
                Culture c = (Culture) zone;
                nbCultures += c.getTerre().size();
            } else if (zone instanceof Animalerie) {
                Animalerie a = (Animalerie) zone;
                nbAnimaux += a.getKouri().size();
            } else if (zone instanceof Aquacole) {
                Aquacole aq = (Aquacole) zone;
                nbPoissons += aq.getAquarium().size();
            }

            nbCapteurs += zone.getMaintenance().size();
        }

        System.out.println("\n📈 STATISTIQUES:");
        System.out.println("  • Cultures: " + nbCultures);
        System.out.println("  • Animaux: " + nbAnimaux);
        System.out.println("  • Poissons: " + nbPoissons);
        System.out.println("  • Capteurs: " + nbCapteurs);
        System.out.println("  • Relevés: " + ferme.getHistoriqueReleves().size());
        System.out.println("  • Alertes totales: " + ferme.getHistoriqueAlertes().size());
        System.out.println("  • Alertes actives: " + ferme.alertesActives().size());
        System.out.println("  • Événements sanitaires: " + ferme.getEvenementsSanitaires().size());
    }

    // ========== FONCTIONS UTILITAIRES ==========
    private static ZoneGeographique choisirZoneInteractif(Scanner scanner, Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        if (zones.isEmpty()) {
            System.out.println("⚠ Aucune zone disponible.");
            return null;
        }

        System.out.println("\nZones disponibles:");
        for (int i = 0; i < zones.size(); i++) {
            System.out.println((i + 1) + ". " + zones.get(i).getNom() + 
                " (" + zones.get(i).getClass().getSimpleName() + ")");
        }

        System.out.print("→ Sélectionner une zone: ");
        int idx = lireInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= zones.size()) {
            System.out.println("❌ Sélection invalide.");
            return null;
        }

        return zones.get(idx);
    }

    private static ZoneGeographique choisirZoneParType(Scanner scanner, Ferme ferme, Class<?> type) {
        List<ZoneGeographique> zones = ferme.getZones();
        List<ZoneGeographique> filtered = new ArrayList<>();

        for (ZoneGeographique zone : zones) {
            if (type.isInstance(zone)) {
                filtered.add(zone);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("⚠ Aucune zone de ce type disponible.");
            return null;
        }

        System.out.println("\nZones de ce type:");
        for (int i = 0; i < filtered.size(); i++) {
            System.out.println((i + 1) + ". " + filtered.get(i).getNom());
        }

        System.out.print("→ Sélectionner: ");
        int idx = lireInt(scanner.nextLine()) - 1;
        if (idx < 0 || idx >= filtered.size()) {
            System.out.println("❌ Sélection invalide.");
            return null;
        }

        return filtered.get(idx);
    }

    private static double lireDouble(String valeur) {
        try {
            return Double.parseDouble(valeur.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static int lireInt(String valeur) {
        try {
            return Integer.parseInt(valeur.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static LocalDate lireDate(String valeur) {
        try {
            return LocalDate.parse(valeur.trim());
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    private static Stadedecroissance lireStade(String valeur) {
        try {
            return Stadedecroissance.valueOf(valeur.trim());
        } catch (IllegalArgumentException e) {
            return Stadedecroissance.Semis;
        }
    }
}
