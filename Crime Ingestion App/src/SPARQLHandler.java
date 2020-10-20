import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.stanford.nlp.pipeline.Annotation;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;

public class SPARQLHandler {

	public SPARQLHandler() {
		
	}
	
	private String removeStopword(String original) {
		List<String> allWords = new ArrayList<>(Arrays.asList(original.toLowerCase().split(" ")));
		
		List <String> stopwords = new ArrayList <String> ();
		allWords.removeAll(stopwords);
		String result = String.join(" ", allWords);
		
		return result;
	}

	public void findGeomWithSPARQL() throws Exception, IOException, ParseException {
		
		ArrayList<ArrayList<String>> query_results = new ArrayList<ArrayList<String>>(50000);
		
		System.out.println("SPARQL query...");
		Query sparql = QueryFactory.create("Prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>" + 
				"Prefix ogc: <http://www.opengis.net/ont/geosparql#>" + 
				"Prefix geom: <http://geovocab.org/geometry#>" + 
				"Prefix lgdo: <http://linkedgeodata.org/ontology/>" + 
				"Prefix bif: <bif:>" +
				"SELECT ?s ?g ?o1 ?o2 ?o3 ?o4 ?o5 ?o6 ?o7 ?o8 ?o9 ?o10 ?o11 from <http://linkedgeodata.org> where {" + 
				"	{" + 
				"		SELECT ?s ?g" + 
				"		WHERE {" + 
				"			?s a <http://linkedgeodata.org/meta/Node> ;" + 
				"			geom:geometry [" + 
				"			  ogc:asWKT ?g" + 
				"			] ." + 
				"			Filter(bif:st_intersects (?g, bif:st_geomFromText(\"POLYGON((10.5444 44.9613, 11.3864 44.9613, 11.3864 44.0542, 10.544 44.0542, 10.5444 44.9613))\"))) ." + 
				"		}" + 
				"	}" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/historicalName> ?o1 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/ref> ?o2 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/service> ?o3 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/note> ?o4 . }" + 
				"	OPTIONAL { ?s <http://www.w3.org/2000/01/rdf-schema#label> ?o5 . }" + 
				"	OPTIONAL { ?s <http://www.w3.org/2002/07/owl#sameAs> ?ox ." + 
				"                   ?ox <http://www.w3.org/2000/01/rdf-schema#label> ?o6 . }" + 
				"	OPTIONAL { ?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#comment> ?o7 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/information> ?o8 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/addr%3Acity> ?o9 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/is_in%3Acountry> ?o10 . }" + 
				"	OPTIONAL { ?s <http://linkedgeodata.org/ontology/isIn> ?o11 . }" + 
				"}");

		QueryExecution vqe = QueryExecutionFactory.sparqlService ("http://www.linkedgeodata.org/sparql", sparql);
		org.apache.jena.query.ResultSet results =  vqe.execSelect();

		System.out.println("SPARQL query... DONE");
		while (results.hasNext()) {
			QuerySolution rs1 = ((org.apache.jena.query.ResultSet) results).nextSolution();
		    String s = rs1.get("s").toString();
		    String geom = rs1.get("g").toString().replace("^^http://www.openlinksw.com/schemas/virtrdf#Geometry", "");
		    RDFNode o1 = rs1.get("o1");
		    RDFNode o2 = rs1.get("o2");
		    RDFNode o3 = rs1.get("o3");
		    RDFNode o4 = rs1.get("o4");
		    RDFNode o5 = rs1.get("o5");
		    RDFNode o6 = rs1.get("o6");
		    RDFNode o7 = rs1.get("o7");
		    RDFNode o8 = rs1.get("o8");
		    RDFNode o9 = rs1.get("o9");
		    RDFNode o10 = rs1.get("o10");
		    RDFNode o11 = rs1.get("o11");
		    String concatenation = o1 + " " + o2 + " " + o3 + " " + o4 + " " + o5 + " " + o6 + " " + o7 + " " + o8;
		    String country = o9 + " " + o10 + " " + o11;
		    country = country.replace("null", "").trim();
		    concatenation = concatenation.replace("null", "").trim();
		    
		    ArrayList<String> res = new ArrayList<String>(); 
	        res.add(s);
	        res.add(geom);
	        res.add(concatenation);
	        res.add(country);
	        query_results.add(res);
		}
		
