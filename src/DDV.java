package application;

import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleDoubleProperty;

/**
 * Classe permettant de calculer la duree de vie d'un composant sur un pcb.
 * Toute les formules sont issus des feuilles excel.
 * 
 * @author FX603984
 *
 * @param <C> composant
 * @param <B> brasure
 */
public class DDV<C extends Composant, B extends BrasureComposant> {

	private StringBuilder stringLogger;
	private String td;

	private Resultat_temperature_pcb resultat_pcb1;
	private C composant;
	private B brasure;

	private Pcb pcb;

	private boolean matrice_creuse;

	private double temperature, duree, x1t1, x1t2;

	private OngletPcb onglet;

	private final static double K = Math.sqrt(2);

	private static final Logger logger = Logger.getLogger(Main.class.getName());

	public DDV(
			double temperature,
			double duree,
			String PCB,
			C composant,
			B brasure) {

		this.temperature = temperature;
		this.duree = duree;

		this.composant = composant;
		this.brasure = brasure;

		this.matrice_creuse = false;

		this.pcb = new Pcb();
		this.onglet = new OngletPcb();
		this.onglet.load_circuit(pcb, PCB);
		this.resultat_pcb1 = new Resultat_temperature_pcb(
				onglet,
				this.temperature);

		this.x1t1 = 0;
		this.x1t2 = 0;

		stringLogger = new StringBuilder();
		td = "[Temperature: " + temperature + " durée: " + duree + "]";
		stringLogger.append(
				"--- Temperature: " + temperature + " durée: " + duree
						+ " ---");
		stringLogger.append("CTE PCB: " + resultat_pcb1.getCteXY());
	}

	public double getX1t1() {
		return x1t1;
	}

	public void setX1t1(double x1t1) {
		this.x1t1 = x1t1;
	}

	public double getX1t2() {
		return x1t2;
	}

	public void setX1t2(double x1t2) {
		this.x1t2 = x1t2;
	}

	public boolean isMatrice_creuse() {
		return matrice_creuse;
	}

	public void setMatrice_creuse(boolean matrice_creuse) {
		this.matrice_creuse = matrice_creuse;
	}

	public SimpleDoubleProperty getTemperature() {
		return new SimpleDoubleProperty(temperature);
	}

	public void setTemperature(double temperature) {
		this.temperature = temperature;
		resultat_pcb1.setTemperature(temperature);
		resultat_pcb1.setCalculs();
	}

	public void setDuree(double duree) {
		this.duree = duree;
	}

	public double getDuree() {
		return duree;
	}

	private double deltaT() {
		return temperature - 20;
	}

	private double getRgm() {
		double calcul = (((Ressources.SAC305_K3_CISAILLEMENT
				* Math.pow(temperature, 2))
				+ (Ressources.SAC305_K4_CISAILLEMENT * temperature)
				+ Ressources.SAC305_K5_CISAILLEMENT))
				* Math.pow(
						(duree * 60),
						(Ressources.SAC305_K6_CISAILLEMENT * (Math.pow(
								Math.E,
								(Ressources.SAC305_K7_CISAILLEMENT
										* temperature)))));
		logger.info(td + " rgm(temperature): " + calcul);
		return calcul;
	}

	private double getDeltaMaxX() {
		double calcul = 0;

		if (composant instanceof ComposantLeadframe) {
			calcul = (temperature - 20)
					* (resultat_pcb1.getCteX()
							- ((ComposantQFN) composant).getCte(temperature))
					* ((ComposantLeadframe) composant).getX();
			logger.info(
					"composant.getCte(temperature): "
							+ ((ComposantQFN) composant).getCte(temperature)
							+ " composant.getX(): " + composant.getX()
							+ " resultat_pcb1.getCteXY():"
							+ resultat_pcb1.getCteX());

		} else {
			calcul = (temperature - 20)
					* (resultat_pcb1.getCteX() - composant.getCte(temperature))
					* composant.getX();
			logger.info(
					"composant.getCte(): " + composant.getCte(temperature)
							+ " composant.getX(): " + composant.getX()
							+ " resultat_pcb1.getCteXY:"
							+ resultat_pcb1.getCteX());
		}
		logger.severe(td + "delta max X: " + calcul);
		return calcul;
	}

	private double getDeltaMaxY() {
		double calcul = 0;
		if (composant instanceof ComposantQFN) {
			calcul = (temperature - 20)
					* (resultat_pcb1.getCteY()
							- ((ComposantQFN) composant).getCte(temperature))
					* composant.getY();
			logger.info("Y: " + composant.getY());
		} else {
			calcul = (temperature - 20)
					* (resultat_pcb1.getCteY() - composant.getCte(temperature))
					* composant.getY();
		}

		logger.severe(td + "delta max Y: " + calcul);

		return calcul;
	}

	private double getDeltaMaxXY() {
		double calcul = 0;

		if (deltaT() >= 0) {
			calcul = Math.sqrt(
					Math.pow(getDeltaMaxX(), 2) + Math.pow(getDeltaMaxY(), 2));
		} else {
			calcul = -Math.sqrt(
					Math.pow(getDeltaMaxX(), 2) + Math.pow(getDeltaMaxY(), 2));
		}
		logger.info(td + "Delta L Max(X,Y): " + calcul);
		return calcul;
	}

