package net.codejava.MongoDB;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public class HelperMigration implements Runnable {

	private Mongo mongo;
	private MongoCollection<Document> collection;

	public HelperMigration(Mongo m, MongoCollection<Document> collection) {
		this.mongo = m;
		this.collection = collection;
	}

	public void run() {
		mongo.updateMongoLocal(collection);
	}

}
