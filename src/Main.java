package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

	private static final String pathLog = Ressources.path + "\\Logs\\"
			+ "logancea.log";

	// classe principale dans laquelle sont créés les 4 onglets de calculs
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	private Handler fichierLog;

	private OngletPcb fenetre_circuit;
	private OngletComposant fenetre_composant;
	private OngletBrasure fenetre_brasure;
	private OngletDuree fenetre_durees;
	private OngletCarte fenetre_carte;
	private TabPane tabPane;

	@Override
	public void start(Stage primaryStage) throws Exception {

		initLogger();

		fenetre_circuit = new OngletPcb();
		ScrollPane sp_circuit = new ScrollPane(fenetre_circuit);

		fenetre_composant = new OngletComposant();
		ScrollPane sp_composant = new ScrollPane(fenetre_composant);

		fenetre_brasure = new OngletBrasure(10);
		ScrollPane sp_brasure = new ScrollPane(fenetre_brasure);

		fenetre_durees = new OngletDuree(10);
		ScrollPane sp_duree_de_vie = new ScrollPane(fenetre_durees);

		fenetre_carte = new OngletCarte(10);
		ScrollPane sp_carte = new ScrollPane(fenetre_carte);

		Tab tabPcb = new Tab("PCB");
		tabPcb.setContent(sp_circuit);
		tabPcb.setClosable(false);

		Tab tabComposant = new Tab("Composant");
		tabComposant.setContent(sp_composant);
		tabComposant.setClosable(false);

		Tab tabBrasure = new Tab("Brasure");
		tabBrasure.setContent(sp_brasure);
		tabBrasure.setClosable(false);

		Tab tabDureeDeVie = new Tab("Durée de vie");
		tabDureeDeVie.setContent(sp_duree_de_vie);
		tabDureeDeVie.setClosable(false);

		Tab tabCarte = new Tab("Carte");
		tabCarte.setContent(sp_carte);
		tabCarte.setClosable(false);

		tabPane = new TabPane();

		tabPane.getTabs().addAll(tabPcb, tabComposant, tabBrasure,
				tabDureeDeVie, tabCarte);

		MenuBar menuBar = init_bar_menu(primaryStage);

		BorderPane borderPane = new BorderPane();
		borderPane.setTop(menuBar);
		borderPane.setCenter(tabPane);

		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();

		Group root = new Group();
		root.getChildren().add(borderPane);

		Scene scene = new Scene(root, bounds.getWidth(), bounds.getHeight(),
				Color.rgb(0, 0, 0, 0));
		scene.getStylesheets().add("application/application.css");

		borderPane.prefWidthProperty().bind(scene.widthProperty());
		borderPane.prefHeightProperty().bind(scene.heightProperty());

		fenetre_circuit.prefWidthProperty().bind(borderPane.widthProperty());
		fenetre_composant.prefWidthProperty().bind(borderPane.widthProperty());
		fenetre_durees.prefWidthProperty().bind(borderPane.widthProperty());
		fenetre_carte.prefWidthProperty().bind(borderPane.widthProperty());

		primaryStage.setTitle("AnCEA");
		primaryStage.getIcons().add(new Image(Ressources.LOGO_SAFRAN));
		primaryStage.setMaximized(true);
		// primaryStage.setX(bounds.getMinX());
		// primaryStage.setY(bounds.getMinY());
		// primaryStage.setWidth(bounds.getWidth());
		// primaryStage.setHeight(bounds.getHeight());
		primaryStage.setScene(scene);

		primaryStage.show();
	}

	private void initLogger() {
		try {
			fichierLog = new FileHandler(pathLog);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		fichierLog.setFormatter(new SimpleFormatter());
		fichierLog.setLevel(Level.OFF);
		logger.setLevel(Level.OFF);
		logger.addHandler(fichierLog);

	}

	public static void main(String... args) {
		Application.launch(args);
	}

	public MenuBar init_bar_menu(Stage s) {
		MenuBar barMenu = new MenuBar();

		Menu menuFichier = new Menu("Fichier");
		Menu menuPCB = new Menu("PCB");

		MenuItem ItemQuitter = new MenuItem("Exit");
		MenuItem ItemImprimer = new MenuItem("Imprimer");

		MenuItem ItemChargementPCB = new MenuItem("Chargement PCB");
		MenuItem ItemSauvegardePCB = new MenuItem("Sauvegarde PCB");

		Menu menuExport = new Menu("Exporter vers Excel");
		MenuItem menuItemExportResultatPCB = new MenuItem("Resultat PCB ");
		menuItemExportResultatPCB.setOnAction(event -> {
			fenetre_circuit.exportToCsv();
		});

		MenuItem menuItemExportResultatProfilDeVie = new MenuItem(
				"Resultat profil de vie");
		menuItemExportResultatProfilDeVie.setOnAction(event -> {
			fenetre_durees.exportToCsv();
		});

		MenuItem menuItemExportResultatComposant = new MenuItem(
				"Resultat composant");
		menuItemExportResultatComposant.setOnAction(event -> {
			fenetre_composant.exportToCsv();
		});

		MenuItem menuItemExportResultatCarte = new MenuItem("Resultat carte");
		menuItemExportResultatCarte.setOnAction(event -> {
			fenetre_carte.exportToCsv();
		});

		menuExport.getItems().addAll(menuItemExportResultatPCB,
				menuItemExportResultatProfilDeVie,
				menuItemExportResultatComposant, menuItemExportResultatCarte);

		ItemQuitter.setAccelerator(
				new KeyCodeCombination(KeyCode.Q, KeyCombination.CONTROL_DOWN));
		ItemImprimer.setAccelerator(
				new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN));

		ItemImprimer.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Printer printer = Printer.getDefaultPrinter();
				PageLayout pageLayout = printer.createPageLayout(
						Paper.NA_LETTER, PageOrientation.PORTRAIT,
						Printer.MarginType.DEFAULT);
				double scaleX = pageLayout.getPrintableWidth() / s.getWidth();
				double scaleY = pageLayout.getPrintableHeight() / s.getWidth();
				tabPane.getTransforms().add(new Scale(scaleX, scaleY));

				PrinterJob job = PrinterJob.createPrinterJob();
				if (job != null) {
					boolean success = job.printPage(tabPane.getSelectionModel()
							.getSelectedItem().getContent());
					if (success) {
						job.endJob();
					}
				}
			}
		});
		ItemQuitter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				s.close();
			}
		});

		ItemChargementPCB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				try {
					fenetre_circuit.vb_pcb.getChildren().clear();
					fenetre_circuit.liste_couches_pcb = fenetre_circuit.pcb
							.get_liste_couches_pcb();
					fenetre_circuit.nombre_vialaser.setText(String
							.valueOf(fenetre_circuit.pcb.nombre_vialaser));
					fenetre_circuit.nombre_couche.setText(
							String.valueOf(fenetre_circuit.pcb.nombre_couche));
					fenetre_circuit.vb_pcb.getChildren()
							.addAll(fenetre_circuit.pcb.al);
					fenetre_circuit.button_calculer.setVisible(true);
				} catch (NullPointerException npe) {

					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Erreur Chargement PCB");
					alert.setHeaderText("Probleme de chargement");
					alert.setContentText(
							"Vous devez d'abord générer un circuit");
					alert.showAndWait();
				}

			}
		});
		ItemSauvegardePCB.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {

				FileChooser fileChooser = new FileChooser();

				// Set extension filter
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
						"PCB (*.pcb)", "*.pcb");
				fileChooser.getExtensionFilters().add(extFilter);

				// Show save file dialog
				File file = fileChooser.showSaveDialog(new Stage());

				if (file != null) {

					try {

						PrintWriter pw = new PrintWriter(
								new BufferedWriter(new FileWriter(file)));
						pw.println(fenetre_circuit.get_Resine().toString());
						for (int i = 0; i < fenetre_circuit.pcb.al
								.size(); i++) {

							pw.println(
									(fenetre_circuit.pcb.al.get(i).toString()));
						}

						pw.close();

					} catch (IOException ex) {
						Logger.getLogger(OngletPcb.class.getName())
								.log(Level.SEVERE, null, ex);
					}
				}
				// Actualisation des différentes ComboBox
				fenetre_circuit.rafraichir_cb_liste_pcb();
				fenetre_durees.rafraichir_cb_pcb_existants();
				fenetre_carte.rafraichir_cbPcb();
			}
		});

		menuFichier.getItems().addAll(menuExport, ItemImprimer, ItemQuitter);
		menuPCB.getItems().addAll(ItemChargementPCB, ItemSauvegardePCB);

		barMenu.getMenus().addAll(menuFichier, menuPCB);

		return barMenu;

	}
}