		//tint ner
    	TintPipeline pipeline = new TintPipeline();
    	pipeline.loadDefaultProperties();
    	pipeline.load();
		
    	LongestSubstringOfTwoString lsots = new LongestSubstringOfTwoString();
    	
    	//Configuring connection to database
		Object obj_config = new JSONParser().parse(new FileReader("configuration/config.json"));
    	JSONObject jo_config = (JSONObject) obj_config;
    	JSONObject db = (JSONObject) jo_config.get("database");
    	String username = (String) db.get("username");
    	String password = (String) db.get("password");
    	
    	Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost/postgres?currentSchema=crime_news&user="+username+"&password="+password;
		Connection conn = DriverManager.getConnection(url);
    	Statement st = conn.createStatement();
    	
    	//query to select all rows without geom or with incorrect geom
    	ResultSet rs = st.executeQuery("SELECT concat(title, description, text) as textual, municipality, area, url "
    									+ "FROM crime_news.news "
    									+ "WHERE (geom is null "
    									+ "OR ((ST_X(geom)< 10.5444 "
    									+ "or ST_X(geom)> 11.3864) " 
    									+ "or (ST_Y(geom)< 44.0542 "
    									+ "or ST_Y(geom)> 44.9613))) ");
    	
    	int new_geom=0;
    	int new_org=0;
    	int cont_news=0;
    	int not_found=0;
		int cont_lod=0;

    	String text="";
		String municipality="";
    	String area="";
    	String news_url="";
    	
