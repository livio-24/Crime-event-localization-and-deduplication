import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import javax.naming.ServiceUnavailableException;
import org.apache.jena.atlas.web.HttpException;
import org.apache.jena.sparql.engine.http.QueryExceptionHTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;



public class Main {

	public Main() {
		
	}

	public static void main(String[] args) throws Exception {
		
		//Configuring database connection
		Object obj = new JSONParser().parse(new FileReader("configuration/config.json"));
    	JSONObject jo = (JSONObject) obj;
    	JSONObject db = (JSONObject) jo.get("database");
    	String username = (String) db.get("username");
    	String password = (String) db.get("password");
    	Integer num_of_pages = ((Long) (jo.get("num_of_pages"))).intValue();
		
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost/crime_news?currentSchema=crime_news&user="+username+"&password="+password;
		Connection conn = DriverManager.getConnection(url);
		
		NewsArticles news_articles = new NewsArticles();
		
		System.out.println("ModenaToday");
		JSONObject obj_MT = (JSONObject) new JSONParser().parse(new FileReader("configuration/modenatoday.json"));
		JSONArray ja_MT = (JSONArray) obj_MT.get("urls");
		Iterator<JSONObject> iterator_MT = ja_MT.iterator();
        while(iterator_MT.hasNext()) {
        	JSONObject obj_iterator = (JSONObject) (iterator_MT.next());
        	String url_news = (String) obj_iterator.get("url");
        	String crime_type = (String) obj_iterator.get("crime-type");
        	System.out.println(url_news);
        	System.out.println(crime_type);
        	int insertedNews = news_articles.addNewsMT(conn, num_of_pages, url_news, crime_type);
        	System.out.println(insertedNews + " news inserted related to " + crime_type + " - ModenaToday");
        }

        System.out.println("Gazzetta di Modena");        
        JSONObject obj_GM = (JSONObject) new JSONParser().parse(new FileReader("configuration/gazzettadimodena.json"));
		JSONArray ja_GM = (JSONArray) obj_GM.get("urls");
		Iterator<JSONObject> iterator_GM = ja_GM.iterator();
        while(iterator_GM.hasNext()) {
        	JSONObject obj_iterator = (JSONObject) (iterator_GM.next());
        	String url_news = (String) obj_iterator.get("url");
        	String crime_type = (String) obj_iterator.get("crime-type");
        	System.out.println(url_news);
        	System.out.println(crime_type);
        	int insertedNews = news_articles.addNewsGM(conn, num_of_pages, url_news, crime_type);
        	System.out.println(insertedNews + " news inserted related to " + crime_type + " - Gazzetta di Modena");
        }
        
    	// Trying to find missing geom with LinkedGeoData
        try {
			SPARQLHandler sparql = new SPARQLHandler();
			sparql.findGeomWithSPARQL();
		} catch (HttpException e1){
			System.out.println("linkedgeodata/sparql service not available");
		} catch (ServiceUnavailableException e2){
			System.out.println("linkedgeodata/sparql service not available");
		} catch (QueryExceptionHTTP e3){
			System.out.println("linkedgeodata/sparql service not available");
		}
		
		// Searching duplicates
		System.out.println("---------------------------------------------------------------------");
		System.out.println("Duplicate detection");
		// Inserting deduplication algorithm into database
		Algorithm alg = new Algorithm();
		alg.addAlgorithm(conn);
		DuplicateDetection DD = new DuplicateDetection();
		int InsertedDuplicateNewsDiffNewspaper = DD.DetectionDiffNewspaper(conn);
		System.out.println("Sono state inserite " +  InsertedDuplicateNewsDiffNewspaper + " notizie duplicate su giornali diversi");
		int InsertedDuplicateNewsSameNewspaper = DD.DetectionSameNewspaper(conn);
		System.out.println("Sono state inserite " +  InsertedDuplicateNewsSameNewspaper + " notizie duplicate su stesso giornale");
		
		
		// Statistiche del DB
		Statistics st = new Statistics();
		int  nn = st.getNewsNumber(conn);
		System.out.println("Numero di news totali presenti nel database: "+nn);
		double  nd = st.getNewsforDay(conn);
			
		int ngm = st.getNewsNumberGM(conn);
		int nmt = st.getNewsNumberMT(conn);
		HashMap <String, Integer> nft = st.getNewsforTag(conn);
		
		System.out.println("Numero medio di news pubblicate al giorno: "+nd);
		System.out.println("Numero di news totali presenti nel database: "+nn);
		System.out.println("Numero di news del giornale La Gazzetta di Modena: "+ngm);
		System.out.println("Numero di news del Giornale ModenaToday: "+nmt);
		System.out.println("Numero di notizie per tag: "+nft);
	}

}
