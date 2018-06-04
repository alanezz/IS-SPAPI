package SPARQLSon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class EasyQuery {

	public static void main(String[] args) throws IOException {

		String TDBdirectory = "/put/your/directory/here";

		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				   + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				   + "PREFIX bsbm: <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/vocabulary/> "
				   + "PREFIX ex: <http://example.org/> "
				   + "PREFIX rev: <http://purl.org/stuff/rev#> "
				   + "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				   + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				   + "SELECT * WHERE {?x rdfs:label ?l . "
				   + "?x rdf:type <http://www4.wiwiss.fu-berlin.de/bizer/bsbm/v01/instances/ProductType1> . "
				   + "?r bsbm:reviewFor ?x . ?r ex:id ?id . "
				   + "?r rev:reviewer ?reviewer . ?reviewer foaf:name ?revName . "
				   + "?r dc:title ?revTitle . ?r rev:text ?revText}";


		long start = System.nanoTime();

		DatabaseWrapper dbw = new DatabaseWrapper(TDBdirectory);
		ResultSet rs = dbw.execQuery(queryString);

		/*File file = new File("/Users/adriansotosuarez/Desktop/reviews.json");
		OutputStream fos = new FileOutputStream(file);
		ResultSetFormatter.outputAsJSON(fos, rs);

		fos.close();*/

		int mappingCount = 0;
		for ( ; rs.hasNext() ; ) {
			QuerySolution rb = rs.nextSolution() ;
			mappingCount++;
		}

		System.out.println("Triples: " + mappingCount);

		dbw.qexec.close();
		dbw.dataset.close();

		long elapsedTime = System.nanoTime() - start;

		System.out.println(elapsedTime / 1000000000.0);

	}

}
