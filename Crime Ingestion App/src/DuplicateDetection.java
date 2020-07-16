import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.postgresql.util.PSQLException;

import info.debatty.java.stringsimilarity.Cosine;

public class DuplicateDetection {
	
// metodi per il metodo che calcola la distanza in giorni
	
	public Calendar convertDateToCalendar(Date date){
			
		if(date == null)
			return null;
		Calendar cal = null;
		cal = Calendar.getInstance();       
		cal.setTime(date);
		return cal;		
	}
	
	public int DateDistance(Date d1, Date d2) {
	
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.setTime(d1);
		c2.setTime(d2);
		
		long days = (c2.getTime().getTime() - c1.getTime().getTime()) / (24 * 3600 * 1000);
		
		int i = (int) days;
		
		if(i < 0)
			i = i*(-1);
		
		return i;
		
	}

	// metodo che recupera l'id della news
	public static int getId (String url, Connection conn) {
		int id = 0;
		boolean trovato = false;
		try{  
			Statement stmt=conn.createStatement();  
			ResultSet rs=stmt.executeQuery("select * from crime_news.link");  
			while(rs.next()) {
			  if(rs.getString(2).equals(url)) {
				  id = rs.getInt(1);
				  trovato = true;
				  }
			  }
			}catch(Exception e){
				System.out.println(e);
				}
			
			if(trovato = true)
				return id;
			else 
				return 0;
	}

// metodi che eliminano dal database le informazioni
/*
public void DeleteRowNews(String url, Connection conn){
		 try {  
		   
		PreparedStatement st = conn.prepareStatement("DELETE FROM crime_news.news WHERE url = ?");
		st.setString(1,url);
		st.executeUpdate(); 

		 } catch(Exception e){
		     System.out.println(e);
		 }
		}
	
	
		
public void DeleteRowLinks(String url, Connection conn){
			 try {  
			   
			PreparedStatement st = conn.prepareStatement("DELETE FROM crime_news.links WHERE url = ?");
			st.setString(1,url);
			st.executeUpdate(); 

			 }catch(Exception e) {
			     System.out.println(e);
			 }
			}
	
	*/

	// metodi per il calcolo del valore da confrontare con la soglia
	public double valueWithoutDes(double x, double y, double title, double text ) {
		
		return (x*title+y*text) / (x+y);
	}
	
	public double valueWithDes (double x, double y, double z, double title, double description, double text ) {
		
		return (x*title+y*description+z*text) / (x+y+z);
	}

//	public double valueWithoutDes(double x, double y, double title, double text ) {
//			
//			double a = x; 
//			double c = y;
//			
//			double value = (a*title+c*text) / (a+c);
//			return value;
//	}
//	
//	public double valueWithDes (double x, double y, double z, double title, double description, double text ) {
//		
//		double a = x; 
//		double b = y;
//		double c = z;
//		
//		double value = (a*title+b*description+c*text) / (a+b+c);
//		return value;
//	}
	
	
	
// metodo che usavo quando avevo un array di url
	
/*	public boolean isPresent(ArrayList<Duplicate> duplicate, String url) {
		
		boolean ok = false ;
		
		for(int i =0; i< duplicate.size(); i++) {
			if (duplicate.get(i).url1.contains(url)) {
				ok= true;
				break;
			}

		}
		return ok;
	}
	*/
	
	

// metodo per trovare la distanza usando le coordinate
	
	/*public  double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		if ((lat1 == lat2) && (lon1 == lon2)) {
			return 0;
		}
		else {
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;
			if (unit == "K") {
				dist = dist * 1.609344;
			} else if (unit == "N") {
				dist = dist * 0.8684;
			}
			return (dist);
		}
	}

	*/	
	
