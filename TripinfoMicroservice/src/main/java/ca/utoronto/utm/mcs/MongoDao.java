package ca.utoronto.utm.mcs;

import com.mongodb.client.*;
import com.mongodb.client.MongoCollection;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

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

	// *** implement database operations here *** //

}
