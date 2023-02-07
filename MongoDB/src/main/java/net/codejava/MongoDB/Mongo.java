package net.codejava.MongoDB;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import java.io.IOException;
import java.util.ArrayList;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;

public class Mongo {

	private MongoClient clientMongoLocal;
	private MongoClient clientMongoCloud;
	private MongoDatabase dbCloud;
	private MongoDatabase dbLocal;
	private final String localDatabaseName = "MongoSensores";
	private final String cloudDatabaseName = "sid2022";
	private ArrayList<MongoCollection<Document>> collections;
	private final String cloudCollectionName = "medicoes2022";
	final static String[] nameCollections = new String[] { "T1", "T2", "H1", "H2", "L1", "L2" };

	public Mongo() {

	}

	public void initClientReplicas() {
		this.clientMongoLocal = MongoClients
				.create("mongodb://localhost:27019,localhost:25019,localhost:23019/?replicaSet=replicapisid");
		this.dbLocal = clientMongoLocal.getDatabase(localDatabaseName);
		addCollections();
	}

	public void initClientCloud() {
		this.clientMongoCloud = MongoClients.create("mongodb://aluno:aluno@194.210.86.10:27017");
		this.dbCloud = clientMongoCloud.getDatabase(cloudDatabaseName);

	}

	public void addCollections() {
		this.collections = new ArrayList<MongoCollection<Document>>();
		for (int i = 0; i < nameCollections.length; i++) {
			collections.add(this.dbLocal.getCollection(nameCollections[i]));
		}
	}

	public ArrayList<MongoCollection<Document>> getCollections() {
		return this.collections;
	}

	public MongoDatabase getLocalDB() {
		return this.dbLocal;
	}

	public void initReplicas() {
		try {
			String[] command = { "cmd.exe", "/C", "C:\\Users\\35196\\Documents\\initReplicas.bat" };
			Process p = Runtime.getRuntime().exec(command);
			Thread.sleep(1000);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Bson getFilter(FindIterable<Document> list, String sensor) {

		Bson filter = null;
		for (Document doc : list) {
			return filter = and(eq("Sensor", sensor), gt("Data", doc.get("Data")));
		}
		return filter;
	}

	public void updateEmptyCollections(MongoCollection<Document> localCollection, MongoCollection<Document> medicoes) {

		// for (MongoCollection<Document> coll : this.collections) {
		if (localCollection.countDocuments() == 0) {
			FindIterable<Document> lastRecord = medicoes
					.find(eq("Sensor", localCollection.getNamespace().getCollectionName()))
					.sort(new BasicDBObject("Data", -1)).limit(1);
			lastRecord.forEach(doc -> localCollection.insertOne(doc));
			// }
		}
	}

	public void insertMostRecentRecord(MongoCollection<Document> localCollection, MongoCollection<Document> medicoes) {

		// for (MongoCollection<Document> coll : this.collections) {
		FindIterable<Document> cursor1 = localCollection.find().sort(new BasicDBObject("Data", -1)).limit(1);
		Bson filter1 = getFilter(cursor1, localCollection.getNamespace().getCollectionName());
		if (filter1 != null) {
			FindIterable<Document> list1 = medicoes.find(filter1).sort(new BasicDBObject("Data", -1)).limit(1);
			list1.forEach(doc -> localCollection.insertOne(doc));
			// }
		}
	}

	public void insertData(MongoCollection<Document> localCollection, MongoCollection<Document> medicoes) {

		// for (MongoCollection<Document> coll : this.collections) {
		FindIterable<Document> cursor1 = localCollection.find().sort(new BasicDBObject("Data", -1)).limit(1);
		Bson filter1 = getFilter(cursor1, localCollection.getNamespace().getCollectionName());
		if (filter1 != null) {
			FindIterable<Document> list1 = medicoes.find(filter1);
			list1.forEach(doc -> localCollection.insertOne(doc));
			// }
		}

	}

	public String[] getNameCollections() {
		return this.nameCollections;
	}

	public void updateMongoLocal(MongoCollection<Document> localCollection) {

		MongoCollection<Document> medicoes = this.dbCloud.getCollection(cloudCollectionName);
//		String resultCreateIndex = medicoes.createIndex((Indexes.ascending("Data")));
//		System.out.println(String.format("Index created: %s", resultCreateIndex));

		boolean alreadyExecuted = false;

		while (true) {

			updateEmptyCollections(localCollection, medicoes);

			if (!alreadyExecuted) {

				insertMostRecentRecord(localCollection, medicoes);
				alreadyExecuted = true;

			}

			insertData(localCollection, medicoes);
		}

	}

	public void helperMigration() {
		for (int i = 0; i < this.nameCollections.length; i++) {
			HelperMigration h = new HelperMigration(this, this.dbLocal.getCollection(nameCollections[i]));
			Thread t = new Thread(h); // Using the constructor Thread(Runnable r)
			t.start();
		}
	}

	// Main
	public static void main(String[] args) {

		Mongo mtm = new Mongo();
		mtm.initReplicas();
		mtm.initClientReplicas();
		mtm.initClientCloud();
		mtm.helperMigration();
	}
}

//                                       NÃO APAGAR
//      FindIterable<Document> cursor1 = zona1.find().sort(new BasicDBObject("Data", -1)).limit(1);
//		MongoCursor<Document> iterator = cursor1.iterator();
//		while (iterator.hasNext()) {
//			System.out.println(iterator.next());
//		}
//		cursor1.forEach(x -> System.out.println(doc));
//      FindIterable<Document> x = medicoes.find().sort(new BasicDBObject("Data", -1)).limit(10);
//      MongoCursor<Document> iterator = x.iterator();
//      while (iterator.hasNext()) {
//	    System.out.println(iterator.next());
//      }
//