    	while (rs.next()) {
    		cont_news++;
    		System.out.println("#news: " + cont_news);
    		System.out.println("#new_geom: " + new_geom);
    		System.out.println("#new_org: " + new_org);
    		System.out.println("#cont_lod: " + cont_lod);

    		text=rs.getString(1);
    	    municipality=rs.getString(2);
    	    area=rs.getString(3);
    		news_url=rs.getString(4);
    	    
    	    if(municipality.equals("")) {
    	    	List < String > municipalities = new ArrayList < String > ();
    			municipalities = Utils.load_list("municipalityList_ModenaToday.txt");
//    	    	List<String> municipalities = Arrays.asList("Bastiglia", "Bomporto", "Campogalliano", "Camposanto", "Carpi", "Castelfranco Emilia", "Castelnuovo Rangone", "Castelvetro di Modena", 
//    	    			"Cavezzo", "Concordia sulla Secchia", "Fanano", "Finale Emilia", "Fiorano Modenese", "Fiumalbo", "Formigine", 
//    	    			"Frassinoro", "Guiglia", "Lama Mocogno", "Maranello", "Marano sul Panaro", 
//    	    			"Medolla", "Mirandola", "Montecreto", "Montefiorino", "Montese", "Nonantola", 
//    	    			"Novi di Modena", "Palagano", "Pavullo nel Frignano", "Pievepelago", "Polinago", "Prignano sulla Secchia", 
//    	    			"Ravarino", "Riolunato", "San Cesario sul Panaro", "San Felice sul Panaro", "San Possidonio", 
//    	    			"San Prospero", "Sassuolo", "Savignano sul Panaro", "Serramazzoni", "Sestola", "Soliera", "Spilamberto", "Vignola", "Zocca");
    	    	for (String m: municipalities) {
    	    		if(text.contains(m)) {
    	    			municipality=m;
    	    		}
    	    	}
    	    }
    	    
    	    if(municipality.length()<2)
				municipality="Modena";
			
    	    System.out.println("Text: " + text);
    	    System.out.println("Municipality: " + municipality);
    	    System.out.println("Area: " + area);

        	File file = new File("new_JSON.json");
        	OutputStream outputstream = new FileOutputStream(file);
	    	InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
	    	Annotation annotation = pipeline.run(stream, outputstream, TintRunner.OutputFormat.JSON);
	    	outputstream.close();
	    	Object obj = new JSONParser().parse(new FileReader("new_JSON.json"));
	    	JSONObject jo = (JSONObject) obj;
	    	JSONArray ja = (JSONArray) jo.get("sentences");
	    	Iterator itr = ja.iterator();
	    	
	    	List<String> pos = new ArrayList<String>();
	    	List<String> org = new ArrayList<String>();
	    	String word = "";
	    	
	    	while(itr.hasNext()) {
	    		Iterator<Map.Entry> itr1 = ((Map) itr.next()).entrySet().iterator();
		    	while(itr1.hasNext()) {
		    		Map.Entry pair = itr1.next();
		    		if(pair.getKey().equals("tokens")) {
		    			JSONArray tokens_array = (JSONArray) pair.getValue();
		    			Iterator itr2 = tokens_array.iterator();
		    			
		    			while (itr2.hasNext()) {
		    				Iterator<Map.Entry> itr3 = ((Map) itr2.next()).entrySet().iterator();
		    				String ner = new String();
		    				while (itr3.hasNext()) {
		    					Map.Entry pair_tokens = itr3.next();
	                            if(pair_tokens.getKey().equals("ner"))
	                            	ner = pair_tokens.getValue().toString();
	                            if(pair_tokens.getKey().equals("word"))
	                            	word =  pair_tokens.getValue().toString();
	                        }

	                        if(ner.equals("LOC"))
	                        	pos.add(word.toLowerCase());
	                        else
	                        	pos.add(" ");
	                        
	                        if(ner.equals("ORG"))
	                    		org.add(word.toLowerCase());
	                        else
	                        	org.add(" ");
		                }
		            }
		    	}
	    	}
	    	
			int cont=0;
			String location="";
			String street ="";
			String lat="";
			String lon="";
	    	
			//location + municipality
			for(int k=0; k<pos.size(); k++) {
				if(pos.get(k).equals(" "))
					continue;
				location=pos.get(k)+" ";
				int j=k+1;
				for( ; j<pos.size() && pos.get(j)!=" "; j++) {
					location += pos.get(j)+" ";
				}
				cont=j;
	    		
				street = location.trim() + " " + municipality.trim();
				System.out.println("Searching for street: " + street);
				
				if(street.trim() != "") {
					Map<String, Double> coords;
			        coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
			        try {
				        lat = coords.get("lat").toString().trim();
				        lon = coords.get("lon").toString().trim();
			        }
			        catch(java.lang.NullPointerException e){
			        }
				}
	    		
	    		if(!lat.equals("") && !lon.equals("")) {
	    			System.out.println("latitude :" + lat);
			        System.out.println("longitude:" + lon);
			        new_geom++;
	    			break;
	    		}
	    		else {
	    			//municipality + location
	    			street = municipality.trim() + " " + location.trim();
					System.out.println("Searching for street: " + street);
					
					if(street.trim() != "" && street.trim() != " ") {
						Map<String, Double> coords;
				        coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
				        try {
					        lat = coords.get("lat").toString().trim();
					        lon = coords.get("lon").toString().trim();
				        }
				        catch(java.lang.NullPointerException e){
				        }
					}
					
	    			if(!lat.equals("") && !lon.equals("")) {
	    				System.out.println("latitude :" + lat);
				        System.out.println("longitude:" + lon);
				        new_geom++;
		    			break;
	    			}	
	    		}
	    		if((cont+1)<pos.size())
	    			k=cont+1;
	    		else
	    			break;
		    }

			//organization + municipality
			if(lat.equals("") && lon.equals("")) {
				String organization = "";
				cont=0;
				for(int k=0; k<org.size(); k++) {
					if(org.get(k).equals(" "))
						continue;
					organization=org.get(k)+" ";
					int j=k+1;
					for( ; j<org.size() && org.get(j) != " " ; j++) {
						organization += org.get(j)+" ";
					}
					cont=j;
					
					street = organization.trim() + " " + municipality.trim();
					System.out.println("Searching for organization: " + street);
					
					if(street.trim() != "" && street.trim() != " "){
						Map<String, Double> coords;
				        coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
				        try {
					        lat = coords.get("lat").toString().trim();
					        lon = coords.get("lon").toString().trim();
				        }
				        catch(java.lang.NullPointerException e){
				        }
					}
		    		
		    		if(!lat.equals("") && !lon.equals("")) {
		    			System.out.println("latitude :" + lat);
				        System.out.println("longitude:" + lon);
				        new_org++;
		    			break;
		    		}
		    		else {
		    			//municipality + organization
		    			street = municipality.trim() + " " + organization.trim();
						System.out.println("Searching for organization: " + street);
						
						if(street.trim() != "" && street.trim() != " "){
							Map<String, Double> coords;
					        coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
					        try {
						        lat = coords.get("lat").toString().trim();
						        lon = coords.get("lon").toString().trim();
					        }
					        catch(java.lang.NullPointerException e){
					        }
						}
						
		    			if(!lat.equals("") && !lon.equals("")) {
		    				System.out.println("latitude :" + lat);
					        System.out.println("longitude:" + lon);
					        new_org++;
			    			break;
		    			}
			    	}
		    		if((cont+1)<org.size())
		    			k=cont+1;
		    		else
		    			break;
			    }
			}
			
			//le loc del NER non sono state utili per geolocalizzare il crimine
			cont=0;
			if(lat.equals("") && lon.equals("")) {
	    		System.out.println("Searching for text: " + text);
				int max_lenght=0;
				String max_node="";
				String max_geom="";
				String max_concatenation="";
				
				String match = "";
				for (int i = 0; i < query_results.size(); i++) {
					String node = query_results.get(i).get(0);
					String geom = query_results.get(i).get(1);
					String concatenation = query_results.get(i).get(2);
					String country = query_results.get(i).get(3);
				    
					String text_withoutSW = removeStopword(text);
					String concatenation_withoutSW = removeStopword(concatenation);
					
				    if(country.equals("") || municipality.equals("") || (!country.equals("") && !municipality.equals("") && country.contains(municipality))) {
				    	Pair<Integer, String> p = lsots.LCSubStr(text_withoutSW.replaceAll("[.:,;-_+*]", ""), concatenation_withoutSW, text_withoutSW.replaceAll("[.:,;-_+*]", "").length(), concatenation_withoutSW.length());
					    
					    if(p.getKey()>max_lenght && p.getKey()>=10) {
					    	max_lenght=p.getKey();
					    	max_concatenation = concatenation;
					    	max_node = node;
					    	max_geom = geom;
					    	match = p.getValue();
					    	String coordinates[] = geom.replace("POINT(", "").replace(")", "").split(" ");
					    	lon = coordinates[0];
					    	lat = coordinates[1];
					    }
				    }
				}
				
				if(!lat.equals("") && !lon.equals("")) {
					cont_lod++;
				}
				
				System.out.println("Match lenght: " + max_lenght + " (" + match + ")");
	    		System.out.println("Found node " + max_node + " (" + max_concatenation + " " + max_geom + ")");
			}
			
			if(lat.equals("") && lon.equals("")) {
				not_found++;
			}
			else {
				String query = "update crime_news.news set geom = ST_GeomFromText(?, 4326) where url = ?";

                int affectedrows = 0;
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, "POINT("+lon+" "+lat+")");
                pstmt.setString(2, news_url);
                affectedrows = pstmt.executeUpdate();
                if(affectedrows!=1) {
                	System.out.println("ERROR: update do nothing");
                }
                else
                	System.out.println("Updated: " + affectedrows + " row");
			}
		}


    	System.out.println("#not_found: " + not_found);
    	System.out.println("#geom: " + new_geom);
    	System.out.println("#org: " + new_org);
    	System.out.println("#news: " + cont_news);
    	System.out.println("#lod: " + cont_lod);
    	
	}

}
