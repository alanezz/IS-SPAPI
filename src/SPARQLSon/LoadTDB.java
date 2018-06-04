package SPARQLSon;

public class LoadTDB {

	public static void main(String[] args) {

		String TDBdirectory = "/Users/adriansotosuarez/Desktop/miniYago";
		DatabaseWrapper dbw = new DatabaseWrapper(TDBdirectory);
		dbw.createDataset("/Users/adriansotosuarez/Desktop/trekking2.ttl", "TTL");		
	}

}
