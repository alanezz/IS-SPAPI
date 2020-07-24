package main;

import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFSyntax;

public class LoadTDB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String tdb_folder = "C:\\Users\\Fernando\\Desktop\\paper\\data";
		String data_source = "C:\\Users\\Fernando\\Desktop\\paper\\wikidata-filtered.nt";
		Dataset dataset = TDBFactory.createDataset(tdb_folder);
		Model tdb = dataset.getDefaultModel();
		FileManager.get().readModel(tdb, data_source, "NT");
		dataset.close();
				
	}

}
