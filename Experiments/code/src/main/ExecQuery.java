package main;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.resultset.rw.ResultSetReaderJSON;
import org.apache.jena.riot.resultset.rw.ResultSetWriterJSON;
import org.apache.jena.sparql.util.ResultSetUtils;
import org.apache.jena.tdb.TDBFactory;

public class ExecQuery {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub

		// Path to the TDBFolder of the Wikidata Dataset
		String tdbFolder = "Path/To/TDB/Folder";
		Dataset dataset = TDBFactory.createDataset(tdbFolder);
		Model tdb = dataset.getDefaultModel();

		// Path to the file to execute
		// TX for trees, TIX for inverted trees, JX for joins
		// PX for paths, TrX for triangles and SX for squares
		File file = new File("Path/To/File"); 
	    Scanner sc = new Scanner(file); 
	  
	    while (sc.hasNextLine()) {
	      String queryString = sc.nextLine();
			// Parse
	      	long start = System.currentTimeMillis();
			Query query = QueryFactory.create(queryString);
			QueryExecution qe = QueryExecutionFactory.create(query, tdb);
			ResultSet rs = qe.execSelect();
			long end = System.currentTimeMillis();
			HashSet<String> aux = new HashSet<>();
					
			int count = 0;
			while(rs.hasNext()) {
				QuerySolution qs = rs.next();

				// Variable given to the API
				String value = qs.get("?X").toString();
				aux.add(value);
				count++;
			}

			// Print stats
			System.out.println(count);
			System.out.println(aux.size());
			NumberFormat formatter = new DecimalFormat("#0.00000");
			System.out.print(formatter.format((end - start) / 1000d));
			System.out.println(count);
			qe.close();
	    } 
		
		tdb.close();
				

	}

}
