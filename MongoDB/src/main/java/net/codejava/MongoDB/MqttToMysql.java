package net.codejava.MongoDB;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttToMysql implements MqttCallback {

	private Mysql ms;
	private Mysql msCloud;
	private MqttClient client;
	private String topic = "g26/#";
	private final String serverURI = "tcp://broker.mqtt-dashboard.com:1883";
	static final String ZONA = "(?<=Zona).*?(Z\\d)";
	static final String SENSOR = "(?<=Sensor).*?([T,L,H]\\d)";
	static final String DATA = "(?<=Data).*?([0-9]{4}.[0-9]{2}.[0-9]{2}T[0-9]{2}.[0-9]{2}.[0-9]{2}Z)";
	static final String MEDICAO = "(?<=Medicao).*?(\\d+.\\d+)";

	public MqttToMysql() {
		this.ms = new Mysql();
		this.msCloud = new Mysql();

	}

	public void establishConnections() {
		this.ms.connectToMysql();
		this.msCloud.connectToCloud();
		this.ms.initOutliers();
	}

	public void connectMqqtServer() {
		String clientID = UUID.randomUUID().toString();
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		try {
			client = new MqttClient(serverURI, clientID);
			client.connect(options);
			System.out.println("Client " + clientID + " connected sucessfully");
		} catch (MqttException e) {
			System.out.println("Error connecting to server");
		}
	}

	public String getMatcher(String msg, String pattern) {
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(msg);
		if (m.find()) {
			System.out.println("Found value: " + m.group(1));
			return m.group(1);
		} else {
			System.out.println("NO MATCH");
			return "";
		}
	}

	public void updateDB(String msg, ArrayList<String> limitesSensores) throws Exception {

		String zona = getMatcher(msg, ZONA);
		String sensor = getMatcher(msg, SENSOR);
		String data = getMatcher(msg, DATA);
		String medicao = getMatcher(msg, MEDICAO);

		if (zona != "" && sensor != "" && data != "" && medicao != "") {
			String dataEdited = data.replace("T", " ").replace("Z", "");
			Double valorMedicao = Double.parseDouble(medicao.replace(",", "."));
			Timestamp time = Timestamp.valueOf(dataEdited);

			if (this.ms.isAnomalia(valorMedicao, sensor, limitesSensores)) {
				this.ms.insertAnomalia(sensor, zona, time, valorMedicao, "Erro Fabricante");
			}

			else if (this.ms.isOutlier(valorMedicao, zona, sensor)) {
				this.ms.insertAnomalia(sensor, zona, time, valorMedicao, "Outlier");
			}

			else {
				int idMedicao = this.ms.insertMedicao(sensor, zona, time, valorMedicao);
				ArrayList<String> culturas = this.ms.getCulturasByZona(zona);
				this.ms.insertAlertas(idMedicao, sensor, zona, time, valorMedicao, culturas);

			}
		}

		else {
			// throw new Exception("O Regex falhou");
		}

	}

	public void subscribe() {
		try {
			client.subscribe(topic);
			client.setCallback(this);

		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error trying to subscribe to topic " + topic);
		}

	}

	@Override
	public void connectionLost(Throwable arg0) {
		System.out.println("Connection lost " + arg0);
	}

	@Override
	public void messageArrived(String arg0, MqttMessage msg) throws Exception {

		ArrayList<String> limSensores = this.msCloud.getLimitesSensores();
		String message = msg.toString();
		System.out.println("Message: " + message);
		updateDB(message, limSensores);

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		System.out.println("Message arrived sucessfully");
	}

	public static void main(String[] args) {

		MqttToMysql mtm = new MqttToMysql();
		mtm.establishConnections();
		mtm.connectMqqtServer();
		mtm.subscribe();
	}

}

//
