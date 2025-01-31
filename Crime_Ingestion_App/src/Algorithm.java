import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.ParseException;

import org.postgresql.util.PSQLException;

public class Algorithm {
	
	
	public Algorithm() {
		
	}

	public void addAlgorithm(Connection conn) throws FileNotFoundException, IOException, SQLException, ParseException, Exception {
		
		//Algoritmo con descrizione	
		PreparedStatement insertAlgorithm = conn.prepareStatement("INSERT INTO crime_news.algorithm (id, name, numofdays, configurations) VALUES ( ?, ?, ?,?)");
		insertAlgorithm.setInt(1, 1);
		insertAlgorithm.setString(2, "Cosine similarity");
		insertAlgorithm.setInt(3, 3);
		insertAlgorithm.setString(4,"k-shingle size: title k = 2, description k = 2, text k = 3 | weights: title = 1, description = 1.25, text = 4 | score: same municipality +0.025, same day +0.03, one day of difference +0.015 | threshold:0.7");
		try {
			insertAlgorithm.executeUpdate();
		} catch (PSQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		//Algoritmo senza descrizione
		PreparedStatement insertAlgorithm2 = conn.prepareStatement("INSERT INTO crime_news.algorithm (id, name, numofdays, configurations) VALUES ( ?, ?, ?,?)");
		insertAlgorithm2.setInt(1, 2);
		insertAlgorithm2.setString(2, "Cosine similarity");
		insertAlgorithm2.setInt(3, 3);
		insertAlgorithm2.setString(4,"k-shingle size: title k = 2, text k = 3 | weights: title = 1, text = 4 | score: same municipality +0.025, same day +0.03, one day of difference +0.015 | threshold:0.7");
		try {
			insertAlgorithm2.executeUpdate();
		} catch (PSQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
}
