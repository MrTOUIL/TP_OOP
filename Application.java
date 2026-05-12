import capteur.Alerte;
import capteur.Biometrique;
import capteur.Capteur;
import capteur.Eau;
import capteur.Environemental;
import capteur.GPS;
import capteur.ReleveGPS;
import capteur.ReleveNum;
import capteur.Sol;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import modele.Animal;
import modele.EvenementSanitaire;
import modele.Plantation;
import modele.Poisson;
import modele.ProgrammeAlimentaire;
import modele.Stadedecroissance;
import modele.espece;
import modele.etatdesante;
import zone.Animalerie;
import zone.Aquacole;
import zone.Culture;
import zone.Ferme;
import zone.ZoneGeographique;

public class Application {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Ferme ferme = new Ferme();

        while (true) {
            afficherMenu();
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1":
                    ajouterZone(scanner, ferme);
                    break;
                case "2":
                    afficherZonesAvecContenu(ferme);
                    break;
                case "3":
                    changerEtatZone(scanner, ferme, true);
                    break;
                case "4":
                    changerEtatZone(scanner, ferme, false);
                    break;
                case "5":
                    ajouterEntite(scanner, ferme);
                    break;
                case "6":
                    ajouterCapteur(scanner, ferme);
                    break;
                case "7":
                    enregistrerReleve(scanner, ferme);
                    break;
                case "8":
                    afficherAlertes(ferme);
                    break;
                case "9":
                    acquitterAlerte(scanner, ferme);
                    break;
                case "10":
                    supprimerAlerte(scanner, ferme);
                    break;
                case "11":
                    enregistrerProduction(scanner, ferme);
                    break;
                case "12":
                    enregistrerEvenementSanitaire(scanner, ferme);
                    break;
                case "13":
                    afficherHistorique(scanner, ferme);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private static void afficherMenu() {
        System.out.println();
        System.out.println("=== MENU FERME ===");
        System.out.println("1. Ajouter une zone");
        System.out.println("2. Lister les zones");
        System.out.println("3. Activer une zone");
        System.out.println("4. Suspendre une zone");
        System.out.println("5. Ajouter une entite");
        System.out.println("6. Ajouter un capteur");
        System.out.println("7. Enregistrer un releve");
        System.out.println("8. Voir les alertes actives");
        System.out.println("9. Acquitter une alerte");
        System.out.println("10. Supprimer une alerte");
        System.out.println("11. Enregistrer une production");
        System.out.println("12. Enregistrer un evenement sanitaire");
        System.out.println("13. Historique des releves et alertes");
        System.out.println("0. Quitter");
        System.out.print("Votre choix: ");
    }

    private static void afficherHistorique(Scanner scanner, Ferme ferme) {
        System.out.println("1. Historique des releves");
        System.out.println("2. Historique des alertes");
        System.out.println("3. Les deux");
        System.out.print("Choix: ");
        String choix = scanner.nextLine().trim();

        if ("1".equals(choix) || "3".equals(choix)) {
            afficherHistoriqueReleves(ferme);
        }
        if ("2".equals(choix) || "3".equals(choix)) {
            afficherHistoriqueAlertes(ferme);
        }
    }

    private static void afficherHistoriqueReleves(Ferme ferme) {
        List<capteur.Releve> releves = ferme.getHistoriqueReleves();
        System.out.println();
        System.out.println("=== HISTORIQUE DES RELEVES ===");
        if (releves.isEmpty()) {
            System.out.println("Aucun releve enregistre.");
            return;
        }

        int index = 1;
        for (capteur.Releve releve : releves) {
            String type = releve.getClass().getSimpleName();
            String capteur = releve.getCapteur() == null ? "inconnu" : releve.getCapteur().getClass().getSimpleName();
            String detail = "";
            if (releve instanceof ReleveNum) {
                ReleveNum num = (ReleveNum) releve;
                detail = "valeur=" + num.getValeur() + " " + num.getUnite();
            } else if (releve instanceof ReleveGPS) {
                ReleveGPS gps = (ReleveGPS) releve;
                detail = "latitude=" + gps.getLatitude() + ", longitude=" + gps.getLongitude();
            }

            System.out.println(index + ". " + type
                    + " | capteur=" + capteur
                    + " | date=" + releve.getHorodatage()
                    + " | " + detail
                    + " | alerte=" + (releve.getAlt() == null ? "non" : releve.getAlt().getGrv() + " - " + releve.getAlt().getMessage()));
            index++;
        }
    }

    private static void afficherHistoriqueAlertes(Ferme ferme) {
        List<Alerte> alertes = ferme.getHistoriqueAlertes();
        System.out.println();
        System.out.println("=== HISTORIQUE DES ALERTES ===");
        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte enregistree.");
            return;
        }

        int index = 1;
        for (Alerte alerte : alertes) {
            String etat = alerte.isAcquittee() ? "acquittee" : "active";
            String source = alerte.getReleve() == null ? "inconnue" : alerte.getReleve().getClass().getSimpleName();
            System.out.println(index + ". " + alerte.getGrv()
                    + " | date=" + alerte.getDate()
                    + " | etat=" + etat
                    + " | source=" + source
                    + " | message=" + alerte.getMessage());
            index++;
        }
    }

