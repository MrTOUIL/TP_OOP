import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.event.ItemEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class FermeGUI extends JFrame {

    private final Ferme ferme;

    private final DefaultComboBoxModel<ZoneRef> allZonesModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<ZoneRef> cultureZonesModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<ZoneRef> animalerieZonesModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<ZoneRef> aquacoleZonesModel = new DefaultComboBoxModel<>();
    private final DefaultComboBoxModel<ZoneRef> capteurZonesModel = new DefaultComboBoxModel<>();

    private final JTextArea zonesArea = new JTextArea();
    private final JTextArea culturesArea = new JTextArea();
    private final JTextArea animauxArea = new JTextArea();
    private final JTextArea capteursArea = new JTextArea();
    private final JTextArea alertesArea = new JTextArea();
    private final JTextArea resumeArea = new JTextArea();
    private final DataChartPanel productionChart = new DataChartPanel("Évolution de la production");
    private final DataChartPanel releveChart = new DataChartPanel("Évolution des relevés");

    private final DefaultListModel<AlertRef> activeAlertsModel = new DefaultListModel<>();
    private final DefaultListModel<AlertRef> historyAlertsModel = new DefaultListModel<>();
    private final JList<AlertRef> activeAlertsList = new JList<>(activeAlertsModel);
    private final JList<AlertRef> historyAlertsList = new JList<>(historyAlertsModel);

    private final JComboBox<ZoneRef> zoneActuelleCombo = new JComboBox<>(allZonesModel);
    private final JComboBox<ZoneRef> cultureZoneCombo = new JComboBox<>(cultureZonesModel);
    private final JComboBox<ZoneRef> animalerieZoneCombo = new JComboBox<>(animalerieZonesModel);
    private final JComboBox<ZoneRef> aquacoleZoneCombo = new JComboBox<>(aquacoleZonesModel);
    private final JComboBox<ZoneRef> capteurZoneCombo = new JComboBox<>(capteurZonesModel);

    private final JComboBox<String> typeZoneCombo = new JComboBox<>(new String[]{"Culture", "Animalerie", "Aquacole"});
    private final JComboBox<String> typeCapteurCombo = new JComboBox<>(new String[]{"Environemental", "Sol", "Eau", "GPS", "Biometrique"});
    private final JComboBox<String> especeCombo = new JComboBox<>(new String[]{"Ruminant", "Volaille"});
    private final JComboBox<String> santeCombo = new JComboBox<>(new String[]{"Malade", "Soin", "Quarantine"});
    private final JComboBox<String> stadeCombo = new JComboBox<>(new String[]{"Semis", "Germination", "Croissance", "Maturite", "Recolte"});
    private final JComboBox<String> stadeMajCombo = new JComboBox<>(new String[]{"Semis", "Germination", "Croissance", "Maturite", "Recolte"});
    private final JComboBox<String> graviteCombo = new JComboBox<>(new String[]{"Avertissement", "Critique"});
    private final JComboBox<String> triModeCombo = new JComboBox<>(new String[]{"Alphabetique", "Date"});

    private final JComboBox<PlantationRef> plantationCombo = new JComboBox<>();
    private final JComboBox<AnimalRef> animalCombo = new JComboBox<>();
    private final JComboBox<CapteurRef> capteurCombo = new JComboBox<>();

    private final JTextField zoneNomField = new JTextField(16);
    private final JTextField productionField = new JTextField(10);

    private final JTextField plantationPlantField = new JTextField(10);
    private final JTextField plantationRecolteField = new JTextField(10);
    private final JTextField plantationTypeField = new JTextField(12);
    private final JTextField plantationPhMinField = new JTextField(8);
    private final JTextField plantationPhMaxField = new JTextField(8);
    private final JTextField plantationHumiditeField = new JTextField(8);

    private final JTextField animalAgeField = new JTextField(8);
    private final JTextField animalPoidsField = new JTextField(8);
    private final JTextField alimentQuantiteField = new JTextField(8);
    private final JTextField alimentTypeField = new JTextField(10);

    private final JTextField poissonEspeceField = new JTextField(10);
    private final JTextField poissonQuantiteField = new JTextField(8);

    private final JTextField evenementVariationField = new JTextField(8);
    private final JTextField evenementDescriptionField = new JTextField(16);

    private final JTextField capteurSeuilMinField = new JTextField(8);
    private final JTextField capteurSeuilMaxField = new JTextField(8);
    private final JTextField releveValeurField = new JTextField(8);
    private final JTextField releveUniteField = new JTextField(8);
    private final JTextField releveLatField = new JTextField(8);
    private final JTextField releveLonField = new JTextField(8);

    private final JTextField alerteZoneFilterField = new JTextField(10);
    private final JTextField alerteTypeFilterField = new JTextField(10);
    private final JTextField alerteNiveauFilterField = new JTextField(10);
    private final JTextField alerteDebutFilterField = new JTextField(10);
    private final JTextField alerteFinFilterField = new JTextField(10);

    public FermeGUI() {
        this(new Ferme());
    }

    public FermeGUI(Ferme ferme) {
        super("Smart Farm - Interface Graphique");
        this.ferme = ferme;
        construireFenetre();
        triModeCombo.setSelectedItem(ferme.getModeTri() == Ferme.TriMode.DATE ? "Date" : "Alphabetique");
        triModeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                String valeur = (String) triModeCombo.getSelectedItem();
                ferme.setModeTri("Date".equals(valeur) ? Ferme.TriMode.DATE : Ferme.TriMode.ALPHABETIQUE);
                refreshAll();
            }
        });
        capteurZoneCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                mettreAJourTypesCapteursPrincipaux();
                refreshCapteurCombo();
                refreshReleveChart();
            }
        });
        zoneActuelleCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshProductionChart();
            }
        });
        capteurCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                refreshReleveChart();
            }
        });
        refreshAll();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FermeGUI().setVisible(true));
    }

    private void construireFenetre() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 860);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1180, 780));

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(new Color(245, 247, 250));

        JLabel titre = new JLabel("Smart Farm - Gestion interactive", SwingConstants.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 24));
        titre.setBorder(new EmptyBorder(6, 6, 6, 6));
        titre.setForeground(new Color(34, 51, 68));
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setOpaque(false);
        header.add(titre, BorderLayout.CENTER);

        JPanel triPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        triPanel.setOpaque(false);
        triPanel.add(new JLabel("Tri"));
        triPanel.add(triModeCombo);
        header.add(triPanel, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Zones", creerPanelZones());
        tabs.addTab("Cultures", creerPanelCultures());
        tabs.addTab("Animaux", creerPanelAnimaux());
        tabs.addTab("Capteurs", creerPanelCapteurs());
        tabs.addTab("Alertes", creerPanelAlertes());
        tabs.addTab("Résumé", creerPanelResume());

        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);

        activeAlertsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyAlertsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeAlertsList.setCellRenderer(new AlertCellRenderer());
        historyAlertsList.setCellRenderer(new AlertCellRenderer());

        zonesArea.setEditable(false);
        culturesArea.setEditable(false);
        animauxArea.setEditable(false);
        capteursArea.setEditable(false);
        alertesArea.setEditable(false);
        resumeArea.setEditable(false);

        for (JTextArea area : new JTextArea[]{zonesArea, culturesArea, animauxArea, capteursArea, alertesArea, resumeArea}) {
            area.setFont(new Font("Monospaced", Font.PLAIN, 13));
            area.setBackground(Color.WHITE);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
        }
    }

    private JPanel creerPanelZones() {
        JPanel panel = panelPrincipal();
        panel.add(sectionTitre("Gestion des zones"), constraints(0, 0, 4, 1));

        panel.add(new JLabel("Type"), constraints(0, 1, 1, 1));
        panel.add(typeZoneCombo, constraints(1, 1, 1, 1));
        panel.add(new JLabel("Nom"), constraints(2, 1, 1, 1));
        panel.add(zoneNomField, constraints(3, 1, 1, 1));

        JButton ajouterZoneButton = new JButton("Ajouter zone");
        ajouterZoneButton.addActionListener(e -> ajouterZone());
        panel.add(ajouterZoneButton, constraints(4, 1, 1, 1));

        panel.add(new JLabel("Zone sélectionnée"), constraints(0, 2, 1, 1));
        panel.add(zoneActuelleCombo, constraints(1, 2, 2, 1));

        JButton activerButton = new JButton("Activer");
        activerButton.addActionListener(e -> basculerZone(true));
        JButton desactiverButton = new JButton("Désactiver");
        desactiverButton.addActionListener(e -> basculerZone(false));
        panel.add(activerButton, constraints(3, 2, 1, 1));
        panel.add(desactiverButton, constraints(4, 2, 1, 1));

        panel.add(new JLabel("Production"), constraints(0, 3, 1, 1));
        panel.add(productionField, constraints(1, 3, 1, 1));
        JButton productionButton = new JButton("Enregistrer");
        productionButton.addActionListener(e -> enregistrerProduction());
        panel.add(productionButton, constraints(2, 3, 1, 1));

        JButton actualiserButton = new JButton("Actualiser");
        actualiserButton.addActionListener(e -> refreshAll());
        panel.add(actualiserButton, constraints(3, 3, 1, 1));

        JButton rapportZones = new JButton("Rapport détaillé");
        rapportZones.addActionListener(e -> ouvrirRapportDetaille("Rapport détaillé - zones", rapportZonesDetaille()));
        panel.add(rapportZones, constraints(4, 3, 1, 1));

        panel.add(scroll(zonesArea), constraints(0, 4, 6, 1, 1.0, 0.8));
        panel.add(productionChart, constraints(0, 5, 6, 1, 1.0, 0.8));
        return panel;
    }

    private JPanel creerPanelCultures() {
        JPanel panel = panelPrincipal();
        panel.add(sectionTitre("Gestion des cultures"), constraints(0, 0, 5, 1));

        panel.add(new JLabel("Zone culture"), constraints(0, 1, 1, 1));
        panel.add(cultureZoneCombo, constraints(1, 1, 2, 1));
        JButton actualiserCultures = new JButton("Actualiser la liste");
        actualiserCultures.addActionListener(e -> refreshCultures());
        panel.add(actualiserCultures, constraints(3, 1, 1, 1));

        panel.add(new JLabel("Type culture / plantation"), constraints(0, 2, 1, 1));
        panel.add(plantationTypeField, constraints(1, 2, 1, 1));
        panel.add(new JLabel("Date plantation"), constraints(2, 2, 1, 1));
        panel.add(plantationPlantField, constraints(3, 2, 1, 1));
        panel.add(new JLabel("Date récolte"), constraints(4, 2, 1, 1));
        panel.add(plantationRecolteField, constraints(5, 2, 1, 1));
        panel.add(new JLabel("Stade"), constraints(6, 2, 1, 1));
        panel.add(stadeCombo, constraints(7, 2, 1, 1));

        JLabel formatDatesLabel = new JLabel("Format des dates: yyyy-MM-dd");
        formatDatesLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        formatDatesLabel.setForeground(new Color(90, 100, 115));
        panel.add(formatDatesLabel, constraints(0, 3, 2, 1));

        panel.add(new JLabel("pH min"), constraints(2, 3, 1, 1));
        panel.add(plantationPhMinField, constraints(3, 3, 1, 1));
        panel.add(new JLabel("pH max"), constraints(4, 3, 1, 1));
        panel.add(plantationPhMaxField, constraints(5, 3, 1, 1));
        panel.add(new JLabel("Humidité"), constraints(6, 3, 1, 1));
        panel.add(plantationHumiditeField, constraints(7, 3, 1, 1));

        JButton ajouterPlantation = new JButton("Ajouter plantation");
        ajouterPlantation.addActionListener(e -> ajouterPlantation());
        panel.add(ajouterPlantation, constraints(0, 4, 2, 1));

        JButton rapportCultures = new JButton("Rapport détaillé");
        rapportCultures.addActionListener(e -> ouvrirRapportDetaille("Rapport détaillé - cultures", rapportCulturesDetaille()));
        panel.add(rapportCultures, constraints(2, 4, 2, 1));

        panel.add(new JLabel("Plantation sélectionnée"), constraints(0, 5, 1, 1));
        panel.add(plantationCombo, constraints(1, 5, 2, 1));
        panel.add(new JLabel("Nouveau stade"), constraints(3, 5, 1, 1));
        panel.add(stadeMajCombo, constraints(4, 5, 1, 1));

        JButton majStadeButton = new JButton("Mettre à jour");
        majStadeButton.addActionListener(e -> mettreAJourStade());
        panel.add(majStadeButton, constraints(5, 5, 1, 1));

        panel.add(scroll(culturesArea), constraints(0, 6, 6, 1, 1.0, 1.0));
        return panel;
    }

    private JPanel creerPanelAnimaux() {
        JPanel panel = panelPrincipal();
        panel.add(sectionTitre("Gestion des animaux"), constraints(0, 0, 6, 1));

        panel.add(new JLabel("Animalerie"), constraints(0, 1, 1, 1));
        panel.add(animalerieZoneCombo, constraints(1, 1, 2, 1));
        JButton rafraichirAnimaux = new JButton("Actualiser");
        rafraichirAnimaux.addActionListener(e -> refreshAnimaux());
        panel.add(rafraichirAnimaux, constraints(3, 1, 1, 1));

        panel.add(new JLabel("Espèce"), constraints(0, 2, 1, 1));
        panel.add(especeCombo, constraints(1, 2, 1, 1));
        panel.add(new JLabel("Âge"), constraints(2, 2, 1, 1));
        panel.add(animalAgeField, constraints(3, 2, 1, 1));
        panel.add(new JLabel("Poids"), constraints(4, 2, 1, 1));
        panel.add(animalPoidsField, constraints(5, 2, 1, 1));

        panel.add(new JLabel("Qté alim."), constraints(0, 3, 1, 1));
        panel.add(alimentQuantiteField, constraints(1, 3, 1, 1));
        panel.add(new JLabel("Type alim."), constraints(2, 3, 1, 1));
        panel.add(alimentTypeField, constraints(3, 3, 1, 1));
        panel.add(new JLabel("Sante"), constraints(4, 3, 1, 1));
        panel.add(santeCombo, constraints(5, 3, 1, 1));

        JButton ajouterAnimalButton = new JButton("Ajouter animal");
        ajouterAnimalButton.addActionListener(e -> ajouterAnimal());
        panel.add(ajouterAnimalButton, constraints(0, 4, 2, 1));

        JButton rapportAnimaux = new JButton("Rapport détaillé");
        rapportAnimaux.addActionListener(e -> ouvrirRapportDetaille("Rapport détaillé - animaux", rapportAnimauxDetaille()));
        panel.add(rapportAnimaux, constraints(2, 4, 2, 1));

        JButton rapportSanitaire = new JButton("Rapport sanitaire");
        rapportSanitaire.addActionListener(e -> ouvrirRapportDetaille("Rapport sanitaire", rapportSanitaireDetaille()));
        panel.add(rapportSanitaire, constraints(4, 4, 2, 1));

        panel.add(new JLabel("Aquacole"), constraints(0, 5, 1, 1));
        panel.add(aquacoleZoneCombo, constraints(1, 5, 2, 1));
        panel.add(new JLabel("Poisson"), constraints(3, 5, 1, 1));
        panel.add(poissonEspeceField, constraints(4, 5, 1, 1));
        panel.add(new JLabel("Qté alim."), constraints(0, 6, 1, 1));
        panel.add(poissonQuantiteField, constraints(1, 6, 1, 1));
        JButton ajouterPoissonButton = new JButton("Ajouter poisson");
        ajouterPoissonButton.addActionListener(e -> ajouterPoisson());
        panel.add(ajouterPoissonButton, constraints(2, 6, 2, 1));

        panel.add(new JLabel("Animal sélectionné"), constraints(0, 7, 1, 1));
        panel.add(animalCombo, constraints(1, 7, 2, 1));
        panel.add(new JLabel("Description"), constraints(3, 7, 1, 1));
        panel.add(evenementDescriptionField, constraints(4, 7, 2, 1));
        panel.add(new JLabel("Variation poids"), constraints(0, 8, 1, 1));
        panel.add(evenementVariationField, constraints(1, 8, 1, 1));
        JButton enregistrerEvt = new JButton("Enregistrer événement sanitaire");
        enregistrerEvt.addActionListener(e -> enregistrerEvenementSanitaire());
        panel.add(enregistrerEvt, constraints(2, 8, 3, 1));

        panel.add(scroll(animauxArea), constraints(0, 9, 6, 1, 1.0, 1.0));
        return panel;
    }

    private JPanel creerPanelCapteurs() {
        JPanel panel = panelPrincipal();
        panel.add(sectionTitre("Gestion des capteurs"), constraints(0, 0, 6, 1));

        JButton ouvrirFenetreCapteur = new JButton("Créer un capteur dans une fenêtre");
        ouvrirFenetreCapteur.addActionListener(e -> ouvrirFenetreCreationCapteur());
        panel.add(ouvrirFenetreCapteur, constraints(0, 1, 2, 1));

        panel.add(new JLabel("Zone"), constraints(2, 1, 1, 1));
        panel.add(capteurZoneCombo, constraints(3, 1, 2, 1));
        panel.add(new JLabel("Type capteur"), constraints(0, 2, 1, 1));
        panel.add(typeCapteurCombo, constraints(1, 2, 2, 1));

        panel.add(new JLabel("Seuil min"), constraints(3, 2, 1, 1));
        panel.add(capteurSeuilMinField, constraints(4, 2, 1, 1));
        panel.add(new JLabel("Seuil max"), constraints(5, 2, 1, 1));
        panel.add(capteurSeuilMaxField, constraints(6, 2, 1, 1));
        JButton ajouterCapteurButton = new JButton("Ajouter capteur");
        ajouterCapteurButton.addActionListener(e -> ajouterCapteur());
        panel.add(ajouterCapteurButton, constraints(0, 3, 2, 1));

        JButton rapportCapteurs = new JButton("Rapport détaillé");
        rapportCapteurs.addActionListener(e -> ouvrirRapportDetaille("Rapport détaillé - capteurs", rapportCapteursDetaille()));
        panel.add(rapportCapteurs, constraints(2, 3, 2, 1));

        panel.add(new JLabel("Capteur"), constraints(2, 3, 1, 1));
        panel.add(capteurCombo, constraints(3, 3, 2, 1));
        panel.add(new JLabel("Valeur"), constraints(0, 4, 1, 1));
        panel.add(releveValeurField, constraints(1, 4, 1, 1));
        panel.add(new JLabel("Unité"), constraints(2, 4, 1, 1));
        panel.add(releveUniteField, constraints(3, 4, 1, 1));
        panel.add(new JLabel("Latitude"), constraints(4, 4, 1, 1));
        panel.add(releveLatField, constraints(5, 4, 1, 1));
        panel.add(new JLabel("Longitude"), constraints(6, 4, 1, 1));
        panel.add(releveLonField, constraints(7, 4, 1, 1));

        JButton enregistrerReleveButton = new JButton("Enregistrer relevé");
        enregistrerReleveButton.addActionListener(e -> enregistrerReleve());
        panel.add(enregistrerReleveButton, constraints(0, 5, 2, 1));

        JButton activerCapteurButton = new JButton("Activer / suspendre");
        activerCapteurButton.addActionListener(e -> basculerCapteur());
        panel.add(activerCapteurButton, constraints(2, 5, 2, 1));

        JButton actualiserCapteursButton = new JButton("Actualiser");
        actualiserCapteursButton.addActionListener(e -> refreshCapteurs());
        panel.add(actualiserCapteursButton, constraints(4, 5, 1, 1));

        panel.add(scroll(capteursArea), constraints(0, 6, 8, 1, 1.0, 0.8));
        panel.add(releveChart, constraints(0, 7, 8, 1, 1.0, 0.8));
        return panel;
    }

    private JPanel creerPanelAlertes() {
        JPanel panel = panelPrincipal();
        panel.add(sectionTitre("Gestion des alertes"), constraints(0, 0, 6, 1));

        panel.add(new JLabel("Filtres zone"), constraints(0, 1, 1, 1));
        panel.add(alerteZoneFilterField, constraints(1, 1, 1, 1));
        panel.add(new JLabel("Type capteur"), constraints(2, 1, 1, 1));
        panel.add(alerteTypeFilterField, constraints(3, 1, 1, 1));
        panel.add(new JLabel("Niveau"), constraints(4, 1, 1, 1));
        panel.add(alerteNiveauFilterField, constraints(5, 1, 1, 1));

        panel.add(new JLabel("Début"), constraints(0, 2, 1, 1));
        panel.add(alerteDebutFilterField, constraints(1, 2, 1, 1));
        panel.add(new JLabel("Fin"), constraints(2, 2, 1, 1));
        panel.add(alerteFinFilterField, constraints(3, 2, 1, 1));

        JButton actualiserAlertes = new JButton("Actualiser les listes");
        actualiserAlertes.addActionListener(e -> refreshAlertes());
        panel.add(actualiserAlertes, constraints(4, 2, 2, 1));

        JButton rapportAlertes = new JButton("Rapport détaillé");
        rapportAlertes.addActionListener(e -> ouvrirRapportDetaille("Rapport détaillé - alertes", rapportAlertesDetaille()));
        panel.add(rapportAlertes, constraints(0, 3, 2, 1));

        panel.add(new JLabel("Alertes actives"), constraints(2, 3, 1, 1));
        panel.add(new JScrollPane(activeAlertsList), constraints(0, 4, 3, 1, 1.0, 1.0));
        panel.add(new JLabel("Historique"), constraints(3, 3, 1, 1));
        panel.add(new JScrollPane(historyAlertsList), constraints(3, 4, 3, 1, 1.0, 1.0));

        JButton acquitterButton = new JButton("Acquitter");
        acquitterButton.addActionListener(e -> acquitterAlerte());
        JButton supprimerButton = new JButton("Supprimer");
        supprimerButton.addActionListener(e -> supprimerAlerte());
        panel.add(acquitterButton, constraints(0, 5, 1, 1));
        panel.add(supprimerButton, constraints(1, 5, 1, 1));

        panel.add(scroll(alertesArea), constraints(0, 6, 6, 1, 1.0, 0.8));
        return panel;
    }

    private JPanel creerPanelResume() {
        JPanel panel = panelPrincipal();
        panel.add(sectionTitre("Résumé général"), constraints(0, 0, 4, 1));
        JButton actualiserResume = new JButton("Actualiser le résumé");
        actualiserResume.addActionListener(e -> refreshResume());
        panel.add(actualiserResume, constraints(3, 0, 1, 1));
        JButton rapportResume = new JButton("Rapport détaillé");
        rapportResume.addActionListener(e -> ouvrirRapportDetaille("Rapport détaillé - résumé", rapportResumeDetaille()));
        panel.add(rapportResume, constraints(4, 0, 1, 1));
        panel.add(scroll(resumeArea), constraints(0, 1, 5, 1, 1.0, 1.0));
        return panel;
    }

    private JPanel panelPrincipal() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setBackground(new Color(245, 247, 250));
        return panel;
    }

    private JLabel sectionTitre(String texte) {
        JLabel label = new JLabel(texte);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(new Color(31, 45, 61));
        return label;
    }

    private GridBagConstraints constraints(int x, int y, int width, int height) {
        return constraints(x, y, width, height, 0, 0);
    }

    private GridBagConstraints constraints(int x, int y, int width, int height, double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        return gbc;
    }

    private JScrollPane scroll(JTextArea area) {
        JScrollPane pane = new JScrollPane(area);
        pane.setPreferredSize(new Dimension(620, 240));
        return pane;
    }

    private void ajouterZone() {
        String nom = zoneNomField.getText().trim();
        if (nom.isEmpty()) {
            erreur("Le nom de la zone ne peut pas être vide.");
            return;
        }

        ZoneGeographique zone;
        String type = (String) typeZoneCombo.getSelectedItem();
        if ("Animalerie".equals(type)) {
            zone = new Animalerie(nom);
        } else if ("Aquacole".equals(type)) {
            zone = new Aquacole(nom);
        } else {
            zone = new Culture(nom);
        }

        try {
            ferme.ajouterZone(zone);
            zoneNomField.setText("");
            refreshAll();
            info("Zone ajoutée: " + zone.getNom());
        } catch (FermeException e) {
            erreur(e.getMessage());
        }
    }

    private void basculerZone(boolean activer) {
        ZoneRef ref = selectedZone(zoneActuelleCombo);
        if (ref == null) {
            erreur("Sélectionnez une zone.");
            return;
        }
        if (activer) {
            ref.zone.activer();
        } else {
            ref.zone.desactiver();
        }
        refreshAll();
    }

    private void enregistrerProduction() {
        ZoneRef ref = selectedZone(zoneActuelleCombo);
        if (ref == null) {
            erreur("Sélectionnez une zone.");
            return;
        }
        double valeur = parseDouble(productionField.getText(), Double.NaN);
        if (Double.isNaN(valeur)) {
            erreur("Valeur de production invalide.");
            return;
        }
        ferme.enregistrerProduction(ref.zone, valeur);
        productionField.setText("");
        refreshAll();
    }

    private void ajouterPlantation() {
        ZoneRef ref = selectedZone(cultureZoneCombo);
        if (ref == null || !(ref.zone instanceof Culture)) {
            erreur("Sélectionnez une zone de culture.");
            return;
        }

        String typeCulture = plantationTypeField.getText().trim();
        LocalDate plant = parseDate(plantationPlantField.getText());
        LocalDate recolte = parseDate(plantationRecolteField.getText());
        double phMin = parseDouble(plantationPhMinField.getText(), Double.NaN);
        double phMax = parseDouble(plantationPhMaxField.getText(), Double.NaN);
        double humidite = parseDouble(plantationHumiditeField.getText(), Double.NaN);
        String stadeTexte = (String) stadeCombo.getSelectedItem();

        if (typeCulture.isEmpty() || plant == null || recolte == null || Double.isNaN(phMin) || Double.isNaN(phMax) || Double.isNaN(humidite)) {
            erreur("Vérifiez les dates et valeurs numériques.");
            return;
        }

        try {
            Stadedecroissance stade = Stadedecroissance.valueOf(stadeTexte);
            Plantation plantation = new Plantation(plant, recolte, stade, phMax, phMin, humidite, typeCulture);
            ferme.ajouterCulture((Culture) ref.zone, plantation);
            clearPlantationFields();
            plantationTypeField.setText("");
            refreshAll();
            info("Plantation ajoutée à " + ref.zone.getNom());
        } catch (IllegalArgumentException e) {
            erreur("Stade invalide.");
        } catch (FermeException e) {
            erreur(e.getMessage());
        }
    }

    private void mettreAJourStade() {
        PlantationRef ref = (PlantationRef) plantationCombo.getSelectedItem();
        String stadeTexte = (String) stadeMajCombo.getSelectedItem();
        if (ref == null) {
            erreur("Sélectionnez une plantation.");
            return;
        }
        try {
            ref.plantation.setEpan(Stadedecroissance.valueOf(stadeTexte));
            refreshAll();
        } catch (IllegalArgumentException e) {
            erreur("Stade invalide.");
        }
    }

    private void ajouterAnimal() {
        ZoneRef ref = selectedZone(animalerieZoneCombo);
        if (ref == null || !(ref.zone instanceof Animalerie)) {
            erreur("Sélectionnez une animalerie.");
            return;
        }

        double age = parseDouble(animalAgeField.getText(), Double.NaN);
        double poids = parseDouble(animalPoidsField.getText(), Double.NaN);
        double quantite = parseDouble(alimentQuantiteField.getText(), Double.NaN);
        String typeAliment = alimentTypeField.getText().trim();
        String especeTexte = (String) especeCombo.getSelectedItem();
        String santeTexte = (String) santeCombo.getSelectedItem();

        if (Double.isNaN(age) || Double.isNaN(poids) || Double.isNaN(quantite) || typeAliment.isEmpty()) {
            erreur("Vérifiez les valeurs de l'animal.");
            return;
        }

        try {
            Animal animal = new Animal(new ProgrammeAlimentaire(quantite, typeAliment), espece.valueOf(especeTexte), age, poids, etatdesante.valueOf(santeTexte), null);
            ferme.ajouterAnimal((Animalerie) ref.zone, animal);
            clearAnimalFields();
            refreshAll();
            info("Animal ajouté à " + ref.zone.getNom());
        } catch (IllegalArgumentException e) {
            erreur("Valeur d'espèce ou d'état invalide.");
        } catch (FermeException e) {
            erreur(e.getMessage());
        }
    }

    private void ajouterPoisson() {
        ZoneRef ref = selectedZone(aquacoleZoneCombo);
        if (ref == null || !(ref.zone instanceof Aquacole)) {
            erreur("Sélectionnez une zone aquacole.");
            return;
        }

        String especeTexte = poissonEspeceField.getText().trim();
        double quantite = parseDouble(poissonQuantiteField.getText(), Double.NaN);
        if (especeTexte.isEmpty() || Double.isNaN(quantite)) {
            erreur("Vérifiez les données du poisson.");
            return;
        }

        try {
            Poisson poisson = new Poisson(especeTexte, new ProgrammeAlimentaire(quantite, "Granule"));
            ferme.ajouterPoisson((Aquacole) ref.zone, poisson);
            poissonEspeceField.setText("");
            poissonQuantiteField.setText("");
            refreshAll();
            info("Poisson ajouté à " + ref.zone.getNom());
        } catch (FermeException e) {
            erreur(e.getMessage());
        }
    }

    private void enregistrerEvenementSanitaire() {
        ZoneRef ref = selectedZone(animalerieZoneCombo);
        AnimalRef animalRef = (AnimalRef) animalCombo.getSelectedItem();
        if (ref == null || !(ref.zone instanceof Animalerie) || animalRef == null) {
            erreur("Sélectionnez une animalerie et un animal.");
            return;
        }

        double variation = parseDouble(evenementVariationField.getText(), Double.NaN);
        String description = evenementDescriptionField.getText().trim();
        if (Double.isNaN(variation) || description.isEmpty()) {
            erreur("Vérifiez la description et la variation.");
            return;
        }

        EvenementSanitaire evt = new EvenementSanitaire(animalRef.animal, LocalDate.now(), description, variation);
        ferme.enregistrerEvenementSanitaire(evt);
        evenementVariationField.setText("");
        evenementDescriptionField.setText("");
        refreshAll();
        info("Événement sanitaire enregistré.");
    }

    private void ajouterCapteur() {
        ZoneRef ref = selectedZone(capteurZoneCombo);
        if (ref == null) {
            erreur("Sélectionnez une zone.");
            return;
        }

        List<String> typesAutorises = typesCapteursAutorises(ref.zone);
        String type = (String) typeCapteurCombo.getSelectedItem();
        if (type == null || !typesAutorises.contains(type)) {
            erreur("Ce type de capteur n'est pas compatible avec la zone sélectionnée.");
            return;
        }

        double min = parseDouble(capteurSeuilMinField.getText(), Double.NaN);
        double max = parseDouble(capteurSeuilMaxField.getText(), Double.NaN);
        if (Double.isNaN(min) || Double.isNaN(max) || min >= max) {
            erreur("Les seuils sont invalides.");
            return;
        }

        Capteur capteur = creerCapteurParType(type);

        capteur.configurerSeuils(min, max);
        try {
            ferme.ajouterCapteur(ref.zone, capteur);
            capteurSeuilMinField.setText("");
            capteurSeuilMaxField.setText("");
            refreshAll();
            info("Capteur ajouté à " + ref.zone.getNom());
        } catch (FermeException e) {
            erreur(e.getMessage());
        }
    }

    private void ouvrirFenetreCreationCapteur() {
        JDialog dialog = new JDialog(this, "Créer un capteur", true);
        dialog.setSize(560, 260);
        dialog.setLocationRelativeTo(this);

        DefaultComboBoxModel<ZoneRef> dialogZoneModel = new DefaultComboBoxModel<>();
        for (ZoneGeographique zone : ferme.getZones()) {
            dialogZoneModel.addElement(new ZoneRef(zone));
        }

        JComboBox<ZoneRef> dialogZoneCombo = new JComboBox<>(dialogZoneModel);
        JComboBox<String> dialogTypeCombo = new JComboBox<>();
        JTextField dialogSeuilMin = new JTextField(10);
        JTextField dialogSeuilMax = new JTextField(10);

        Runnable rafraichirTypes = () -> {
            dialogTypeCombo.removeAllItems();
            ZoneRef selectedZone = (ZoneRef) dialogZoneCombo.getSelectedItem();
            if (selectedZone == null) {
                return;
            }
            for (String type : typesCapteursAutorises(selectedZone.zone)) {
                dialogTypeCombo.addItem(type);
            }
        };

        dialogZoneCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                rafraichirTypes.run();
            }
        });

        if (dialogZoneCombo.getItemCount() > 0) {
            dialogZoneCombo.setSelectedIndex(0);
        }
        rafraichirTypes.run();

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(12, 12, 12, 12));
        form.add(new JLabel("Zone"), constraints(0, 0, 1, 1));
        form.add(dialogZoneCombo, constraints(1, 0, 2, 1));
        form.add(new JLabel("Type"), constraints(0, 1, 1, 1));
        form.add(dialogTypeCombo, constraints(1, 1, 2, 1));
        form.add(new JLabel("Seuil min"), constraints(0, 2, 1, 1));
        form.add(dialogSeuilMin, constraints(1, 2, 1, 1));
        form.add(new JLabel("Seuil max"), constraints(2, 2, 1, 1));
        form.add(dialogSeuilMax, constraints(3, 2, 1, 1));

        JButton creer = new JButton("Créer");
        creer.addActionListener(e -> {
            ZoneRef zoneRef = (ZoneRef) dialogZoneCombo.getSelectedItem();
            String type = (String) dialogTypeCombo.getSelectedItem();
            double min = parseDouble(dialogSeuilMin.getText(), Double.NaN);
            double max = parseDouble(dialogSeuilMax.getText(), Double.NaN);

            if (zoneRef == null || type == null) {
                erreur("Sélectionnez une zone et un type.");
                return;
            }
            if (Double.isNaN(min) || Double.isNaN(max) || min >= max) {
                erreur("Les seuils sont invalides.");
                return;
            }

            try {
                Capteur capteur = creerCapteurParType(type);
                capteur.configurerSeuils(min, max);
                ferme.ajouterCapteur(zoneRef.zone, capteur);
                refreshAll();
                dialog.dispose();
                info("Capteur créé dans la zone " + zoneRef.zone.getNom());
            } catch (FermeException ex) {
                erreur(ex.getMessage());
            }
        });

        JButton annuler = new JButton("Annuler");
        annuler.addActionListener(e -> dialog.dispose());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(annuler);
        actions.add(creer);

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void mettreAJourTypesCapteursPrincipaux() {
        ZoneRef ref = selectedZone(capteurZoneCombo);
        List<String> typesAutorises = ref == null ? Collections.emptyList() : typesCapteursAutorises(ref.zone);
        String selection = (String) typeCapteurCombo.getSelectedItem();

        typeCapteurCombo.removeAllItems();
        for (String type : typesAutorises) {
            typeCapteurCombo.addItem(type);
        }

        if (selection != null && typesAutorises.contains(selection)) {
            typeCapteurCombo.setSelectedItem(selection);
        } else if (typeCapteurCombo.getItemCount() > 0) {
            typeCapteurCombo.setSelectedIndex(0);
        }
    }

    private List<String> typesCapteursAutorises(ZoneGeographique zone) {
        if (zone instanceof Culture) {
            List<String> types = new ArrayList<>();
            types.add("Environemental");
            types.add("Sol");
            return types;
        }
        if (zone instanceof Animalerie) {
            List<String> types = new ArrayList<>();
            types.add("GPS");
            types.add("Biometrique");
            return types;
        }
        if (zone instanceof Aquacole) {
            List<String> types = new ArrayList<>();
            types.add("Eau");
            return types;
        }
        List<String> types = new ArrayList<>();
        types.add("Environemental");
        types.add("Sol");
        types.add("Eau");
        types.add("GPS");
        types.add("Biometrique");
        return types;
    }

    private Capteur creerCapteurParType(String type) {
        if ("Sol".equals(type)) {
            return new Sol();
        }
        if ("Eau".equals(type)) {
            return new Eau();
        }
        if ("GPS".equals(type)) {
            return new GPS();
        }
        if ("Biometrique".equals(type)) {
            return new Biometrique();
        }
        return new Environemental();
    }

    private void enregistrerReleve() {
        ZoneRef ref = selectedZone(capteurZoneCombo);
        CapteurRef capteurRef = (CapteurRef) capteurCombo.getSelectedItem();
        if (ref == null || capteurRef == null) {
            erreur("Sélectionnez une zone et un capteur.");
            return;
        }

        Capteur capteur = capteurRef.capteur;
        try {
            if (capteur instanceof GPS) {
                double lat = parseDouble(releveLatField.getText(), Double.NaN);
                double lon = parseDouble(releveLonField.getText(), Double.NaN);
                if (Double.isNaN(lat) || Double.isNaN(lon)) {
                    erreur("Latitude et longitude requises.");
                    return;
                }
                ReleveGPS releve = new ReleveGPS(capteur, lat, lon, LocalDateTime.now());
                ferme.enregistrerReleveGps(capteur, releve);
            } else {
                double valeur = parseDouble(releveValeurField.getText(), Double.NaN);
                String unite = releveUniteField.getText().trim();
                if (Double.isNaN(valeur) || unite.isEmpty()) {
                    erreur("Valeur et unité requises.");
                    return;
                }
                ReleveNum releve = new ReleveNum(capteur, valeur, unite, LocalDateTime.now());
                ferme.enregistrerReleve(capteur, releve);
            }
            clearReleveFields();
            refreshAll();
            info("Relevé enregistré.");
        } catch (FermeException e) {
            erreur(e.getMessage());
        }
    }

    private void basculerCapteur() {
        CapteurRef ref = (CapteurRef) capteurCombo.getSelectedItem();
        if (ref == null) {
            erreur("Sélectionnez un capteur.");
            return;
        }
        if (ref.capteur.isActif()) {
            ref.capteur.desactiver();
        } else {
            ref.capteur.activer();
        }
        refreshAll();
    }

    private void acquitterAlerte() {
        AlertRef ref = activeAlertsList.getSelectedValue();
        if (ref == null) {
            erreur("Sélectionnez une alerte active.");
            return;
        }
        ferme.acquitterAlerte(ref.alerte);
        refreshAll();
    }

    private void supprimerAlerte() {
        AlertRef ref = historyAlertsList.getSelectedValue();
        if (ref == null) {
            ref = activeAlertsList.getSelectedValue();
        }
        if (ref == null) {
            erreur("Sélectionnez une alerte.");
            return;
        }
        if (ref.zone != null) {
            ferme.supprimerAlerte(ref.zone, ref.alerte);
        }
        refreshAll();
    }

    private void refreshAll() {
        refreshZones();
        refreshCultures();
        refreshAnimaux();
        refreshCapteurs();
        refreshAlertes();
        refreshResume();
    }

    private void refreshZones() {
        fillZoneModels();
        zonesArea.setText(construireTexteZones());
        refreshProductionChart();
    }

    private void refreshCultures() {
        fillZoneModels();
        refreshPlantationCombo();
        culturesArea.setText(construireTexteCultures());
    }

    private void refreshAnimaux() {
        fillZoneModels();
        refreshAnimalCombo();
        animauxArea.setText(construireTexteAnimaux());
    }

    private void refreshCapteurs() {
        fillZoneModels();
        mettreAJourTypesCapteursPrincipaux();
        refreshCapteurCombo();
        capteursArea.setText(construireTexteCapteurs());
        refreshReleveChart();
    }

    private void refreshAlertes() {
        alertesArea.setText(construireTexteAlertes());
        activeAlertsModel.clear();
        historyAlertsModel.clear();
        for (AlertRef ref : collecterAlertesActives()) {
            activeAlertsModel.addElement(ref);
        }
        for (AlertRef ref : collecterHistoriqueAlertes()) {
            historyAlertsModel.addElement(ref);
        }
    }

    private void refreshResume() {
        resumeArea.setText(ferme.resumeZones());
    }

    private void ouvrirRapportDetaille(String titre, String contenu) {
        JTextArea area = new JTextArea(contenu);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setPreferredSize(new Dimension(920, 620));

        JDialog dialog = new JDialog(this, titre, true);
        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton fermer = new JButton("Fermer");
        fermer.addActionListener(e -> dialog.dispose());
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.add(fermer);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void refreshProductionChart() {
        ZoneRef ref = selectedZone(zoneActuelleCombo);
        if (ref == null) {
            productionChart.setSeries(Collections.emptyList());
            return;
        }
        productionChart.setSeries(ref.zone.getProduction());
    }

    private void refreshReleveChart() {
        CapteurRef ref = (CapteurRef) capteurCombo.getSelectedItem();
        if (ref == null) {
            releveChart.setSeries(Collections.emptyList());
            return;
        }

        List<Double> series = new ArrayList<>();
        for (Releve releve : ref.capteur.getRel()) {
            if (releve instanceof ReleveNum) {
                series.add(((ReleveNum) releve).getValeur());
            } else if (releve instanceof ReleveGPS) {
                series.add(((ReleveGPS) releve).getLatitude());
            }
        }
        releveChart.setSeries(series);
    }

    private void fillZoneModels() {
        ZoneRef selectedAll = selectedZone(zoneActuelleCombo);
        ZoneRef selectedCulture = selectedZone(cultureZoneCombo);
        ZoneRef selectedAnimalerie = selectedZone(animalerieZoneCombo);
        ZoneRef selectedAquacole = selectedZone(aquacoleZoneCombo);
        ZoneRef selectedCapteur = selectedZone(capteurZoneCombo);

        allZonesModel.removeAllElements();
        cultureZonesModel.removeAllElements();
        animalerieZonesModel.removeAllElements();
        aquacoleZonesModel.removeAllElements();
        capteurZonesModel.removeAllElements();

        for (ZoneGeographique zone : ferme.getZones()) {
            ZoneRef ref = new ZoneRef(zone);
            allZonesModel.addElement(ref);
            if (zone instanceof Culture) {
                cultureZonesModel.addElement(ref);
            }
            if (zone instanceof Animalerie) {
                animalerieZonesModel.addElement(ref);
            }
            if (zone instanceof Aquacole) {
                aquacoleZonesModel.addElement(ref);
            }
            capteurZonesModel.addElement(ref);
        }

        restoreSelection(zoneActuelleCombo, selectedAll);
        restoreSelection(cultureZoneCombo, selectedCulture);
        restoreSelection(animalerieZoneCombo, selectedAnimalerie);
        restoreSelection(aquacoleZoneCombo, selectedAquacole);
        restoreSelection(capteurZoneCombo, selectedCapteur);
    }

    private void refreshPlantationCombo() {
        plantationCombo.removeAllItems();
        ZoneRef ref = selectedZone(cultureZoneCombo);
        if (ref == null || !(ref.zone instanceof Culture)) {
            return;
        }
        List<Plantation> plantations = trierPlantations(((Culture) ref.zone).getTerre());
        for (Plantation plantation : plantations) {
            plantationCombo.addItem(new PlantationRef(ref.zone, plantation));
        }
    }

    private void refreshAnimalCombo() {
        animalCombo.removeAllItems();
        ZoneRef ref = selectedZone(animalerieZoneCombo);
        if (ref == null || !(ref.zone instanceof Animalerie)) {
            return;
        }
        for (Animal animal : trierAnimaux(((Animalerie) ref.zone).getKouri())) {
            animalCombo.addItem(new AnimalRef(ref.zone, animal));
        }
    }

    private void refreshCapteurCombo() {
        capteurCombo.removeAllItems();
        ZoneRef ref = selectedZone(capteurZoneCombo);
        if (ref == null) {
            return;
        }
        for (Capteur capteur : trierCapteurs(ref.zone.getMaintenance())) {
            capteurCombo.addItem(new CapteurRef(ref.zone, capteur));
        }
    }

    private String construireTexteZones() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            builder.append(zone.getNom())
                   .append(" | ")
                   .append(zone.getClass().getSimpleName())
                   .append(" | ")
                   .append(zone.isActif() ? "ACTIF" : "SUSPENDU")
                   .append(" | entités=")
                   .append(zone.getNombreEntites())
                   .append(" | capteurs=")
                   .append(zone.getMaintenance().size())
                   .append(" | alertes=")
                   .append(zone.getAlt().size())
                   .append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String rapportZonesDetaille() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            builder.append("Zone: ").append(zone.getNom()).append(System.lineSeparator());
            builder.append("  Type: ").append(zone.getClass().getSimpleName()).append(System.lineSeparator());
            builder.append("  ID: ").append(zone.getId()).append(System.lineSeparator());
            builder.append("  Statut: ").append(zone.isActif() ? "ACTIF" : "SUSPENDU").append(System.lineSeparator());
            builder.append("  Nombre d'entités: ").append(zone.getNombreEntites()).append(System.lineSeparator());
            builder.append("  Production: ").append(zone.getProduction().size()).append(" valeur(s)").append(System.lineSeparator());
            builder.append("  Capteurs: ").append(zone.getMaintenance().size()).append(System.lineSeparator());
            builder.append("  Relevés: ").append(zone.getRel().size()).append(System.lineSeparator());
            builder.append("  Alertes: ").append(zone.getAlt().size()).append(System.lineSeparator());
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String rapportCulturesDetaille() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            if (zone instanceof Culture) {
                Culture culture = (Culture) zone;
                builder.append("Zone culture: ").append(culture.getNom()).append(System.lineSeparator());
                builder.append("  Statut: ").append(culture.isActif() ? "ACTIF" : "SUSPENDU").append(System.lineSeparator());
                builder.append("  Plantation(s): ").append(culture.getTerre().size()).append(System.lineSeparator());
                if (culture.getTerre().isEmpty()) {
                    builder.append("  Aucune plantation enregistrée.").append(System.lineSeparator());
                } else {
                    List<Plantation> plantations = trierPlantations(culture.getTerre());
                    for (int i = 0; i < plantations.size(); i++) {
                        Plantation plantation = plantations.get(i);
                        builder.append("  Plantation ").append(i + 1).append(":").append(System.lineSeparator());
                        builder.append("    Type: ").append(plantation.getType()).append(System.lineSeparator());
                        builder.append("    Date plantation: ").append(plantation.getDate_plant()).append(System.lineSeparator());
                        builder.append("    Date récolte: ").append(plantation.getDate_rec()).append(System.lineSeparator());
                        builder.append("    Stade: ").append(plantation.getEpan()).append(System.lineSeparator());
                        builder.append("    pH min: ").append(plantation.getPhMin()).append(System.lineSeparator());
                        builder.append("    pH max: ").append(plantation.getPhMax()).append(System.lineSeparator());
                        builder.append("    Humidité: ").append(plantation.getHumidite()).append(System.lineSeparator());
                    }
                }
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    private String rapportAnimauxDetaille() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            if (zone instanceof Animalerie) {
                Animalerie animalerie = (Animalerie) zone;
                builder.append("Animalerie: ").append(animalerie.getNom()).append(System.lineSeparator());
                builder.append("  Statut: ").append(animalerie.isActif() ? "ACTIF" : "SUSPENDU").append(System.lineSeparator());
                builder.append("  Nombre d'animaux: ").append(animalerie.getKouri().size()).append(System.lineSeparator());
                if (animalerie.getKouri().isEmpty()) {
                    builder.append("  Aucun animal enregistré.").append(System.lineSeparator());
                } else {
                    int index = 1;
                    for (Animal animal : trierAnimaux(animalerie.getKouri())) {
                        builder.append("  Animal ").append(index).append(":").append(System.lineSeparator());
                        builder.append("    ID: ").append(animal.getId()).append(System.lineSeparator());
                        builder.append("    Espèce: ").append(animal.getGen()).append(System.lineSeparator());
                        builder.append("    Âge: ").append(animal.getAge()).append(System.lineSeparator());
                        builder.append("    Poids: ").append(animal.getPoids()).append(System.lineSeparator());
                        builder.append("    Santé: ").append(animal.getSante()).append(System.lineSeparator());
                        builder.append("    Programme alimentaire: ").append(formatProgramme(animal.getPg())).append(System.lineSeparator());
                        index++;
                    }
                }
                builder.append(System.lineSeparator());
            } else if (zone instanceof Aquacole) {
                Aquacole aquacole = (Aquacole) zone;
                builder.append("Aquacole: ").append(aquacole.getNom()).append(System.lineSeparator());
                builder.append("  Statut: ").append(aquacole.isActif() ? "ACTIF" : "SUSPENDU").append(System.lineSeparator());
                builder.append("  Nombre de poissons: ").append(aquacole.getAquarium().size()).append(System.lineSeparator());
                if (aquacole.getAquarium().isEmpty()) {
                    builder.append("  Aucun poisson enregistré.").append(System.lineSeparator());
                } else {
                    int index = 1;
                    for (Poisson poisson : trierPoissons(aquacole.getAquarium())) {
                        builder.append("  Poisson ").append(index).append(":").append(System.lineSeparator());
                        builder.append("    Espèce: ").append(poisson.getEspece()).append(System.lineSeparator());
                        builder.append("    Programme alimentaire: ").append(formatProgramme(poisson.getPg())).append(System.lineSeparator());
                        index++;
                    }
                }
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    private String rapportCapteursDetaille() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            builder.append("Zone: ").append(zone.getNom()).append(System.lineSeparator());
            if (zone.getMaintenance().isEmpty()) {
                builder.append("  Aucun capteur.").append(System.lineSeparator());
            } else {
                int index = 1;
                for (Capteur capteur : trierCapteurs(zone.getMaintenance())) {
                    builder.append("  Capteur ").append(index).append(":").append(System.lineSeparator());
                    builder.append("    Type: ").append(capteur.getClass().getSimpleName()).append(System.lineSeparator());
                    builder.append("    ID: ").append(capteur.getId()).append(System.lineSeparator());
                    builder.append("    Statut: ").append(capteur.getStat()).append(System.lineSeparator());
                    builder.append("    Actif: ").append(capteur.isActif()).append(System.lineSeparator());
                    builder.append("    Seuil min: ").append(capteur.getSeuilMin()).append(System.lineSeparator());
                    builder.append("    Seuil max: ").append(capteur.getSeuilMax()).append(System.lineSeparator());
                    builder.append("    Relevés: ").append(capteur.getRel().size()).append(System.lineSeparator());
                    if (!capteur.getRel().isEmpty()) {
                        int relevIndex = 1;
                        for (Releve releve : capteur.getRel()) {
                            builder.append("      Relevé ").append(relevIndex).append(": ");
                            if (releve instanceof ReleveNum) {
                                ReleveNum num = (ReleveNum) releve;
                                builder.append("valeur=").append(num.getValeur())
                                       .append(" ").append(num.getUnite());
                            } else if (releve instanceof ReleveGPS) {
                                ReleveGPS gps = (ReleveGPS) releve;
                                builder.append("latitude=").append(gps.getLatitude())
                                       .append(", longitude=").append(gps.getLongitude());
                            }
                            builder.append(" | horodatage=").append(releve.getHorodatage()).append(System.lineSeparator());
                            relevIndex++;
                        }
                    }
                    index++;
                }
            }
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String rapportAlertesDetaille() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alertes actives").append(System.lineSeparator());
        if (ferme.alertesActives().isEmpty()) {
            builder.append("  Aucune alerte active.").append(System.lineSeparator());
        } else {
            for (Alerte alerte : ferme.alertesActives()) {
                builder.append(formatAlerteDetaillee(alerte)).append(System.lineSeparator());
            }
        }
        builder.append(System.lineSeparator()).append("Historique complet").append(System.lineSeparator());
        if (ferme.getHistoriqueAlertes().isEmpty()) {
            builder.append("  Aucune alerte.").append(System.lineSeparator());
        } else {
            for (Alerte alerte : ferme.getHistoriqueAlertes()) {
                builder.append(formatAlerteDetaillee(alerte)).append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    private String rapportResumeDetaille() {
        StringBuilder builder = new StringBuilder();
        builder.append("Résumé détaillé de la ferme").append(System.lineSeparator());
        builder.append("  Zones: ").append(ferme.getZones().size()).append(System.lineSeparator());
        builder.append("  Alertes historiques: ").append(ferme.getHistoriqueAlertes().size()).append(System.lineSeparator());
        builder.append("  Alertes actives: ").append(ferme.alertesActives().size()).append(System.lineSeparator());
        builder.append("  Événements sanitaires: ").append(ferme.getEvenementsSanitaires().size()).append(System.lineSeparator());
        builder.append("  Relevés: ").append(ferme.getHistoriqueReleves().size()).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        builder.append(ferme.resumeZones());
        return builder.toString();
    }

    private String formatAlerteDetaillee(Alerte alerte) {
        StringBuilder builder = new StringBuilder();
        builder.append("  Gravité: ").append(alerte.getGrv()).append(System.lineSeparator());
        builder.append("  Date: ").append(alerte.getDate()).append(System.lineSeparator());
        builder.append("  État: ").append(alerte.isAcquittee() ? "ACQUITTÉE" : "ACTIVE").append(System.lineSeparator());
        builder.append("  Message: ").append(alerte.getMessage()).append(System.lineSeparator());
        Releve releve = alerte.getReleve();
        if (releve != null) {
            builder.append("  Relevé lié: ").append(releve.getClass().getSimpleName()).append(System.lineSeparator());
            if (releve instanceof ReleveNum) {
                ReleveNum num = (ReleveNum) releve;
                builder.append("    Valeur: ").append(num.getValeur()).append(" ").append(num.getUnite()).append(System.lineSeparator());
            } else if (releve instanceof ReleveGPS) {
                ReleveGPS gps = (ReleveGPS) releve;
                builder.append("    Latitude: ").append(gps.getLatitude()).append(System.lineSeparator());
                builder.append("    Longitude: ").append(gps.getLongitude()).append(System.lineSeparator());
            }
            builder.append("    Horodatage: ").append(releve.getHorodatage()).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String construireTexteCultures() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            if (zone instanceof Culture) {
                Culture culture = (Culture) zone;
                builder.append(culture.getNom()).append(System.lineSeparator());
                List<Plantation> plantations = trierPlantations(culture.getTerre());
                if (plantations.isEmpty()) {
                    builder.append("  Aucune plantation").append(System.lineSeparator());
                } else {
                    for (int i = 0; i < plantations.size(); i++) {
                        Plantation plantation = plantations.get(i);
                        builder.append("  ").append(i + 1).append(". ")
                               .append(plantation.getType() == null || plantation.getType().isEmpty() ? "Type non renseigné" : plantation.getType())
                               .append(" | ")
                               .append(plantation.getDate_plant())
                               .append(" -> ")
                               .append(plantation.getDate_rec())
                               .append(" | stade=")
                               .append(plantation.getEpan())
                               .append(" | pH=")
                               .append(plantation.getPhMin())
                               .append("-")
                               .append(plantation.getPhMax())
                               .append(" | humidité=")
                               .append(plantation.getHumidite())
                               .append(System.lineSeparator());
                    }
                }
            }
        }
        return builder.toString();
    }

    private String construireTexteAnimaux() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            if (zone instanceof Animalerie) {
                Animalerie animalerie = (Animalerie) zone;
                builder.append("Animalerie: ").append(animalerie.getNom()).append(System.lineSeparator());
                if (animalerie.getKouri().isEmpty()) {
                    builder.append("  Aucun animal").append(System.lineSeparator());
                } else {
                    for (Animal animal : trierAnimaux(animalerie.getKouri())) {
                        builder.append("  ")
                               .append(animal.getGen())
                               .append(" | âge=")
                               .append(animal.getAge())
                               .append(" | poids=")
                               .append(animal.getPoids())
                               .append(" | état=")
                               .append(animal.getSante())
                               .append(" | alimentation=")
                               .append(formatProgramme(animal.getPg()))
                               .append(System.lineSeparator());
                    }
                }
            } else if (zone instanceof Aquacole) {
                Aquacole aquacole = (Aquacole) zone;
                builder.append("Aquacole: ").append(aquacole.getNom()).append(System.lineSeparator());
                if (aquacole.getAquarium().isEmpty()) {
                    builder.append("  Aucun poisson").append(System.lineSeparator());
                } else {
                    for (Poisson poisson : trierPoissons(aquacole.getAquarium())) {
                        builder.append("  ")
                               .append(poisson.getEspece())
                               .append(" | alimentation=")
                               .append(formatProgramme(poisson.getPg()))
                               .append(System.lineSeparator());
                    }
                }
            }
        }
        return builder.toString();
    }

    private String construireTexteCapteurs() {
        StringBuilder builder = new StringBuilder();
        for (ZoneGeographique zone : ferme.getZones()) {
            builder.append(zone.getNom()).append(System.lineSeparator());
            if (zone.getMaintenance().isEmpty()) {
                builder.append("  Aucun capteur").append(System.lineSeparator());
            } else {
                for (Capteur capteur : trierCapteurs(zone.getMaintenance())) {
                    builder.append("  ")
                           .append(capteur.getClass().getSimpleName())
                           .append(" | ")
                           .append(capteur.isActif() ? "ACTIF" : "SUSPENDU")
                           .append(" | seuils=")
                           .append(capteur.getSeuilMin())
                           .append("-")
                           .append(capteur.getSeuilMax())
                           .append(" | relevés=")
                           .append(capteur.getRel().size())
                           .append(System.lineSeparator());
                }
            }
        }
        return builder.toString();
    }

    private String construireTexteAlertes() {
        StringBuilder builder = new StringBuilder();
        builder.append("Alertes actives: ").append(ferme.alertesActives().size()).append(System.lineSeparator());
        for (AlertRef ref : collecterAlertesActives()) {
            builder.append(ref).append(System.lineSeparator());
        }
        builder.append(System.lineSeparator()).append("Historique complet: ").append(ferme.getHistoriqueAlertes().size()).append(System.lineSeparator());
        for (AlertRef ref : collecterHistoriqueAlertes()) {
            builder.append(ref).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String rapportSanitaireDetaille() {
        return ferme.rapportEvenementsSanitairesDetaille();
    }

    private List<Plantation> trierPlantations(Collection<Plantation> plantations) {
        List<Plantation> resultat = new ArrayList<>(plantations);
        if (ferme.getModeTri() == Ferme.TriMode.DATE) {
            resultat.sort(Comparator
                .comparing(Plantation::getDate_plant, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Plantation::getDate_rec, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(plantation -> plantation.getType() == null ? "" : plantation.getType(), String.CASE_INSENSITIVE_ORDER));
        } else {
            resultat.sort(Comparator
                .comparing((Plantation plantation) -> plantation.getType() == null ? "" : plantation.getType(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Plantation::getDate_plant, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Plantation::getDate_rec, Comparator.nullsLast(Comparator.naturalOrder())));
        }
        return resultat;
    }

    private List<Animal> trierAnimaux(Collection<Animal> animaux) {
        List<Animal> resultat = new ArrayList<>(animaux);
        resultat.sort(Comparator
            .comparing((Animal animal) -> animal.getGen() == null ? "" : animal.getGen().name(), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(animal -> animal.getId() == null ? "" : animal.getId().toString()));
        return resultat;
    }

    private List<Poisson> trierPoissons(Collection<Poisson> poissons) {
        List<Poisson> resultat = new ArrayList<>(poissons);
        resultat.sort(Comparator.comparing(poisson -> poisson.getEspece() == null ? "" : poisson.getEspece(), String.CASE_INSENSITIVE_ORDER));
        return resultat;
    }

    private List<Capteur> trierCapteurs(Collection<Capteur> capteurs) {
        List<Capteur> resultat = new ArrayList<>(capteurs);
        resultat.sort(Comparator
            .comparing((Capteur capteur) -> capteur.getClass().getSimpleName(), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(capteur -> capteur.getId() == null ? "" : capteur.getId().toString()));
        return resultat;
    }

    private List<AlertRef> collecterAlertesActives() {
        List<AlertRef> resultat = new ArrayList<>();
        for (Alerte alerte : ferme.alertesActives()) {
            resultat.add(new AlertRef(localiserZonePourAlerte(alerte), alerte));
        }
        return resultat;
    }

    private List<AlertRef> collecterHistoriqueAlertes() {
        List<AlertRef> resultat = new ArrayList<>();
        for (Alerte alerte : ferme.getHistoriqueAlertes()) {
            resultat.add(new AlertRef(localiserZonePourAlerte(alerte), alerte));
        }
        return resultat;
    }

    private ZoneGeographique localiserZonePourAlerte(Alerte alerte) {
        for (ZoneGeographique zone : ferme.getZones()) {
            if (zone.getAlt().contains(alerte)) {
                return zone;
            }
        }
        return null;
    }

    private ZoneRef selectedZone(JComboBox<ZoneRef> combo) {
        return (ZoneRef) combo.getSelectedItem();
    }

    private void restoreSelection(JComboBox<ZoneRef> combo, ZoneRef previous) {
        if (previous == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            ZoneRef current = combo.getItemAt(i);
            if (current != null && current.zone.getId().equals(previous.zone.getId())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void clearPlantationFields() {
        plantationTypeField.setText("");
        plantationPlantField.setText("");
        plantationRecolteField.setText("");
        plantationPhMinField.setText("");
        plantationPhMaxField.setText("");
        plantationHumiditeField.setText("");
    }

    private void clearAnimalFields() {
        animalAgeField.setText("");
        animalPoidsField.setText("");
        alimentQuantiteField.setText("");
        alimentTypeField.setText("");
    }

    private void clearReleveFields() {
        releveValeurField.setText("");
        releveUniteField.setText("");
        releveLatField.setText("");
        releveLonField.setText("");
    }

    private double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.trim().replace(',', '.'));
        } catch (Exception e) {
            return fallback;
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private String formatProgramme(ProgrammeAlimentaire programme) {
        if (programme == null) {
            return "non renseigné";
        }
        return String.format(Locale.ROOT, "%.2f kg de %s", programme.getQuantity(), programme.getTypealiment());
    }

    private void info(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void erreur(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private static final class ZoneRef {
        private final ZoneGeographique zone;

        private ZoneRef(ZoneGeographique zone) {
            this.zone = zone;
        }

        @Override
        public String toString() {
            return zone.getNom() + " [" + zone.getClass().getSimpleName() + ", " + (zone.isActif() ? "ACTIF" : "SUSPENDU") + "]";
        }
    }

    private static final class PlantationRef {
        private final ZoneGeographique zone;
        private final Plantation plantation;

        private PlantationRef(ZoneGeographique zone, Plantation plantation) {
            this.zone = zone;
            this.plantation = plantation;
        }

        @Override
        public String toString() {
            String type = plantation.getType() == null || plantation.getType().isEmpty() ? "Type non renseigné" : plantation.getType();
            return type + " | " + plantation.getDate_plant() + " -> " + plantation.getDate_rec() + " [" + zone.getNom() + ", " + plantation.getEpan() + "]";
        }
    }

    private static final class AnimalRef {
        private final ZoneGeographique zone;
        private final Animal animal;

        private AnimalRef(ZoneGeographique zone, Animal animal) {
            this.zone = zone;
            this.animal = animal;
        }

        @Override
        public String toString() {
            return animal.getGen() + " | âge=" + animal.getAge() + " | poids=" + animal.getPoids() + " | " + zone.getNom();
        }
    }

    private static final class CapteurRef {
        private final ZoneGeographique zone;
        private final Capteur capteur;

        private CapteurRef(ZoneGeographique zone, Capteur capteur) {
            this.zone = zone;
            this.capteur = capteur;
        }

        @Override
        public String toString() {
            return capteur.getClass().getSimpleName() + " | " + (capteur.isActif() ? "ACTIF" : "SUSPENDU") + " | " + zone.getNom();
        }
    }

    private static final class AlertRef {
        private final ZoneGeographique zone;
        private final Alerte alerte;

        private AlertRef(ZoneGeographique zone, Alerte alerte) {
            this.zone = zone;
            this.alerte = alerte;
        }

        @Override
        public String toString() {
            String etat = alerte.isAcquittee() ? "ACQUITTÉE" : "ACTIVE";
            String zoneNom = zone == null ? "?" : zone.getNom();
            return alerte.getGrv() + " | " + etat + " | " + zoneNom + " | " + alerte.getMessage() + " | " + alerte.getDate();
        }
    }

    private static final class AlertCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (component instanceof JLabel && value instanceof AlertRef) {
                ((JLabel) component).setText(value.toString());
            }
            return component;
        }
    }

    private static final class DataChartPanel extends JPanel {
        private final String title;
        private List<Double> series = Collections.emptyList();

        private DataChartPanel(String title) {
            this.title = title;
            setPreferredSize(new Dimension(600, 190));
            setBackground(Color.WHITE);
            setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new Color(210, 218, 228)),
                new EmptyBorder(10, 10, 10, 10)
            ));
        }

        void setSeries(List<Double> values) {
            this.series = values == null ? Collections.emptyList() : new ArrayList<>(values);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int width = getWidth();
                int height = getHeight();
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, width, height);

                g2.setColor(new Color(31, 45, 61));
                g2.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2.drawString(title, 10, 18);

                if (series.isEmpty()) {
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
                    g2.setColor(new Color(110, 120, 135));
                    g2.drawString("Aucune donnée à afficher.", 10, height / 2);
                    return;
                }

                int left = 45;
                int top = 28;
                int right = 15;
                int bottom = 25;
                int chartWidth = Math.max(1, width - left - right);
                int chartHeight = Math.max(1, height - top - bottom);

                double min = series.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                double max = series.stream().mapToDouble(Double::doubleValue).max().orElse(1.0);
                if (Math.abs(max - min) < 0.0001) {
                    max = min + 1.0;
                }

                g2.setColor(new Color(225, 231, 238));
                g2.drawLine(left, top, left, top + chartHeight);
                g2.drawLine(left, top + chartHeight, left + chartWidth, top + chartHeight);

                g2.setColor(new Color(83, 120, 168));
                g2.setStroke(new BasicStroke(2f));

                int previousX = -1;
                int previousY = -1;
                for (int i = 0; i < series.size(); i++) {
                    double value = series.get(i);
                    int x = left + (series.size() == 1 ? chartWidth / 2 : (int) Math.round((double) i * chartWidth / (series.size() - 1)));
                    int y = top + chartHeight - (int) Math.round((value - min) * chartHeight / (max - min));

                    if (previousX >= 0) {
                        g2.drawLine(previousX, previousY, x, y);
                    }
                    g2.fillOval(x - 3, y - 3, 6, 6);
                    previousX = x;
                    previousY = y;
                }

                g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g2.setColor(new Color(90, 100, 115));
                g2.drawString(String.format(Locale.ROOT, "%.2f", max), 5, top + 8);
                g2.drawString(String.format(Locale.ROOT, "%.2f", min), 5, top + chartHeight);
                g2.drawString("n=" + series.size(), width - 55, 18);
            } finally {
                g2.dispose();
            }
        }
    }
}
