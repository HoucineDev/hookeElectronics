package application;

import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author FX603984 Les valeurs du tableau sont dans l'ordre: d, a, b, h,
 *         Masse(g)
 *
 *         (tiré du fichier excel Résistances AncEA.xlsx)
 */
public class Resistance {

	/* Ceramique LCCC ; Resistances */

	public static final double epaisseur = 0.5;

	public static final double beta = 3.3;
	public static final double vernis = 2.3;

	/* Représentation des resitances voir le fichier excel */

	private static final double[] FORMAT_01005 = {
			0.4, 0.2, 0.1, 0.13, 4.00E-05, 2.00E-02, 5.17E-02
	};
	private static final double[] FORMAT_0201 = {
			0.6, 0.3, 0.15, 0.23, 1.40E-04, 4.50E-02, 5.04E-02
	};
	private static final double[] FORMAT_0402 = {
			1, 0.5, 0.25, 0.35, 6.80E-04, 1.25E-01, 4.77E-02
	};
	private static final double[] FORMAT_0603 = {
			1.6, 0.8, 0.3, 0.45, 2.14E-03, 2.40E-01, 4.37E-02
	};
	private static final double[] FORMAT_0805 = {
			2, 1.25, 0.3, 0.50, 4.54E-03, 3.75E-01, 4.00E-02
	};
	private static final double[] FORMAT_1206 = {
			3.2, 1.6, 0.4, 0.60, 9.14E-03, 6.40E-01, 3.75E-02
	};
	// Erreur fde format ? 2.5 au lieu de 2.6
	private static final double[] FORMAT_1210 = {
			3.2, 2.6, 0.4, 0.60, 1.55E-02, 1.04E+00, 3.67E-02
	};
	private static final double[] FORMAT_2010 = {
			5, 2.5, 0.65, 0.60, 2.43E-02, 1.63E+00, 3.67E-02
	};
	private static final double[] FORMAT_2512 = {
			6.3, 3.1, 0.65, 0.60, 3.71E-02, 2.02E+00, 3.27E-02
	};

	private HashMap<String, Double> format;
	private HashMap<String, HashMap<String, Double>> formats;

	private double[] dimensions;

	private String nom;

	public HashMap<String, Double> getFormat(String format) {
		return formats.get(format);
	}

	/**
	 * 
	 * @param nomFormat
	 * @return Une HashMap où les clés sont les caractéristiques de la
	 *         Résistance et les valeurs associées aux clés , des résultats
	 *         dependant du format de Résistance choisi
	 */
	public static HashMap<String, Double> getResistance(String nomFormat) {

		double[] dimensions;
		HashMap<String, Double> format = new HashMap<>();
		int i = 0;

		double calcul = 0;

		if (nomFormat.equals("01005"))
			dimensions = FORMAT_01005;
		else if (nomFormat.equals("0201"))
			dimensions = FORMAT_0201;
		else if (nomFormat.equals("0402"))
			dimensions = FORMAT_0402;
		else if (nomFormat.equals("0603"))
			dimensions = FORMAT_0603;
		else if (nomFormat.equals("0805"))
			dimensions = FORMAT_0805;
		else if (nomFormat.equals("1206"))
			dimensions = FORMAT_1206;
		else if (nomFormat.equals("1210"))
			dimensions = FORMAT_1210;
		else if (nomFormat.equals("2010"))
			dimensions = FORMAT_2010;
		else if (nomFormat.equals("2512"))
			dimensions = FORMAT_2512;
		else
			dimensions = new double[FORMAT_01005.length];

		for (String s : new String[] {
				"d", "a", "b", "h", "masse", "s", "h1"
		}) {
			if (s.equals("h1")) {
				// calcul = -2 * format.get("masse")
				// / (2 * format.get("s") * format.get("h")) + 0.065;
				calcul = 0.05 - format.get("masse") / (2 * format.get("s"));
				format.put(s, calcul);
			} else {
				format.put(s, dimensions[i++]);
			}
		}

		return format;
	}

	public Resistance(String nom, double[] dimensions) {
		this.nom = nom;

	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public double[] getDimensions() {
		return this.dimensions;
	}

	public void setDimensions(double[] dimensions) {
		this.dimensions = dimensions;
	}

	public String toString() {
		return this.nom;
	}

	public static double[] getDimension(String nomFormat) {
		double[] dimensions;
		if (nomFormat.equals("01005"))
			dimensions = FORMAT_01005;
		else if (nomFormat.equals("0201"))
			dimensions = FORMAT_0201;
		else if (nomFormat.equals("0402"))
			dimensions = FORMAT_0402;
		else if (nomFormat.equals("0603"))
			dimensions = FORMAT_0603;
		else if (nomFormat.equals("0805"))
			dimensions = FORMAT_0805;
		else if (nomFormat.equals("1206"))
			dimensions = FORMAT_1206;
		else if (nomFormat.equals("1210"))
			dimensions = FORMAT_1210;
		else if (nomFormat.equals("2010"))
			dimensions = FORMAT_2010;
		else if (nomFormat.equals("2512"))
			dimensions = FORMAT_2512;
		else
			dimensions = new double[FORMAT_01005.length];

		return dimensions;
	}

	/**
	 * 
	 * @return Une liste contenant les différents formats existants des
	 *         Resistances. Note: On utilise ici une "ObservableList" et non un
	 *         array car on a besoin de mettre des "Listeners d'évènements"
	 *         dessus
	 */
	public static ObservableList<String> getResistances() {
		ObservableList<String> resistances = FXCollections.observableArrayList(
				"01005",
				"0201",
				"0402",
				"0603",
				"0805",
				"1206",
				"1210",
				"2010",
				"2512");
		return resistances;
	}
}