    private static void afficherZonesAvecContenu(Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        if (zones.isEmpty()) {
            System.out.println("Aucune zone disponible.");
            return;
        }

        for (ZoneGeographique zone : zones) {
            System.out.println("--------------------------------");
            System.out.println("Zone: " + zone.getNom());
            System.out.println("ID: " + zone.getId());
            System.out.println("Actif: " + zone.isActif());
            System.out.println("Nombre d'entites: " + zone.getNombreEntites());

            if (zone instanceof Culture) {
                Culture culture = (Culture) zone;
                System.out.println("Type: Culture");
                if (culture.getTerre().isEmpty()) {
                    System.out.println("Entites: aucune culture");
                } else {
                    System.out.println("Entites:");
                    int index = 1;
                    for (Plantation plantation : culture.getTerre()) {
                        System.out.println("  " + index + ". Plantation - date plantation: " + plantation.getDate_plant()
                                + ", date recolte: " + plantation.getDate_rec()
                                + ", stade: " + plantation.getEpan()
                                + ", pH min: " + plantation.getPhMin()
                                + ", pH max: " + plantation.getPhMax()
                                + ", humidite: " + plantation.getHumidite());
                        index++;
                    }
                }
            } else if (zone instanceof Animalerie) {
                Animalerie animalerie = (Animalerie) zone;
                System.out.println("Type: Animalerie");
                if (animalerie.getKouri().isEmpty()) {
                    System.out.println("Entites: aucun animal");
                } else {
                    System.out.println("Entites:");
                    int index = 1;
                    for (Animal animal : animalerie.getKouri()) {
                        System.out.println("  " + index + ". Animal - id: " + animal.getId()
                                + ", espece: " + animal.getGen()
                                + ", age: " + animal.getAge()
                                + ", poids: " + animal.getPoids()
                                + ", sante: " + animal.getSante());
                        index++;
                    }
                }
            } else if (zone instanceof Aquacole) {
                Aquacole aquacole = (Aquacole) zone;
                System.out.println("Type: Aquacole");
                if (aquacole.getAquarium().isEmpty()) {
                    System.out.println("Entites: aucun poisson");
                } else {
                    System.out.println("Entites:");
                    int index = 1;
                    for (Poisson poisson : aquacole.getAquarium()) {
                        System.out.println("  " + index + ". Poisson - espece: " + poisson.getEspece()
                                + ", alimentation: " + poisson.getPg().getTypealiment()
                                + ", quantite: " + poisson.getPg().getQuantity());
                        index++;
                    }
                }
            }
        }
        System.out.println("--------------------------------");
    }

    private static void ajouterZone(Scanner scanner, Ferme ferme) {
        System.out.println("Type: 1-Culture 2-Animalerie 3-Aquacole");
        String type = scanner.nextLine().trim();
        System.out.print("Nom: ");
        String nom = scanner.nextLine().trim();

        ZoneGeographique zone = null;
        if ("1".equals(type)) zone = new Culture(nom);
        if ("2".equals(type)) zone = new Animalerie(nom);
        if ("3".equals(type)) zone = new Aquacole(nom);

        if (zone != null && ferme.ajouterZone(zone)) {
            System.out.println("Zone ajoutee.");
        }
    }

