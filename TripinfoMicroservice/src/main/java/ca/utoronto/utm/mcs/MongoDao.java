package ca.utoronto.utm.mcs;

import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import com.mongodb.client.MongoCollection;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	public MongoDao() {
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String username = dotenv.get("MONGO_INITDB_ROOT_USERNAME");
		String password = dotenv.get("MONGO_INITDB_ROOT_PASSWORD");
		String uriDb = String.format("mongodb://%s:%s@%s:27017", username, password, addr);
		String dbName = dotenv.get("MONGO_INITDB_DATABASE");

		MongoClient mongoClient = MongoClients.create(uriDb);
		MongoDatabase database = mongoClient.getDatabase(dbName);
		this.collection = database.getCollection("trips");
	}

	public FindIterable<Document> getPassengerTrips(String passenger){
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("passenger", passenger);
			return this.collection.find(query);
		} catch (Exception e) {
			System.out.println("Error occurred");
		}
		return null;
	}

	public FindIterable<Document> getDriverTrips(String driver){
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("driver", driver);
			return this.collection.find(query);
		} catch (Exception e) {
			System.out.println("Error occurred");
		}
		return null;
	}

	public FindIterable<Document> getTrip(String trip){
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(trip));
			return this.collection.find(query);
		} catch (Exception e) {
			System.out.println("Error occurred");
		}
		return null;
	}

	public ObjectId postTrip(String driver, String passenger, int startTime){
		Document doc = new Document();
		doc.put("driver", driver);
		doc.put("passenger", passenger);
		doc.put("startTime", startTime);

		try {
			this.collection.insertOne(doc);
			return doc.getObjectId("_id");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean updateTrip(String _id, int distance, int endTime, String timeElapsed, double discount, double totalCost, double driverPayout){
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", new ObjectId(_id));

			BasicDBObject updatedDoc = new BasicDBObject();
			updatedDoc.put("distance", distance);
			updatedDoc.put("endTime", endTime);
			updatedDoc.put("timeElapsed", timeElapsed);
			updatedDoc.put("discount", discount);
			updatedDoc.put("totalCost", totalCost);
			updatedDoc.put("driverPayout", driverPayout);

			BasicDBObject updateObject = new BasicDBObject();
			updateObject.put("$set", updatedDoc);

			this.collection.updateOne(query, updateObject);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error occurred");
		}
		return false;
	}

}
