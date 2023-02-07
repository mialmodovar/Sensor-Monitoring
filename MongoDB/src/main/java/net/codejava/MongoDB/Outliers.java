package net.codejava.MongoDB;

import java.util.HashMap;
import java.util.Map;

public class Outliers {

	private String IDSensor;
	private Map<String, Double> map;

	public Outliers(String IDSensor) {
		this.IDSensor = IDSensor;
		this.map = new HashMap<String, Double>();

	}

	public String getIDSensor() {
		return this.IDSensor;
	}

	public Map<String, Double> getMap() {
		return this.map;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
