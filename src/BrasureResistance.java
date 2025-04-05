package application;

import java.util.HashMap;

import javafx.scene.image.ImageView;

public class BrasureResistance extends BrasureComposantLeadless
		implements InterfaceBrasureComposant {

	public static ImageView image = new ImageView(
			BrasureResistance.class.getResource("images/RESISTANCE_BRASURE.png")
					.toExternalForm());

	private final static double vernis_eparnge = 0.02;

	public BrasureResistance() {
		super();
	}

	public BrasureResistance(String path) {
		super(path);
		composant = Resistance.getResistance(format);

		infoComposant = "as: " + getAs() + " bs: " + getBs() + " es: " + getEs()
				+ "\n a1: " + getA1() + " b1: " + getB1() + " c1: " + getC1();
	}

	public HashMap<String, Double> getResistance() {
		return composant;
	}

	protected void setFormat(HashMap<String, Double> composant) {
		this.composant = composant;
	}

	public void setFormat(String format) {
		this.format = format;
		composant = Resistance.getResistance(format);
	}

	protected double getH1() {
		return composant.get("h1");
	}

	protected double getVa() { /* OK */
		double calcul = 0;

		calcul = getAs() * getBs() * (getEs() + vernis_eparnge) / 2;

		return calcul;
	}

	@Override
	protected double getCoefficientDePoisson() {
		return Ceramique.coefficient_poisson;
	}

	protected double getV1() {
		double calcul = 0;

		calcul = composant.get("a") * composant.get("b") * getH1()
				+ (getA1() - composant.get("a")) * getH1() * composant.get("b");

		return calcul;
	}

	protected double getV2() {
		double calcul = 0;

		calcul = getVa() - getV1();

		return calcul;
	}

	protected double getHxH1() {
		double calcul = 0;

		if (Math.sqrt(2 * getV2() / composant.get("a")) < composant.get("h")) {
			calcul = Math.sqrt(2 * getV2() / composant.get("a"));
		} else {
			calcul = composant.get("h") + getH1();
		}

		return calcul;
	}

	protected double getHx() {
		return getHxH1() - getH1();
	}

	protected double getC() {
		double calcul = 0;

		calcul = 2 * getV2() / (composant.get("a") * getHxH1());

		return calcul;
	}

	protected double getV45() {
		double calcul = 0;

		calcul = Math.pow((getH1() + getHxH1()), 2) / 2 * composant.get("b");

		return calcul;
	}

	protected double getV2V45() {
		double calcul = 0;

		calcul = getV2() - getV45();

		return calcul;
	}

	protected double getTheta() {
		double calcul = 0;
		if (composant.get("h") == getHx()) {
			calcul = Math.atan(getC() / getHxH1());
		} else {
			calcul = Math.PI / 4;
		}
		return calcul;
	}

	protected double getB2() {
		double calcul = 0;

		calcul = getHx() * Math.sin(getTheta());

		return calcul;
	}

	protected double getH2() {
		double calcul = 0;
		calcul = (getHx() + 2 * getH1()) * Math.tan(getTheta())
				* Math.sqrt(Math.pow(getC(), 2) + Math.pow(getHxH1(), 2))
				/ (2 * getC());
		return calcul;
	}

	@Override
	public double getHeq() {
		double calcul = 0;

		calcul = getH1() * getH2() * (composant.get("b") + getB2())
				/ (composant.get("b") * getH2() + getB2() * getH1());

		return calcul;
	}

	protected double getD1() {
		double calcul = 0;

		calcul = ((getC1() + 2 * getB1()) - composant.get("d")) / 2;

		return calcul;
	}

	@Override
	protected void calcul_heq() {
		heq.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getHeq(), 3)));
	}

	public double getSc() {
		double calcul = 0;

		calcul = (composant.get("b") + getB2()) * composant.get("a") / 2;

		return calcul;

	}

	@Override
	protected void calcul_sc() {
		Sc.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getSc(), 3)));
	}

	@Override
	public double getHi() {

		double calcul;

		if (composant.get("a") == getA1()) {

			calcul = this.getHeq();

		} else if (composant.get("a") < getA1()) {

			calcul = (composant.get("a") * getHeq()
					/ (composant.get("a") - getA1()))
					* Math.log(composant.get("a") / getA1());

		} else {
			calcul = getA1();
		}
		/*
		 * // Afin d'eviter une division par 0 if (composant.get("a") ==
		 * getA1()) { calcul = getH1(); } else {
		 * 
		 * calcul = getSc() * getHeq() / (getHxH1() * (composant.get("a") -
		 * getA1())) Math.log(composant.get("a") / getA1()); }
		 */
		return calcul;
	}

	@Override
	protected void calcul_Hi() {
		Hi.set_resultat(
				String.valueOf(Outils.nombreChiffreApresVirgule(getHi(), 3)));
	}

	@Override
	protected void setCalculs() {
		calcul_heq();
		calcul_sc();
		calcul_Hi();
	}

}
