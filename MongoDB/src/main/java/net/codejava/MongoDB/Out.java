package net.codejava.MongoDB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Out {

	public List<Double> getOutliers(List<Double> input) {

		List<Double> outliers = new ArrayList<Double>();

		double Q1 = calcQuartil("Q1", input);
		double Q3 = calcQuartil("Q3", input);
		double deltaQ = Q3 - Q1;
		double Linf = Q1 - 1.5 * deltaQ;
		double Lsup = Q3 + 1.5 * deltaQ;

		for (int i = 0; i < input.size(); i++) {
			if (input.get(i) < Linf || input.get(i) > Lsup)
				outliers.add(input.get(i));
		}
		return outliers;
	}

	public double calcQuartil(String quartil, List<Double> input) {

		double kQ = 0;
		int parteInteiraQ = 0;
		double parteDecimalQ = 0;
		int size = input.size();

		if (quartil.equals("Q1")) {
			if (input.size() % 2 == 0) {

				kQ = (double) (size + 2) / 4;
				parteInteiraQ = (int) kQ;
				parteDecimalQ = (double) kQ - parteInteiraQ;

			}

			else {

				kQ = (double) (size + 1) / 4;
				parteInteiraQ = (int) kQ;
				parteDecimalQ = (double) kQ - parteInteiraQ;
			}

		}

		else if (quartil.equals("Q3")) {
			if (input.size() % 2 == 0) {

				kQ = (double) ((3 * size) + 2) / 4;
				parteInteiraQ = (int) kQ;
				parteDecimalQ = (double) kQ - parteInteiraQ;

			}

			else {

				kQ = (double) 3 * (size + 1) / 4;
				parteInteiraQ = (int) kQ;
				parteDecimalQ = (double) kQ - parteInteiraQ;
			}
		}

		if (parteDecimalQ == 0) {
			return input.get(parteInteiraQ - 1);
		}

		return input.get(parteInteiraQ - 1) + parteDecimalQ * (input.get(parteInteiraQ) - input.get(parteInteiraQ - 1));
	}

	public static void main(String[] args) {

		Out o = new Out();

		List<Double> data = new ArrayList<Double>();
		data.add((double) 10);
		data.add((double) 50);
		data.add((double) 10.5);

		

		Collections.sort(data);
		o.getOutliers(data).forEach(x -> System.out.println(x));

	}
}
