package application;

/**
 * 
 * Cette classe contient les propriétés physiques de la résine PCL370 Isola.
 *
 */
public class ResinePCL370Isola extends ModelPhysiqueStatique {

	static final double masse_volumique_k0 = 1.49;

	static final double conductivite_thermique_k0 = 0.2;

	static final double module_elasticite_k0 = 3110;
	static final double module_elasticite_k1 = 6.01;
	static final double module_elasticite_k2 = 7.2E-03;

	static final double coefficient_poisson_k0 = 0.33;

	static final double coefficient_dilatation_xy_k0 = 4.21E-05;
	static final double coefficient_dilatation_xy_k1 = 7.00E-08;
	static final double coefficient_dilatation_xy_k2 = 4.00E-12;

	static final double coefficient_dilatation_z_k0 = 6.34E-05;
	static final double coefficient_dilatation_z_k1 = 8.00E-08;
	static final double coefficient_dilatation_z_k2 = 2.00E-10;

	static final double amorti_k0 = 3.46E-02;
	static final double amorti_k1 = -1.00E-04;
	static final double amorti_k2 = 1.00E-06;
	static final double amorti_k3 = 6.00E-10;
	static final double amorti_k4 = -1.00E-11;

	public ResinePCL370Isola() {
		super();
	}

