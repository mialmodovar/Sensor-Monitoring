package net.codejava.MongoDB;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class InsertDataCloud {

	public static void main(String[] args) {

		MongoClient clientMongoCloud = MongoClients.create("mongodb://aluno:aluno@194.210.86.10:27017");
		MongoDatabase dbCloud = clientMongoCloud.getDatabase("sid2022");
		MongoCollection<Document> medicoes = dbCloud.getCollection("medicoesG26");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("T1", 0.0);
		map.put("T2", 0.0);
		map.put("H1", 0.0);
		map.put("H2", 0.0);
		map.put("L1", 0.0);
		map.put("L2", 0.0);

		double a = 3;
//
//		String[] teste = { "20", "20.1", "27", "35", "35.2" };

		while (true) {

			sdf.setTimeZone(TimeZone.getDefault());

//			String value = String.valueOf(a);

			for (Map.Entry<String, Double> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = Double.toString(entry.getValue());
				Document document = new Document();
				document.put("Zona", "Z".concat(key.substring(1, 2)));
				document.put("Sensor", key);
				document.put("Data", sdf.format(new Date()));
				document.put("Medicao", value);
				medicoes.insertOne(document);
				map.put(key, entry.getValue() + 0.5);
			}

//			for (int i = 0; i < teste.length; i++) {
//				sdf.setTimeZone(TimeZone.getDefault());
//				Document document = new Document();
//				document.put("Zona", "Z1");
//				document.put("Sensor", "T1");
//				document.put("Data", sdf.format(new Date()));
//				document.put("Medicao", teste[i]);
//				medicoes.insertOne(document);

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