	private double getDXCiu() {
		double calcul = 0;

		if (composant instanceof ComposantQFN) {
			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("a")
							* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
							* K)
					* (((ComposantQFN) composant).getXX()
							/ resultat_pcb1.get_double_module_traction_x()
							- Ressources.PCB_v
									* ((ComposantQFN) composant).getXY()
									/ resultat_pcb1
											.get_double_module_traction_x())
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (((ComposantQFN) composant).getXa()
									* resultat_pcb1.getEpaisseurPcb()
									* Math.sqrt(2) * K)
							* (((ComposantQFN) composant).getLa()
									/ (resultat_pcb1
											.get_double_module_traction_x()
											- Ressources.PCB_v
													* ((ComposantQFN) composant)
															.getLb()
													/ resultat_pcb1
															.get_double_module_traction_x()));
		}

		else if (composant instanceof ComposantLCC) {

			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("b")
							* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
							* K)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXX")
							/ resultat_pcb1.get_double_module_traction_x()
							- Ressources.PCB_v * ((ComposantLCC) composant)
									.calculNombreX(composant, false, "nombreXY")
									/ resultat_pcb1
											.get_double_module_traction_y());

			/*
			 * calcul = getRgm() * brasure.getSc() /
			 * (composant.getComposant().get("b")
			 * resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2) K)
			 * (((ComposantLCC) composant).getXX() /
			 * resultat_pcb1.get_double_module_traction_x() - Ressources.PCB_v
			 * ((ComposantLCC) composant).getXY() / resultat_pcb1
			 * .get_double_module_traction_y());
			 */
			logger.info(
					td + "epaisseur PCB: " + resultat_pcb1.getEpaisseurPcb()
							+ " XX: " + ((ComposantLCC) composant).getXX()
							+ "E x: "
							+ resultat_pcb1.get_double_module_traction_x()
							+ " E y: "
							+ resultat_pcb1.get_double_module_traction_y());

		} else {
			calcul = getRgm() * brasure.getSc()
					/ (1.5 * Math.sqrt(2)
							* resultat_pcb1.get_double_module_traction_x()
							* resultat_pcb1.getEpaisseurPcb())
					* ((composant.getComposant().get("d")
							- composant.getComposant().get("b"))
							/ composant.getComposant().get("a")
							- Ressources.PCB_v
									* composant.getComposant().get("a")
									/ (4 * K * composant.getComposant()
											.get("b")));
		}
		logger.info(
				"1er terme: " + (getRgm() * brasure.getSc()
						/ (composant.getComposant().get("b")
								* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
								* K)));
		logger.info(td + "DX(ciu): " + calcul);
		return calcul;
	}

	private double getDYCiu() {
		double calcul = 0;

		if (composant instanceof ComposantQFN) {
			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("b")
							* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
							* K)
					* (((ComposantQFN) composant).getXY()
							/ resultat_pcb1.get_double_module_traction_y()
							- Ressources.PCB_v
									* ((ComposantQFN) composant).getXX()
									/ resultat_pcb1
											.get_double_module_traction_y())
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (composant.getComposant().get("b")
									* resultat_pcb1.getEpaisseurPcb()
									* Math.sqrt(2) * K)
							* (((ComposantQFN) composant).getLb()
									/ (resultat_pcb1
											.get_double_module_traction_y()
											- Ressources.PCB_v
													* ((ComposantQFN) composant)
															.getLa()
													/ resultat_pcb1
															.get_double_module_traction_y()));
		}

		else if (composant instanceof ComposantLCC) {
			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("b")
							* resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2)
							* K)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXY")
							/ resultat_pcb1.get_double_module_traction_y()
							- Ressources.PCB_v * ((ComposantLCC) composant)
									.calculNombreX(composant, false, "nombreXX")
									/ resultat_pcb1
											.get_double_module_traction_x());

			/*
			 * calcul = getRgm() * brasure.getSc() /
			 * (composant.getComposant().get("b")
			 * resultat_pcb1.getEpaisseurPcb() * Math.sqrt(2) K)
			 * (((ComposantLCC) composant).getXY() /
			 * resultat_pcb1.get_double_module_traction_y() - Ressources.PCB_v
			 * ((ComposantLCC) composant).getXX() / resultat_pcb1
			 * .get_double_module_traction_x());
			 */
		} else {
			calcul = getRgm() * brasure.getSc()
					/ (1.5 * Math.sqrt(2)
							* resultat_pcb1.get_double_module_traction_y()
							* resultat_pcb1.getEpaisseurPcb())
					* ((composant.getComposant().get("a")
							/ (4 * K * composant.getComposant().get("b")))
							- Ressources.PCB_v
									* (composant.getComposant().get("d")
											- composant.getComposant().get("b"))
									/ composant.getComposant().get("a"));
		}
		logger.info(td + "DY(ciu): " + calcul);

		return calcul;
	}

	private double getDXYCiu() {
		double calcul = 0;
		calcul = Math.sqrt(Math.pow(getDXCiu(), 2) + Math.pow(getDYCiu(), 2));
		logger.info(td + "DXY(ciu): " + calcul);
		return calcul;
	}

	private double getDXComp() {
		double calcul = 0;

		if (composant instanceof ComposantQFN) {

			double cteComposant = ((ComposantQFN) composant).getE(temperature);

			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("a")
							* composant.getComposant().get("z") * Math.sqrt(2)
							* K)
					* (((ComposantQFN) composant).getXX() / cteComposant
							- ResineEncapsulation.coefficient_poisson_k0
									* ((ComposantQFN) composant).getXY()
									/ cteComposant)
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (((ComposantQFN) composant).getXa()
									* composant.getComposant().get("z")
									* Math.sqrt(2) * K)
							* (((ComposantQFN) composant).getLa() / cteComposant
									- ResineEncapsulation.coefficient_poisson_k0
											* ((ComposantQFN) composant).getLb()
											/ cteComposant);
		}

		else if (composant instanceof ComposantLCC) {
			calcul = getRgm() * brasure.getSc()
					/ (Math.sqrt(2) * composant.getComposant().get("b")
							* composant.getComposant().get("z") * composants)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXX")
							/ composant.getE()
							- Ceramique.coefficient_poisson
									* ((ComposantLCC) composant).calculNombreX(
											composant,
											false,
											"nombreXY")
									/ composant.getE());

			/*
			 * calcul = getRgm() * brasure.getSc() / (Math.sqrt(2) *
			 * composant.getComposant().get("b")
			 * composant.getComposant().get("z") * composants) (((ComposantLCC)
			 * composant).getXX() / composant.getE() -
			 * Ceramique.coefficient_poisson ((ComposantLCC) composant).getXY()
			 * / composant.getE());
			 */
			logger.info("E composant: " + composant.getE());
		} else {
			calcul = getRgm() * brasure.getSc()
					/ (Math.sqrt(2) * composant.getE()
							* composant.getComposant().get("h"))
					* ((composant.getComposant().get("d")
							- composant.getComposant().get("b"))
							/ composant.getComposant().get("a")
							- ((BrasureComposantLeadless) brasure)
									.getCoefficientDePoisson()
									* composant.getComposant().get("a")
									/ (4 * K * composant.getComposant()
											.get("b")));
		}

		logger.info(td + "DX(composant): " + calcul);
		return calcul;
	}

	public static final double composants = (Math.sqrt(2) - 1) / 2 + 1;

	private double getDYComp() {
		double calcul = 0;

		if (composant instanceof ComposantQFN) {
			calcul = getRgm() * brasure.getSc()
					/ (composant.getComposant().get("b")
							* composant.getComposant().get("z") * Math.sqrt(2)
							* K)
					* (((ComposantQFN) composant).getXY()
							/ ((ComposantQFN) composant).getE(temperature)
							- ResineEncapsulation.coefficient_poisson_k0
									* ((ComposantQFN) composant).getXX()
									/ ((ComposantQFN) composant)
											.getE(temperature))
					+ getRgm() * ((ComposantQFN) composant).getSp()
							/ (((ComposantQFN) composant).getYa()
									* composant.getComposant().get("z")
									* Math.sqrt(2) * K)
							* (((ComposantQFN) composant).getLb()
									/ ((ComposantQFN) composant)
											.getE(temperature)
									- ResineEncapsulation.coefficient_poisson_k0
											* ((ComposantQFN) composant).getLa()
											/ ((ComposantQFN) composant)
													.getE(temperature));
		}

		else if (composant instanceof ComposantLCC) {
			calcul = getRgm() * brasure.getSc()
					/ (Math.sqrt(2) * composant.getComposant().get("b")
							* composant.getComposant().get("z") * composants)
					* (((ComposantLCC) composant)
							.calculNombreX(composant, false, "nombreXY")
							/ composant.getE()
							- Ceramique.coefficient_poisson
									* ((ComposantLCC) composant).calculNombreX(
											composant,
											false,
											"nombreXX")
									/ composant.getE());
		} else {
			calcul = getRgm() * brasure.getSc()
					/ (Math.sqrt(2) * composant.getE()
							* composant.getComposant().get("h"))
					* (composant.getComposant().get("a") / (4 * K
							* composant.getComposant().get("b")
							- ((BrasureComposantLeadless) brasure)
									.getCoefficientDePoisson()
									* (composant.getComposant().get("d")
											- composant.getComposant().get("b"))
									/ composant.getComposant().get("a")));
		}
		logger.info(td + "DY(composant): " + calcul);

		return calcul;
	}

	/* TODO: BGA */

	public double np_pastille_y() {
		double calcul = 0;

		calcul = ((int) (((ComposantBGA) composant).getPuce_y() - 1) / 2.0)
				/ ((ComposantBGA) composant).getPas();
		return calcul;
	}

	public double lnp_pastille_y() {
		double calcul = 0;

		calcul = ((ComposantBilles) composant).getPuce_y() / 2.0;
		return calcul;
	}

	public double u0_pastille_y() {
		double calcul = 0;

		calcul = lnp_pastille_y()
				/ (double) ((ComposantBilles) composant).getPas();
		return calcul;
	}

	public double n_pastille_y() {
		double calcul = 0;
		if (this.u0_pastille_y() - ((int) this.u0_pastille_y()) > 0) {
			calcul = (int) this.u0_pastille_y();
		} else {
			calcul = this.u0_pastille_y() - 1;
		}
		stringLogger.append(td + " N pastille(y): " + calcul);

		return calcul;
	}

	public double n_pastille_x() {
		double calcul = 0;

		if (this.u0_pastille_x() - ((int) (this.u0_pastille_x())) > 0) {
			calcul = (int) (this.u0_pastille_x());
		} else {
			calcul = this.u0_pastille_x() - 1;
		}
		stringLogger.append(td + " N pastille(x): " + calcul + " \n");
		return calcul;
	}

	public double lnp_pastille_x() {
		double calcul = 0;

		calcul = ((ComposantBilles) composant).getPuce_x() / 2.0;

		stringLogger.append(td + " Lnp pastille x: " + calcul + " \n");
		return calcul;
	}

	public double u0_pastille_x() {
		double calcul = 0;

		calcul = lnp_pastille_x()
				/ (double) ((ComposantBilles) composant).getPas();
		logger.info(td + "U0 pastille: " + calcul);
		return calcul;
	}

	public double Dalpha1_t1() {
		double calcul = 0;
		calcul = resultat_pcb1.getCteXY()
				- ((ComposantBilles) composant).getCte(temperature);
		logger.info(td + "Delta alpha 1: " + calcul);
		return calcul;

	}

	public double Dalpha2_t1() {
		double calcul = 0;
		calcul = resultat_pcb1.getCteXY()
				- ((ComposantBGA) composant).getCte2(temperature);
		logger.info(td + "Delta alpha 2: " + calcul);
		return calcul;

	}

	public double point_neutre_t1_x() {

		double calcul = 0;

		double deltaAlphaT1 = Dalpha1_t1();
		double deltaAlphaT2 = Dalpha2_t1();
		double lnp_pastille_x = lnp_pastille_x();

		if (deltaAlphaT1 > 0 && deltaAlphaT2 < 0) {
			calcul = -deltaAlphaT1 * lnp_pastille_x / deltaAlphaT2
					+ lnp_pastille_x;
		} else if (deltaAlphaT1 > 0 && deltaAlphaT2 > 0) {
			calcul = lnp1_matrice1_x();
		} else {
			calcul = 0;
			logger.info("FAUX delta2");
		}
		stringLogger.append("Point neutre x: " + calcul);
		return calcul;
	}

	public double getPointNeutreX() {
		double calcul = 0;

		double deltaAlpha1 = Dalpha1_t1();
		double deltaAlpha2 = Dalpha2_t1();

		if (point_neutre_t1_x() > lnp1_matrice1_x()) {
			calcul = lnp1_matrice1_x();
		} else if (deltaAlpha1 > 0 && deltaAlpha2 > 0) {
			calcul = lnp1_matrice1_x();
		} else {
			calcul = point_neutre_t1_x();
		}
		stringLogger.append("Point neutre(x) corrigé: " + calcul);
		return calcul;
	}

	public double point_neutre_t1_y() {

		double calcul = 0;
		double deltaAlphaT1 = Dalpha1_t1();
		double deltaAlphaT2 = Dalpha2_t1();
		if (deltaAlphaT1 > 0 && deltaAlphaT2 < 0) {
			calcul = -deltaAlphaT1 * this.lnp_pastille_y() / deltaAlphaT2
					+ this.lnp_pastille_y();
		} else if (deltaAlphaT1 > 0 && deltaAlphaT2 > 0) {
			calcul = lnp1_matrice1_x();
		} else {
			calcul = 0;
		}
		stringLogger.append("Point neutre(y): " + calcul);
		return calcul;
	}

	public double N_t1_x() {
		double calcul = 0;

		calcul = ((int) (getPointNeutreX())) - this.lnp1_matrice1_x();
		logger.info(td + "N x: " + calcul);
		return calcul;
	}

	public double nombre_effort_hors_pastille_x_t1() {
		double calcul = 0;

		calcul = this.lnp1_matrice1_x() - this.lnp_pastille_x() + this.N_t1_x();
		logger.info(td + " Nombre effort hors pastille(x): " + calcul);
		return calcul;

	}

	public double X_pastille_x() {
		double calcul = 0;

		calcul = (this.n_pastille_x() + 1)
				* ((2 * this.u0_pastille_x())
						+ (Ressources.RAISON) * this.n_pastille_x())
				/ 2 * ((ComposantBilles) composant).getPas();
		logger.info(td + "X pastille x: " + calcul);
		return calcul;
	}

	public double palier1_Rgm_SAC305() {
		double calcul = 0;
		calcul = Ressources.SAC305_K3_CISAILLEMENT * Math.pow(temperature, 2)
				+ Ressources.SAC305_K4_CISAILLEMENT * temperature
				+ Ressources.SAC305_K5_CISAILLEMENT;

		return calcul;

	}

	public double X_pastille_y() {
		double calcul = 0;

		calcul = (this.n_pastille_y() + 1)
				* ((2 * this.u0_pastille_y())
						+ Ressources.RAISON * this.n_pastille_y())
				/ 2 * ((ComposantBilles) composant).getPas();

		return calcul;
	}

	public double getPointNeutreY() {
		double calcul = 0;

		if (point_neutre_t1_y() > lnp1_matrice1_y()) {
			calcul = lnp1_matrice1_y();
		} else {
			calcul = point_neutre_t1_y();
		}

		return calcul;
	}

	public double N_t1_y() {
		double calcul = 0;

		calcul = ((int) (getPointNeutreY())) - this.lnp1_matrice1_y();
		logger.info(td + "N y: " + calcul);
		return calcul;
	}

	public double uo_matrice1_x() {
		double calcul = 0;
		calcul = this.taille_matrice_bloc1_x();
		logger.info(td + "U0 matrice: " + calcul);

		return calcul;
	}

	public double nx_matrice1_x() {
		double calcul = 0;

		if (this.uo_matrice1_x() - ((int) (this.uo_matrice1_x())) > 0) {
			calcul = (int) this.uo_matrice1_x();
		} else {
			calcul = this.uo_matrice1_x() - 1;
		}
		logger.info("Nx matrice x: " + calcul);

		return calcul;
	}

	public double nombre_X1_x_t1() {
		double calcul = 0;
		if (composant instanceof ComposantBGA
				|| composant instanceof ComposantCSP) {

			if (((ComposantBGA) composant).getNombre_billes_x() % 2 == 0) {
				logger.log(Level.WARNING, "Nombre X1 pair");
				calcul = this.X_pastille_x() + ((int) (this
						.nombre_effort_hors_pastille_x_t1()))
						* (this.np_pastille_x()
								+ (((ComposantBGA) composant).getPas() / 2));
			} else {
				logger.log(Level.WARNING, "Nombre X1 impair");

				calcul = this.X_pastille_x() + ((int) (this
						.nombre_effort_hors_pastille_x_t1()))
						* (this.np_pastille_x()
								+ (((ComposantBGA) composant).getPas()));
			}
		} else {
			calcul = (this.nx_matrice1_x() + 1)
					* (2 * this.uo_matrice1_x()
							+ this.nx_matrice1_x() * Ressources.RAISON)
					/ 2 * ((ComposantBilles) composant).getPas();
		}
		logger.log(Level.INFO, td + "Nombre X1 x: " + calcul);
		return calcul;

	}

	public double nombre_X1_y_t1() {
		double calcul = 0;
		if (((ComposantBGA) composant).getNombre_billes_x() % 2 == 0) {
			calcul = this.X_pastille_y()
					+ ((int) (this.nombre_effort_hors_pastille_y_t1())) * (this
							.np_pastille_y()
							+ (((ComposantBGA) composant).getPas() / 2));
		} else {
			calcul = this.X_pastille_y()
					+ ((int) (this.nombre_effort_hors_pastille_y_t1()))
							* (this.np_pastille_y()
									+ (((ComposantBGA) composant).getPas()));
		}
		logger.info("Nombre X1 y: " + calcul);
		return calcul;

	}

	public double nombre_effort_hors_pastille_y_t1() {
		double calcul = 0;
		calcul = this.lnp1_matrice1_y() - this.lnp_pastille_y() + this.N_t1_y();
		logger.info("Nombre effort hors pastille: " + calcul);

		return calcul;

	}

	protected double getNX2() {
		double calcul = 0;

		calcul = getu0X2() - 1;

		return calcul;
	}

	protected double getX2() {
		double calcul = 0;

		calcul = (getNX2() + 1)
				* ((2 * getu0X2()) + (Ressources.RAISON) * getNX2()) / 2.0
				* ((ComposantBGA) composant).getPas();

		return calcul;
	}

	public double getu0X2() {

		double calcul = 0;
		if (N_t1_x() < 0) {
			calcul = Math.abs((int) (N_t1_x())) - 1;
		} else {
			calcul = lnp1_matrice1_x();
		}

		return calcul;
	}

	public double np_pastille_x() {
		double calcul = 0;

		calcul = ((int) ((((ComposantBGA) composant).getPuce_x() - 1)) / 2.0)
				/ ((ComposantBGA) composant).getPas();
		logger.info("Np Pastille x: " + calcul);
		return calcul;
	}

	public double nombre_X1_xy_eq_t1() {

		// Si la matrice de notre Bga n'es pas pleine on entre
		// on prend en compte les longeur équivalentes entrées
		// par l'utilisateur
		if (matrice_creuse) {
			return x1t1;
		}

		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(nombre_X1_x_t1(), 2) + Math.pow(nombre_X1_y_t1(), 2));
		logger.info("Nombre X1 eq xy: " + calcul);
		return calcul;

	}

	public double palier2_Rgm_SAC305() {
		double calcul = 0;
		calcul = Ressources.SAC305_K3_CISAILLEMENT * Math.pow(temperature, 2)
				+ Ressources.SAC305_K4_CISAILLEMENT * temperature
				+ Ressources.SAC305_K5_CISAILLEMENT;
		return calcul;

	}

	public double lnp1_matrice1_xy() {
		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(taille_matrice_bloc1_x(), 2)
						+ Math.pow(taille_matrice_bloc1_y(), 2));
		logger.info("lnp1 xy: " + calcul);
		return calcul;
	}

	public double taille_matrice_bloc1_y() {
		double calcul = 0;
		calcul = (((ComposantBilles) composant).getNombre_billes_y() - 1) / 2.0;
		logger.info(
				"Nombre de billes y: "
						+ ((ComposantBilles) composant).getNombre_billes_y());
		return calcul;
	}

	public double lnp1_matrice1_y() {
		double calcul = 0;
		calcul = taille_matrice_bloc1_y()
				* ((ComposantBilles) composant).getPas();
		logger.info("Lnp1 matrice y: " + calcul);
		return calcul;

	}

	public double taille_matrice_bloc1_x() {
		double calcul = 0;

		calcul = ((double) (((ComposantBilles) composant).getNombre_billes_x()
				- 1)) / 2.0;
		logger.info(
				"Nombre Billes x: "
						+ ((ComposantBilles) composant).getNombre_billes_x());
		logger.info("Taille matrice bloc x: " + calcul);
		return calcul;
	}

	public double lnp1_matrice1_x() {
		double calcul = 0;
		calcul = taille_matrice_bloc1_x()
				* ((ComposantBilles) composant).getPas();
		logger.info("Lnp1 matrice: " + calcul);
		return calcul;

	}

	public double lnp1() {
		double calcul = 0;

		calcul = Math.sqrt(
				(Math.pow(taille_matrice_bloc1_x(), 2)
						+ Math.pow(taille_matrice_bloc1_y(), 2)))
				/ 2;
		logger.info(td + "lnp1(): " + calcul);
		return calcul;
	}

	public double palier1_DLMax1() {

		double calcul;
		/*
		 * if (composant instanceof ComposantCSP) {
		 * 
		 * double coef_dilatation_xy_pcb = resultat_pcb1.getCteXY(); double
		 * cteR_1_2_bga = ((ComposantCSP) composant) .getCter(temperature);
		 * logger.warning( "coef_dilatation_xy_pcb" + coef_dilatation_xy_pcb +
		 * " cteR_1_2_bga: " + cteR_1_2_bga); calcul = (temperature - 20)
		 * (coef_dilatation_xy_pcb - cteR_1_2_bga) Math.abs( this.N_t1_x()
		 * ((ComposantCSP) composant).getPas());
		 * 
		 * } else if (composant instanceof ComposantBGA || composant instanceof
		 * ComposantWLP) {
		 * 
		 * double coef_dilatation_xy_pcb = resultat_pcb1.getCteXY(); double
		 * ct1_composant = ((ComposantBilles) composant) .getCte(temperature);
		 * logger.info(td + "cte 1: " + ct1_composant); logger.info(td +
		 * "CTE plan pcb: " + coef_dilatation_xy_pcb); logger.info( td +
		 * "epaisseur pcb: " + resultat_pcb1.getEpaisseurPcb());
		 * 
		 * if (composant instanceof ComposantBGA) { calcul = (temperature - 20)
		 * (coef_dilatation_xy_pcb - ct1_composant) this.lnp1_matrice1_xy();
		 * 
		 * } else { calcul = (temperature - 20) (coef_dilatation_xy_pcb -
		 * ct1_composant) this.lnp1(); } } logger.info(td + "Delta L max: " +
		 * calcul);
		 */
		double lnp1 = 0;
		double coef_dilatation_xy_pcb = resultat_pcb1.getCteXY();
		// Cte2Plus1 pour BGA, SBGA, CSP mais cte pour WLP ou CBGA ou PCBGA
		double ct2Plus1_composant = ((ComposantBilles) composant)
				.getCte(temperature);
		logger.info(td + "cte 2+1: " + ct2Plus1_composant);
		logger.info(td + "CTE plan pcb: " + coef_dilatation_xy_pcb);
		logger.info(td + "epaisseur pcb: " + resultat_pcb1.getEpaisseurPcb());

		if (composant instanceof ComposantCSP) {
			lnp1 = ((ComposantCSP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantWLP) {
			lnp1 = ((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantCBGA) {
			lnp1 = ((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantPCBGA) {
			lnp1 = ((ComposantPCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
		} else if (composant instanceof ComposantBGA) {
			lnp1 = ((ComposantBGA) composant).calculNombreX(
					composant,
					this.getPointNeutreX(),
					this.getPointNeutreY(),
					false,
					"lnp1");
		}

		calcul = (temperature - 20)
				* (coef_dilatation_xy_pcb - ct2Plus1_composant) * lnp1;

		return calcul;
	}

	public double dr1_palier1() {

		double K1 = palier1_K1();
		double DLMax = palier1_DLMax1();

		double calcul = 0;

		// System.out.println(td+"k1: "+K1+" delta max: "+DLMax);
		if (Math.abs(K1) > Math.abs(DLMax)) {
			calcul = 0;
		} else if (K1 > 0 && DLMax < 0) {
			calcul = DLMax + K1;
		} else {
			calcul = DLMax - K1; /* VALEUR POUR ELSE */
		}

		logger.info(td + " dr1: " + calcul);
		return calcul;
	}

	public double palier1_Rgm_t_SAC305() {// mettre duree_paler en parametre
		double calcul = 0;

		calcul = this.palier1_Rgm_SAC305() * Math.pow(
				(duree * 60),
				Ressources.SAC305_K6_CISAILLEMENT * Math
						.exp(Ressources.SAC305_K7_CISAILLEMENT * temperature));
		return calcul;

	}

	public double getNombreX1() {
//		System.out.println("Taille de pastilles: "+((Composant_BGA) composant).getPuce_x());
//		System.out.println("this.X_pastille_x(): "+ this.X_pastille_x());
//		System.out.println(" Math.floor(nombre_effort_hors_pastille_x_t1()): "+ Math.floor(nombre_effort_hors_pastille_x_t1()));
//		System.out.println("np_pastille_x() : "+ np_pastille_x());
		double calcul = X_pastille_x()
				+ ((int) (nombre_effort_hors_pastille_x_t1()))
						* (np_pastille_x() + 1);
		return calcul;
	}

	public double nombre_x1_matrice1_x() {
		double calcul = 0;
		calcul = (this.nx_matrice1_x() + 1)
				* (2 * this.uo_matrice1_x()
						+ this.nx_matrice1_x() * Ressources.RAISON)
				/ 2 * ((ComposantBilles) composant).getPas();
		logger.info(td + "nombre X1 x: " + calcul);
		return calcul;
	}

	public double nombre_x1_matrice1_y() {
		double calcul = 0;
		calcul = (this.nx_matrice1_y() + 1)
				* (2 * this.uo_matrice1_y()
						+ this.nx_matrice1_y() * Ressources.RAISON)
				/ 2 * ((ComposantBilles) composant).getPas();
		logger.info(td + "nombre X1 y: " + calcul);
		return calcul;
	}

	public double nx_matrice1_y() {
		double calcul = 0;

		if (this.uo_matrice1_y() - ((int) (this.uo_matrice1_y())) > 0) {
			calcul = (int) this.uo_matrice1_y();
		} else {
			calcul = this.uo_matrice1_y() - 1;
		}

		return calcul;
	}

	public double uo_matrice1_y() {
		return taille_matrice_bloc1_y();
	}

	public double nombre_X1_xy_eq() {
		double calcul = 0;
		calcul = Math.sqrt(
				Math.pow(this.nombre_x1_matrice1_x(), 2)
						+ Math.pow(this.nombre_x1_matrice1_y(), 2));
		logger.info(td + "Nombre X1 xy: " + calcul);
		return calcul;
	}

	public double palier1_K1() { /*******************
									 * VOIR CE QUI VA PAS ICI
									 **************/
		double calcul = 0;

		double module_traction_pcb = resultat_pcb1
				.get_double_module_traction_xy();
		double e1_composant = ((ComposantBilles) composant)
				.getE1Traction(temperature);
		double epaisseur_pcb = resultat_pcb1.getEpaisseurPcb();
		double epaisseur_composant = Double
				.valueOf(((ComposantBilles) composant).getComposant_z());

		// System.out.println("d1: " + ((BrasureComposantBilles)
		// brasure).getD1());
		logger.log(
				Level.INFO,
				td + "module_traction_pcb: " + module_traction_pcb
						+ " e1_composant: " + e1_composant + " epaisseur_pcb: "
						+ epaisseur_pcb + " epaisseur_composant: "
						+ epaisseur_composant + " pas: "
						+ ((ComposantBilles) composant).getPas() + " rgm: "
						+ this.palier1_Rgm_t_SAC305());

		if (composant instanceof ComposantBGA
				|| composant instanceof ComposantCSP
				|| composant instanceof ComposantPCBGA) {

			if (composant instanceof ComposantCSP) {

				calcul = (((ComposantCSP) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX"))
						/ ((ComposantBilles) composant).getPas()
						* this.palier1_Rgm_t_SAC305() * Math.PI
						* Math.pow(
								((BrasureComposantBilles) brasure).getD1(),
								2)
						/ 4.0
						* ((1 - Ressources.PCB_v)
								/ (module_traction_pcb * epaisseur_pcb)
								+ (1 - Ressources.COMPOSANT_v)
										/ (e1_composant * epaisseur_composant));
				// BGA, SBGA
			} else if (composant instanceof ComposantBGA) {

				calcul = (((ComposantBGA) composant).calculNombreX(
						composant,
						this.getPointNeutreX(),
						this.getPointNeutreY(),
						false,
						"nombreX")) / ((ComposantBilles) composant).getPas()
						* this.palier1_Rgm_t_SAC305() * Math.PI
						* Math.pow(
								((BrasureComposantBilles) brasure).getD1(),
								2)
						/ 4.0
						* ((1 - Ressources.PCB_v)
								/ (module_traction_pcb * epaisseur_pcb)
								+ (1 - Ressources.COMPOSANT_v)
										/ (e1_composant * epaisseur_composant));

			} else if (composant instanceof ComposantPCBGA) {
				calcul = (((ComposantPCBGA) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX"))
						/ ((ComposantBilles) composant).getPas()
						* this.palier1_Rgm_t_SAC305() * Math.PI
						* Math.pow(
								((BrasureComposantBilles) brasure).getD1(),
								2)
						/ 4.0
						* ((1 - Ressources.PCB_v)
								/ (module_traction_pcb * epaisseur_pcb)
								+ (1 - Ressources.COMPOSANT_v)
										/ (e1_composant * epaisseur_composant));
				// System.out.println("K1 a " + temperature + " :" + calcul);

			}
		}
		// Composant CBGA
		else if (composant instanceof ComposantCBGA) {
			logger.info(
					td + "d1 : " + ((BrasureComposantBilles) brasure).getD1());

			calcul = ((double) (((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX")))
					/ ((ComposantBilles) composant).getPas()
					* this.palier1_Rgm_t_SAC305() * Math.PI
					* Math.pow(((BrasureComposantBilles) brasure).getD1(), 2)
					/ 4.0
					* ((1 - Ressources.PCB_v)
							/ (module_traction_pcb * epaisseur_pcb)
							+ (1 - Ceramique.coefficient_poisson)
									/ (e1_composant * epaisseur_composant));

			// Composant WLP
		} else {
			logger.info(
					td + "d1 : " + ((BrasureComposantBilles) brasure).getD1());

			calcul = ((double) (((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX")))
					/ ((ComposantBilles) composant).getPas()
					* this.palier1_Rgm_t_SAC305() * Math.PI
					* Math.pow(((BrasureComposantBilles) brasure).getD1(), 2)
					/ 4.0
					* ((1 - Ressources.PCB_v)
							/ (module_traction_pcb * epaisseur_pcb)
							+ (1 - Silicium.coefficient_poisson_k0)
									/ (e1_composant * epaisseur_composant));

		}
		logger.log(Level.INFO, td + "K1: " + calcul);
		return calcul;
	}

	public double palier1_yc1() {
		double calcul = 0;

		if (((BrasureComposantBilles) brasure).getTechnologie()
				.equals("NSMD")) {

			calcul = this.dr1_palier1() / ((BrasureComposantBilles) brasure)
					.getHauteurIntegreeNsmd();

		} else {

			double palierDR1 = dr1_palier1();
			double HI = ((BrasureComposantBilles) brasure)
					.getHauteurIntegreeSmd();

			calcul = this.dr1_palier1() / ((BrasureComposantBilles) brasure)
					.getHauteurIntegreeSmd();
		}

		stringLogger.append(" YC1: " + calcul);

		logger.info(td + "Yc1: " + calcul);
		logger.log(Level.INFO, td + stringLogger.toString());

		return calcul;

	}

	/**
	 * Renvoie N50% pour les composants à Billes en connaissant l'angle de
	 * cisaillement de la température basse. Au cours du traitement la fonction
	 * ira cherher l'angle de cisaillement à la température haute.
	 * 
	 * @param yc12 L'angle de cisaillement à température basse
	 * 
	 * @return N50%
	 */
	public double calcul_cycle_bille_angles_pastille(double yc12) {
		double calcul = 0;
		double _yc11 = this.palier1_yc1();
		double _yc12 = yc12;

		calcul = Outils.absolute_difference(_yc11, _yc12);

		// double resultat = Math.pow(
		// calcul / (((Math.PI * Math
		// .pow(((BrasureComposantBilles) brasure).getD1(), 2)
		// / 4.0) + 1) * Ressources.COEFFICIENT_FATIGUE_A),
		// (1.0 / Ressources.COEFFICIENT_FATIGUE_B));

		double resultat = Math.pow(
				calcul / (((Math.PI * Math
						.pow(((BrasureComposantBilles) brasure).getD1(), 2)
						/ 4.0) + 1) * composant.getCoefficientFatigueA()),
				(1.0 / composant.getCoefficientFatigueB()));
		return resultat;
	}

	/* TODO: FIN BGA */

	private double getDXYComp() {
		logger.log(
				Level.WARNING,
				td + "DXYComp: " + Math.sqrt(
						Math.pow(getDXComp(), 2) + Math.pow(getDYComp(), 2)));
		return Math.sqrt(Math.pow(getDXComp(), 2) + Math.pow(getDYComp(), 2));
	}

	public double getYC1() {
		double calcul = 0;
		double deltaMaxXY = getDeltaMaxXY();

		double DXYCiu = getDXYCiu();
		double DXComp = getDXComp();
		double DXYComp = getDXYComp();

		double hauteur_integree = brasure.getHi();

		if (composant instanceof ComposantLCC
				|| composant instanceof ComposantQFN) {
			calcul = getDeltaR() / hauteur_integree;
		} else {
			if (Math.abs(DXYCiu + DXComp) >= deltaMaxXY) {
				calcul = 0;
			} else {
				calcul = (deltaMaxXY - (DXYCiu + DXYComp)) / hauteur_integree;
			}
		}
		logger.severe(td + "YC1: " + calcul);
		return calcul;
	}

	private double getDeltaR() {
		double calcul = 0;

		double DXYCiu = getDXYCiu();
		double DeltaMaxXY = getDeltaMaxXY();
		double DXYComp = getDXYComp();

		if (Math.abs((DXYCiu + DXYComp)) > Math.abs(DeltaMaxXY)) {
			calcul = 0;
		} else if (DeltaMaxXY < 0 && (DXYCiu + DXYComp) > 0) {
			calcul = DeltaMaxXY + DXYCiu + DXYComp;
		} else {
			calcul = DeltaMaxXY - DXYCiu - DXYComp;
		}
		logger.severe(td + "delta R: " + calcul);
		return calcul;
	}

	/**
	 * Renvoie N50% à partir de l'angle de cisaillement à froid. Au cours du
	 * calcul la fonction ira chercher l'angle de cisaillement à chaud.
	 * 
	 * @param yc12 angle cisaillement t2 (température basse)
	 * @return N50%
	 */
	public double getN50(double yc12) {

		// Angle de cisaillement température haute
		double yc11 = getYC1();

		double calcul = 0;

		calcul = Outils.absolute_difference(yc11, yc12);

		// calcul = Math.pow(
		// ((calcul / (1 + brasure.getSc()))
		// * (1.0 / Ressources.COEFFICIENT_FATIGUE_A)),
		// 1.0 / Ressources.COEFFICIENT_FATIGUE_B);

		calcul = Math.pow(
				((calcul / (1 + brasure.getSc()))
						* (1.0 / composant.getCoefficientFatigueA())),
				1.0 / composant.getCoefficientFatigueB());
		logger.info(td + "Sc: " + brasure.getSc() + " Hi:" + brasure.getHi());
		logger.info(td + " N50%: " + calcul);

		return calcul;
	}

	/**
	 * 
	 * @return Le nombre X d'un composant à Billes.
	 */
	public double getNombreXComposantABilles() {
		double nombreX;

		if (composant instanceof ComposantBGA
				|| composant instanceof ComposantCSP
				|| composant instanceof ComposantPCBGA) {

			if (composant instanceof ComposantCSP) {
				nombreX = ((ComposantCSP) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
				return nombreX;
			} else if (composant instanceof ComposantBGA) {

				nombreX = ((ComposantBGA) composant).calculNombreX(
						composant,
						this.getPointNeutreX(),
						this.getPointNeutreY(),
						false,
						"nombreX");
				return nombreX;
			} else if (composant instanceof ComposantPCBGA) {
				nombreX = ((ComposantPCBGA) composant)
						.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
				return nombreX;
			}
		}
		// Composant CBGA
		else if (composant instanceof ComposantCBGA) {

			nombreX = ((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
			return nombreX;
			// Composant WLP
		} else {

			nombreX = ((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "nombreX");
			return nombreX;
		}
		return (Double) null;
	}

	public double getLnp1ComposantABilles() {
		double lnp1;

		if (composant instanceof ComposantCSP) {
			lnp1 = ((ComposantCSP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantWLP) {
			lnp1 = ((ComposantWLP) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantCBGA) {
			lnp1 = ((ComposantCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantPCBGA) {
			lnp1 = ((ComposantPCBGA) composant)
					.calculNombreX(composant, 0.0, 0.0, false, "lnp1");
			return lnp1;
		} else if (composant instanceof ComposantBGA) {
			lnp1 = ((ComposantBGA) composant).calculNombreX(
					composant,
					this.getPointNeutreX(),
					this.getPointNeutreY(),
					false,
					"lnp1");
			return lnp1;
		}
		return (Double) null;
	}

	/*
	 * Renvoie la valeur de delta gamma d'une température DDV
	 */
	public double getCalculs() {
		if (composant instanceof ComposantBilles) {
			return palier1_yc1();
		} else {
			return getYC1();
		}
	}

}