	public Integer DetectionDiffNewspaper(Connection conn) throws FileNotFoundException, IOException, SQLException, ParseException ,Exception {
		
		PreparedStatement insertDuplicate;
		
		Cosine c = new Cosine();
		Cosine c2 = new Cosine(2);
		Cosine c3 = new Cosine(2);
		
		double x = 1;
		double y = 1.25;
		double z = 4;

		double value;
		double textvalue;
		
		ArrayList<News> news = new ArrayList<News>();
		ArrayList<Duplicate> duplicate = new ArrayList<Duplicate>();

		int id;
		int id2;
		
		int InsertedDuplicateNews = 0;
		
		int cont = 0;
		double threshold = 0.7;
		
		int comparison = 0;

		//carico i dati per il confronto
		try {
			Statement stmnt=conn.createStatement();
			// Integer id, String url, String title, String description, String text, String municipality, Date date, String newspaper, String tag 
			ResultSet rs=stmnt.executeQuery("select url, title, description, text, municipality, date_event, newspaper, tag from crime_news.news");
			while(rs.next()) {
				id = getId(rs.getString(1), conn);
				news.add(new News(id,rs.getString(1), rs.getString(2), rs.getString(3),rs.getString(4),rs.getString(5),rs.getDate(6), rs.getString(7), rs.getString(8)));
			}
			
			System.out.println(news.size());
			
			for(int i = 0; i<news.size(); i++) {
				for(int j = 0; j<news.size(); j++) {
					if((news.get(i).date==null || news.get(j).date==null) || (DateDistance(news.get(i).date,news.get(j).date) < 5 && DateDistance(news.get(i).date,news.get(j).date) > -5)
							&& !news.get(i).newspaper.equals(news.get(j).newspaper)
							&& news.get(i).tag.equals(news.get(j).tag)
							&& news.get(i).id < news.get(j).id){

								if(news.get(i).municipality.toLowerCase().contains(news.get(j).municipality.toLowerCase()) || news.get(i).municipality.equals("")
										|| news.get(i).municipality.equals(" ")
										|| news.get(j).municipality.equals("")
										|| news.get(j).municipality.equals(" ")){
									
									double score = 0;

									if(news.get(i).municipality.toLowerCase().trim().contains(news.get(j).municipality.toLowerCase().trim())
										&& !news.get(i).municipality.toLowerCase().trim().equals("")
									    && !news.get(j).municipality.toLowerCase().trim().equals("")
										&& !news.get(i).municipality.toLowerCase().trim().equals(" ")
										&& !news.get(j).municipality.toLowerCase().trim().equals(" ")) {
										
										score += 0.025;
										
									}
									
									/*	if(distance(Double.parseDouble(news.get(i).lat), Double.parseDouble(news.get(i).lon),
												Double.parseDouble(news.get(j).lat),Double.parseDouble(news.get(j).lon), "K") < 1) {
											
											score = score+0; // sistemare valore
										}
										
										if(distance(Double.parseDouble(news.get(i).lat), Double.parseDouble(news.get(i).lon),
												Double.parseDouble(news.get(j).lat),Double.parseDouble(news.get(j).lon), "K") < 0.5) {
											
											score = score+0; // sistemare valore
										}
										if(distance(Double.parseDouble(news.get(i).lat), Double.parseDouble(news.get(i).lon),
												Double.parseDouble(news.get(j).lat),Double.parseDouble(news.get(j).lon), "K") < 0.2) {
											
											score = score+0; // sistemare valore 
										} */
										
									if(news.get(i).date!=null && news.get(j).date!=null) {
										if(DateDistance(news.get(i).date,news.get(j).date) == 0)
											score += 0.03;
									}
									
									if(news.get(i).date!=null && news.get(j).date!=null) {
										if(DateDistance(news.get(i).date,news.get(j).date) == 1)
											score += 0.015;
									}
						
									//calcolo il valore della similarità normalizzato									
									double text = c.similarity(news.get(i).text ,news.get(j).text);
									double title = c2.similarity(news.get(i).title ,news.get(j).title);
									double description = c3.similarity(news.get(i).description ,news.get(j).description);
									
									if (news.get(i).description.equals("")
									|| news.get(i).description.equals(" ") 
									|| news.get(j).description.equals("")
									|| news.get(j).description.equals(" ")
									|| news.get(j).description == null
									|| news.get(i).description == null)
										value = valueWithoutDes(x,z,title, text);
									else
										value = valueWithDes(x, y, z,title, description, text);
									
									value += score;
									
									comparison++;
									
									// normalizzo il valore di value tenendo conto che score è già normalizzato (compreso tra 0 e 1)
									// il valore massimo di value è 0,025+0,03+1=1,055
									value = value/1.055;
									
									if (value > threshold) {
										
										if(news.get(i).date!=null && news.get(j).date!=null) {
											duplicate.add(new Duplicate(news.get(i).id, news.get(i).url, news.get(j).url, news.get(j).id , value, DateDistance(news.get(i).date,news.get(j).date)));
										}
										else {
											duplicate.add(new Duplicate(news.get(i).id, news.get(i).url, news.get(j).url, news.get(j).id , value, -1));
										}
										
										cont++;
										
										//System.out.println("Inserimento notizie duplicate: "+news.get(i).id+","+news.get(j).id);
										insertDuplicate = conn.prepareStatement("INSERT INTO crime_news.duplicate (id_news1, id_news2, value, algorithm) VALUES (?, ?, ?, ?)");
										insertDuplicate.setInt(1, news.get(i).id);
										insertDuplicate.setInt(2, news.get(j).id);
										insertDuplicate.setDouble(3, value);
										
										if(news.get(i).description.equals("")
										|| news.get(i).description.equals(" ") 
										|| news.get(j).description.equals("")
										|| news.get(j).description.equals(" ")
										|| news.get(j).description == null)
											insertDuplicate.setInt(4, 2);
										else
											insertDuplicate.setInt(4, 1);
										
										int result = -1;
										try {
											result = insertDuplicate.executeUpdate();
											if(result == 1){
												// System.out.println("Inserita nuova coppia di duplicati"+news.get(i).id+" "+news.get(j).id);
												InsertedDuplicateNews++;
											}
										} catch (PSQLException e) {
											// TODO Auto-generated catch block
											//e.printStackTrace();

											System.out.println(e);
										}
										
									}
									
									//duplicate.add(new Duplicate(news.get(i).id, news.get(i).url, news.get(j).url, news.get(j).id , value, DateDistance(news.get(i).date,news.get(j).date)));
									
								}
					}
				}
			}
			
		} catch(Exception e){
			System.out.println(e);
		}

		System.out.println(duplicate);
		System.out.println("Sono stati effettuati " + comparison + " confronti");
		
		return InsertedDuplicateNews;

	}
	