    private static void changerEtatZone(Scanner scanner, Ferme ferme, boolean activer) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (zone == null) {
            return;
        }
        if (activer) {
            zone.activer();
            System.out.println("Zone activee.");
        } else {
            zone.desactiver();
            System.out.println("Zone suspendue.");
        }
    }

    private static void ajouterEntite(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (zone == null) {
            return;
        }

        if (zone instanceof Culture) {
            System.out.print("Date plantation (yyyy-MM-dd): ");
            LocalDate plant = lireDate(scanner.nextLine());
            System.out.print("Date recolte (yyyy-MM-dd): ");
            LocalDate recolte = lireDate(scanner.nextLine());
            System.out.print("Stade: ");
            Stadedecroissance stade = lireStade(scanner.nextLine());
            System.out.print("pH min: ");
            double phMin = lireDouble(scanner.nextLine());
            System.out.print("pH max: ");
            double phMax = lireDouble(scanner.nextLine());
            System.out.print("Humidite: ");
            double humidite = lireDouble(scanner.nextLine());
            ferme.ajouterCulture((Culture) zone, new Plantation(plant, recolte, stade, phMax, phMin, humidite));
            System.out.println("Culture ajoutee.");
        } else if (zone instanceof Animalerie) {
            System.out.print("Type animal (Ruminant/Volaille): ");
            espece typeAnimal = lireEspece(scanner.nextLine());
            System.out.print("Age: ");
            double age = lireDouble(scanner.nextLine());
            System.out.print("Poids: ");
            double poids = lireDouble(scanner.nextLine());
            System.out.print("Sante (Malade/Soin/Quarantine): ");
            etatdesante sante = lireEtat(scanner.nextLine());
            Animal animal = new Animal(new ProgrammeAlimentaire(1.0, "Base"), typeAnimal, age, poids, sante, null);
            ferme.ajouterAnimal((Animalerie) zone, animal);
            System.out.println("Animal ajoute.");
        } else if (zone instanceof Aquacole) {
            System.out.print("Espece poisson: ");
            String especePoisson = scanner.nextLine().trim();
            Poisson poisson = new Poisson(especePoisson, new ProgrammeAlimentaire(1.0, "Granule"));
            ferme.ajouterPoisson((Aquacole) zone, poisson);
            System.out.println("Poisson ajoute.");
        }
    }

    private static void ajouterCapteur(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (zone == null) {
            return;
        }

        System.out.println("Type: 1-Environmental 2-Sol 3-Biometrique 4-GPS 5-Eau");
        String type = scanner.nextLine().trim();
        Capteur capteur = null;
        if ("1".equals(type)) capteur = new Environemental();
        if ("2".equals(type)) capteur = new Sol();
        if ("3".equals(type)) capteur = new Biometrique();
        if ("4".equals(type)) capteur = new GPS();
        if ("5".equals(type)) capteur = new Eau();

        if (capteur == null) {
            System.out.println("Type invalide.");
            return;
        }

        System.out.print("Seuil min: ");
        double min = lireDouble(scanner.nextLine());
        System.out.print("Seuil max: ");
        double max = lireDouble(scanner.nextLine());
        capteur.configurerSeuils(min, max);
        ferme.ajouterCapteur(zone, capteur);
        System.out.println("Capteur ajoute.");
    }

    private static void enregistrerReleve(Scanner scanner, Ferme ferme) {
        Capteur capteur = choisirCapteur(scanner, ferme);
        if (capteur == null) {
            return;
        }

        if (capteur instanceof GPS) {
            System.out.print("Latitude: ");
            double lat = lireDouble(scanner.nextLine());
            System.out.print("Longitude: ");
            double lon = lireDouble(scanner.nextLine());
            boolean alerteDeclenchee = ferme.enregistrerReleveGps(capteur, new ReleveGPS(capteur, lat, lon, LocalDateTime.now()));
            if (alerteDeclenchee) {
                System.out.println("Alerte GPS declenchee.");
            }
        } else {
            System.out.print("Valeur: ");
            double valeur = lireDouble(scanner.nextLine());
            System.out.print("Unite: ");
            String unite = scanner.nextLine().trim();
            ferme.enregistrerReleve(capteur, new ReleveNum(capteur, valeur, unite, LocalDateTime.now()));
        }

        System.out.println("Releve enregistre.");
    }

    private static void afficherAlertes(Ferme ferme) {
        List<Alerte> alertes = ferme.alertesActives();
        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte active.");
            return;
        }
        for (int i = 0; i < alertes.size(); i++) {
            Alerte alerte = alertes.get(i);
            System.out.println((i + 1) + ". " + alerte.getGrv() + " | " + alerte.getDate() + " | acquittee=" + alerte.isAcquittee());
        }
    }

    private static void acquitterAlerte(Scanner scanner, Ferme ferme) {
        List<Alerte> alertes = ferme.alertesActives();
        if (alertes.isEmpty()) {
            System.out.println("Aucune alerte.");
            return;
        }
        afficherAlertes(ferme);
        System.out.print("Numero alerte: ");
        int index = lireInt(scanner.nextLine()) - 1;
        if (index >= 0 && index < alertes.size()) {
            ferme.acquitterAlerte(alertes.get(index));
        }
    }

    private static void supprimerAlerte(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (zone == null || zone.getAlt().isEmpty()) {
            return;
        }
        for (int i = 0; i < zone.getAlt().size(); i++) {
            Alerte alerte = zone.getAlt().get(i);
            System.out.println((i + 1) + ". " + alerte.getGrv() + " | " + alerte.getDate());
        }
        System.out.print("Numero a supprimer: ");
        int index = lireInt(scanner.nextLine()) - 1;
        if (index >= 0 && index < zone.getAlt().size()) {
            ferme.supprimerAlerte(zone, zone.getAlt().get(index));
        }
    }

    private static void enregistrerProduction(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (zone == null) {
            return;
        }
        System.out.print("Valeur production: ");
        ferme.enregistrerProduction(zone, lireDouble(scanner.nextLine()));
    }

    private static void enregistrerEvenementSanitaire(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (!(zone instanceof Animalerie)) {
            return;
        }
        Animalerie animalerie = (Animalerie) zone;
        if (animalerie.getKouri().isEmpty()) {
            return;
        }
        Animal animal = animalerie.getKouri().iterator().next();
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        System.out.print("Variation de poids: ");
        double variation = lireDouble(scanner.nextLine());
        ferme.enregistrerEvenementSanitaire(new EvenementSanitaire(animal, LocalDate.now(), description, variation));
    }

    private static ZoneGeographique choisirZone(Scanner scanner, Ferme ferme) {
        List<ZoneGeographique> zones = ferme.getZones();
        if (zones.isEmpty()) {
            System.out.println("Aucune zone disponible.");
            return null;
        }
        for (int i = 0; i < zones.size(); i++) {
            ZoneGeographique zone = zones.get(i);
            System.out.println((i + 1) + ". " + zone.getNom() + " | actif=" + zone.isActif());
        }
        System.out.print("Numero de la zone: ");
        int index = lireInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= zones.size()) {
            System.out.println("Zone invalide.");
            return null;
        }
        return zones.get(index);
    }

    private static Capteur choisirCapteur(Scanner scanner, Ferme ferme) {
        ZoneGeographique zone = choisirZone(scanner, ferme);
        if (zone == null || zone.getMaintenance().isEmpty()) {
            return null;
        }
        Capteur[] capteurs = zone.getMaintenance().toArray(new Capteur[0]);
        for (int i = 0; i < capteurs.length; i++) {
            System.out.println((i + 1) + ". " + capteurs[i].getClass().getSimpleName() + " | actif=" + capteurs[i].isActif());
        }
        System.out.print("Numero du capteur: ");
        int index = lireInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= capteurs.length) {
            return null;
        }
        return capteurs[index];
    }

    private static double lireDouble(String valeur) {
        try {
            return Double.parseDouble(valeur.trim());
        } catch (NumberFormatException exception) {
            return 0.0;
        }
    }

    private static int lireInt(String valeur) {
        try {
            return Integer.parseInt(valeur.trim());
        } catch (NumberFormatException exception) {
            return -1;
        }
    }

    private static LocalDate lireDate(String valeur) {
        try {
            return LocalDate.parse(valeur.trim());
        } catch (DateTimeParseException exception) {
            return LocalDate.now();
        }
    }

    private static Stadedecroissance lireStade(String valeur) {
        try {
            return Stadedecroissance.valueOf(valeur.trim());
        } catch (IllegalArgumentException exception) {
            return Stadedecroissance.Semis;
        }
    }

    private static espece lireEspece(String valeur) {
        try {
            return espece.valueOf(valeur.trim());
        } catch (IllegalArgumentException exception) {
            return espece.Ruminant;
        }
    }

    private static etatdesante lireEtat(String valeur) {
        try {
            return etatdesante.valueOf(valeur.trim());
        } catch (IllegalArgumentException exception) {
            return etatdesante.Soin;
        }
    }
}