	public double masse_volumique(double temperature) {
		double liste[] = {
				ResinePCL370Isola.masse_volumique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double conductibilite(double temperature) {
		double liste[] = {
				ResinePCL370Isola.conductivite_thermique_k0
		};
		return super.calcul(liste, temperature);
	}

	public double module_elastique(double temperature) {
		double liste[] = {
				ResinePCL370Isola.module_elasticite_k0,
				ResinePCL370Isola.module_elasticite_k1,
				ResinePCL370Isola.module_elasticite_k2
		};
		return super.calcul(liste, temperature);
	}

	public double coef_poisson(double temperature) {
		double liste[] = {
				ResinePCL370Isola.coefficient_poisson_k0
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_xy(double temperature) {
		double liste[] = {
				ResinePCL370Isola.coefficient_dilatation_xy_k0,
				ResinePCL370Isola.coefficient_dilatation_xy_k1,
				ResinePCL370Isola.coefficient_dilatation_xy_k2,
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_z(double temperature) {
		double liste[] = {
				ResinePCL370Isola.coefficient_dilatation_z_k0,
				ResinePCL370Isola.coefficient_dilatation_z_k1,
				ResinePCL370Isola.coefficient_dilatation_z_k2,
		};
		return super.calcul(liste, temperature);
	}

	public double amorti(double temperature) {
		double liste[] = {
				ResinePCL370Isola.amorti_k0, ResinePCL370Isola.amorti_k1,
				ResinePCL370Isola.amorti_k2, ResinePCL370Isola.amorti_k3,
				ResinePCL370Isola.amorti_k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_x(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;

		if (tissus.equals("1037")) {
			k2 = 2.040E-10;
			k1 = 4.308E-08;
			k0 = 1.769E-05;
		} else if (tissus.equals("106")) {
			k2 = 2.007E-10;
			k1 = 4.232E-08;
			k0 = 1.737E-05;
		} else if (tissus.equals("1078")) {
			k2 = 1.787E-10;
			k1 = 3.723E-08;
			k0 = 1.542E-05;
		} else if (tissus.equals("1080")) {
			k2 = 1.629E-10;
			k1 = 3.351E-08;
			k0 = 1.411E-05;
		} else if (tissus.equals("2113")) {
			k2 = 1.226E-10;
			k1 = 2.442E-08;
			k0 = 1.130E-05;
		} else if (tissus.equals("2116")) {
			k2 = 1.379E-10;
			k1 = 2.797E-08;
			k0 = 1.240E-05;
		} else if (tissus.equals("1506")) {
			k2 = 1.200E-10;
			k1 = 2.394E-08;
			k0 = 1.119E-05;
		} else if (tissus.equals("7628")) {
			k2 = 1.147E-10;
			k1 = 2.284E-08;
			k0 = 1.089E-05;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public double coef_dil_y(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;

		if (tissus.equals("1037")) {
			k2 = 2.053E-10;
			k1 = 4.338E-08;
			k0 = 1.776E-05;
		} else if (tissus.equals("106")) {
			k2 = 2.022E-10;
			k1 = 4.263E-08;
			k0 = 1.745E-05;
		} else if (tissus.equals("1078")) {
			k2 = 1.788E-10;
			k1 = 3.728E-08;
			k0 = 1.545E-05;
		} else if (tissus.equals("1080")) {
			k2 = 1.900E-10;
			k1 = 3.978E-08;
			k0 = 1.620E-05;
		} else if (tissus.equals("2113")) {
			k2 = 2.065E-10;
			k1 = 4.360E-08;
			k0 = 1.734E-05;
		} else if (tissus.equals("2116")) {
			k2 = 1.447E-10;
			k1 = 2.943E-08;
			k0 = 1.278E-05;
		} else if (tissus.equals("1506")) {
			k2 = 1.686E-10;
			k1 = 3.483E-08;
			k0 = 1.439E-05;
		} else if (tissus.equals("7628")) {
			k2 = 1.565E-10;
			k1 = 3.210E-08;
			k0 = 1.354E-05;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public double module_tissus_x(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;
		if (tissus.equals("1037")) {
			k2 = 0.0065;
			k1 = 2.364;
			k0 = 7879;
		} else if (tissus.equals("106")) {
			k2 = 0.0065;
			k1 = 2.2178;
			k0 = 8075;
		} else if (tissus.equals("1078")) {
			k2 = 0.0063;
			k1 = 1.1593;
			k0 = 9506;
		} else if (tissus.equals("1080")) {
			k2 = 0.006;
			k1 = -0.0222;
			k0 = 10652;
		} else if (tissus.equals("2113")) {
			k2 = 0.005;
			k1 = -3.4923;
			k0 = 14365;
		} else if (tissus.equals("2116")) {
			k2 = 0.0058;
			k1 = -1.3088;
			k0 = 12870;
		} else if (tissus.equals("1506")) {
			k2 = 0.0052;
			k1 = -3.3356;
			k0 = 14690;
		} else if (tissus.equals("7628")) {
			k2 = 0.0051;
			k1 = -3.7052;
			k0 = 15314;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public double module_tissus_y(double temperature, String tissus) {
		double k0 = 0;
		double k1 = 0;
		double k2 = 0;
		double k3 = 0;
		double k4 = 0;
		if (tissus.equals("1037")) {
			k2 = 0.0065;
			k1 = 2.364;
			k0 = 7879;
		} else if (tissus.equals("106")) {
			k2 = 0.0065;
			k1 = 2.2178;
			k0 = 8075;
		} else if (tissus.equals("1078")) {
			k2 = 0.0063;
			k1 = 1.1593;
			k0 = 9506;
		} else if (tissus.equals("1080")) {
			k2 = 0.0065;
			k1 = 1.6718;
			k0 = 9222;
		} else if (tissus.equals("2113")) {
			k2 = 0.007;
			k1 = 2.6092;
			k0 = 9172;
		} else if (tissus.equals("2116")) {
			k2 = 0.0059;
			k1 = -0.09809;
			k0 = 12594;
		} else if (tissus.equals("1506")) {
			k2 = 0.0065;
			k1 = 0.7149;
			k0 = 11290;
		} else if (tissus.equals("7628")) {
			k2 = 0.0064;
			k1 = 0.0224;
			k0 = 12216;
		}
		double liste[] = {
				k0, k1, k2, k3, k4
		};
		return super.calcul(liste, temperature);
	}

	public String toString() {
		return Ressources.RESINEPCL370ISOLA;
	}

}