	public Integer DetectionSameNewspaper(Connection conn) throws FileNotFoundException, IOException, SQLException, ParseException ,Exception {
		
		PreparedStatement insertDuplicate;
		
		Cosine c = new Cosine();
		Cosine c2 = new Cosine(2);
		Cosine c3 = new Cosine(2);
		
		double x = 1;
		double y = 1.25;
		double z = 4;

		double value;
		double textvalue;
		
		ArrayList<News> news = new ArrayList<News>();
		ArrayList<Duplicate> duplicate = new ArrayList<Duplicate>();

		int id;
		int id2;
		
		int InsertedDuplicateNews = 0;
		
		int cont = 0;
		double threshold = 0.7;
		
		int comparison = 0;

		//carico i dati per il confronto
		try {
			Statement stmnt=conn.createStatement();
			// Integer id, String url, String title, String description, String text, String municipality, Date date, String newspaper, String tag 
			ResultSet rs=stmnt.executeQuery("select url, title, description, text, municipality, date_event, newspaper, tag from crime_news.news");  
			while(rs.next()) {
				id = getId(rs.getString(1), conn);
				news.add(new News(id,rs.getString(1), rs.getString(2), rs.getString(3),rs.getString(4),rs.getString(5),rs.getDate(6), rs.getString(7), rs.getString(8)));
			}
			
			for(int i = 0; i<news.size(); i++) {
				for(int j = 0; j<news.size(); j++) {
					if((news.get(i).date==null || news.get(j).date==null) || (DateDistance(news.get(i).date,news.get(j).date) < 5 && DateDistance(news.get(i).date,news.get(j).date) > -5)
							//&& news.get(i).municipality.toLowerCase().contains(news.get(j).municipality.toLowerCase())
							&& news.get(i).newspaper.equals(news.get(j).newspaper)
							&& news.get(i).tag.equals(news.get(j).tag)
						//	&& !news.get(j).municipality.toLowerCase().trim().equals("")
						//	&& !news.get(i).municipality.toLowerCase().trim().equals("")
							&& news.get(i).id < news.get(j).id){
						
								if(news.get(i).municipality.toLowerCase().contains(news.get(j).municipality.toLowerCase()) || news.get(i).municipality.equals("")
										|| news.get(i).municipality.equals(" ")
										|| news.get(j).municipality.equals("")
										|| news.get(j).municipality.equals(" ")){
									
									double score = 0;

									if(news.get(i).municipality.toLowerCase().trim().contains(news.get(j).municipality.toLowerCase().trim())
										&& !news.get(i).municipality.toLowerCase().trim().equals("")
									    && !news.get(j).municipality.toLowerCase().trim().equals("")
										&& !news.get(i).municipality.toLowerCase().trim().equals(" ")
										&& !news.get(j).municipality.toLowerCase().trim().equals(" ")) {
										
										score += 0.025;
										
									}
											
									/*	if(distance(Double.parseDouble(news.get(i).lat), Double.parseDouble(news.get(i).lon),
												Double.parseDouble(news.get(j).lat),Double.parseDouble(news.get(j).lon), "K") < 1) {
											
											score = score+0; // sistemare valore
										}
										
										if(distance(Double.parseDouble(news.get(i).lat), Double.parseDouble(news.get(i).lon),
												Double.parseDouble(news.get(j).lat),Double.parseDouble(news.get(j).lon), "K") < 0.5) {
											
											score = score+0; // sistemare valore
										}
										if(distance(Double.parseDouble(news.get(i).lat), Double.parseDouble(news.get(i).lon),
												Double.parseDouble(news.get(j).lat),Double.parseDouble(news.get(j).lon), "K") < 0.2) {
											
											score = score+0; // sistemare valore 
										} */
											
									if(news.get(i).date!=null && news.get(j).date!=null) {
										if(DateDistance(news.get(i).date,news.get(j).date) == 0)
											score = score + 0.03;
									}
									
									if(news.get(i).date!=null && news.get(j).date!=null) {
										if(DateDistance(news.get(i).date,news.get(j).date) == 1)
											score = score + 0.015;
									}
				
									//calcolo il valore della similarità normalizzato
									
									double text = c.similarity(news.get(i).text ,news.get(j).text);
									double title = c2.similarity(news.get(i).title ,news.get(j).title);
									double description = c3.similarity(news.get(i).description ,news.get(j).description);
									
									if (news.get(i).description.equals("")
									|| news.get(i).description.equals(" ") 
									|| news.get(j).description.equals("")
									|| news.get(j).description.equals(" ")
									|| news.get(j).description == null
									|| news.get(i).description == null)
										value = valueWithoutDes(x,z,title, text);
									else										
										value = valueWithDes(x, y, z,title, description, text);
									
									value += score;
									
									comparison++;
									
									// normalizzo il valore di value tenendo conto che score è già normalizzato (compreso tra 0 e 1)
									// il valore massimo di value è 0,025+0,03+1=1,055
									value = value/1.055;
		
									if (value > threshold) {
										
										if(news.get(i).date!=null && news.get(j).date!=null) {
											duplicate.add(new Duplicate(news.get(i).id, news.get(i).url, news.get(j).url, news.get(j).id , value, DateDistance(news.get(i).date,news.get(j).date)));
										}
										else {
											duplicate.add(new Duplicate(news.get(i).id, news.get(i).url, news.get(j).url, news.get(j).id , value, -1));
										}
										
										cont++;
										
										// System.out.println("Inserimento notizie duplicate: "+news.get(i).id+","+news.get(j).id);
										insertDuplicate = conn.prepareStatement("INSERT INTO crime_news.duplicate (id_news1, id_news2, value, algorithm) VALUES (?, ?, ?, ?)");
										insertDuplicate.setInt(1, news.get(i).id);
										insertDuplicate.setInt(2, news.get(j).id);
										insertDuplicate.setDouble(3, value);
										
										if(news.get(i).description.equals("")
										|| news.get(i).description.equals(" ") 
										|| news.get(j).description.equals("")
										|| news.get(j).description.equals(" ")
										|| news.get(j).description == null)
											insertDuplicate.setInt(4, 2);
										else
											insertDuplicate.setInt(4, 1);
										
										int result = -1;
										try {
											result = insertDuplicate.executeUpdate();
											if(result == 1){
												// System.out.println("Inserita nuova coppia di duplicati"+news.get(i).id+" "+news.get(j).id);
												InsertedDuplicateNews++;
											}
										} catch (PSQLException e) {
											// TODO Auto-generated catch block
											//e.printStackTrace();
										}
										
									}
									
									//duplicate.add(new Duplicate(news.get(i).id, news.get(i).url, news.get(j).url, news.get(j).id , value, DateDistance(news.get(i).date,news.get(j).date)));
									
								}
					}
				}
			}
			
		} catch(Exception e){
			System.out.println(e);
		}

		// System.out.println(duplicate);
		System.out.println("Sono stati effettuati " + comparison + " confronti");
		
		return InsertedDuplicateNews;

	}
}
