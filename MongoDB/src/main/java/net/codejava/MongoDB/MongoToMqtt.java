package net.codejava.MongoDB;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

public class MongoToMqtt {

	private Mongo mongo;
	private Mysql ms;
	private final String serverURI = "tcp://broker.mqtt-dashboard.com:1883";
	private MqttClient client;
	private MqttMessage message;
	private String topic = "g26";
	private ArrayList<String> msgEnviadas;
	private ArrayList<MongoCollection<Document>> collections;
	private Map<String, String> mapMostRecentDate = new HashMap<String, String>();

	public MongoToMqtt() {
		this.mongo = new Mongo();
		this.ms = new Mysql();
		this.msgEnviadas = new ArrayList<String>();
	}

	public void establishConnections() {
		this.ms.connectToMysql();
	}

	public void initClients() {
		this.mongo.initClientReplicas();
		this.mongo.initClientCloud();
		this.collections = mongo.getCollections();
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

	public void sendMessage(String msg) {
		try {
			message = new MqttMessage(msg.getBytes());
			message.setQos(0);
			message.setRetained(false);
			client.publish(topic, message);
			System.out.println("Message sent sucessfully -> " + msg);
		} catch (MqttException e) {
			System.out.println("Error sending message");
		}
	}

	public void sendMsg(FindIterable<Document> list) {
		ArrayList<String> listStr = new ArrayList<String>();
		for (Document doc : list) {
			listStr.add(doc.toJson().toString());
		}

		System.out.println(listStr.size());
		for (String x : listStr) {
			if (this.msgEnviadas.contains(x)) {
			} else {
				sendMessage(x);
				this.msgEnviadas.add(x);

			}
		}
	}

	public String sendMostRecentRecord(MongoCollection<Document> coll) {

		FindIterable<Document> cursor1 = coll.find().sort(new BasicDBObject("Data", -1)).limit(1);
		sendMsg(cursor1);
		for (Document d : cursor1) {
			return d.get("Data").toString();
		}
		return "";

	}

	public String sendRecordGreaterThan(String dataEdited, MongoCollection<Document> coll) {

		Bson filter1 = and(eq("Sensor", coll.getNamespace().getCollectionName()), gt("Data", dataEdited));
		FindIterable<Document> list1 = coll.find(filter1).sort(new BasicDBObject("Data", -1)).limit(1);
		sendMsg(list1);
		for (Document d : list1) {
			return d.get("Data").toString();
		}
		return "";

	}

	public void sendMostRecentData(String dataEdited, MongoCollection<Document> coll) {

		Bson filter1 = and(eq("Sensor", coll.getNamespace().getCollectionName()), gt("Data", dataEdited));
		FindIterable<Document> list1 = coll.find(filter1);
		sendMsg(list1);

	}

	public void sendMedicoes() {

		boolean alreadyExecuted = false;

		while (true) {

			if (!alreadyExecuted) {

				for (MongoCollection<Document> coll : this.collections) {

					String data = this.ms.getMostRecentlyDate(coll.getNamespace().getCollectionName()).toString();
					System.out.println("Data" + data);
					String dataEdited = data.replace(" ", "T").replace(".0", "").concat("Z");
					System.out.println("Data" + dataEdited);

					if (data.equals("1111-11-11 11:11:11.0")) {

						String mostRecentDate = sendMostRecentRecord(coll);
						if (!mostRecentDate.equals("")) {
							mapMostRecentDate.put(coll.getNamespace().getCollectionName(), mostRecentDate);
						}
					}

					else {

						String mostRecentDate = sendRecordGreaterThan(dataEdited, coll);
						if (!mostRecentDate.equals("")) {
							mapMostRecentDate.put(coll.getNamespace().getCollectionName(), mostRecentDate);
						}
					}

				}

				alreadyExecuted = true;
			}

			else {

				for (MongoCollection<Document> coll : this.collections) {

					String date = mapMostRecentDate.get(coll.getNamespace().getCollectionName());
					if (date != null) {
						sendMostRecentData(date, coll);
					}

					else {

						String mostRecentDate = sendMostRecentRecord(coll);
						if (!mostRecentDate.equals("")) {
							mapMostRecentDate.put(coll.getNamespace().getCollectionName(), mostRecentDate);
						}
					}
				}
			}
		}

	}

	public static void main(String[] args) {

		MongoToMqtt mtm = new MongoToMqtt();
		mtm.establishConnections();
		mtm.initClients();
		mtm.connectMqqtServer();
		mtm.sendMedicoes();

	}

}

//
