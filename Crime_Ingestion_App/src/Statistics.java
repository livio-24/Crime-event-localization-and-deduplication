import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class Statistics {
	
	public Statistics() {
		
	}
	
	// numero di news nel DB
	
	public int getNewsNumber(Connection conn) {
		
		int cont =0;
		try {
			Statement stmnt=conn.createStatement();  
			ResultSet rs=stmnt.executeQuery("select url from crime_news.news");  
			
			while(rs.next())  {
			cont++;
			}
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return cont;
	}
	
	// numero di news della gazzetta di modena nel DB
	public int getNewsNumberGM(Connection conn) {
		
		int cont =0;
		try {
			Statement stmnt=conn.createStatement();  
			ResultSet rs=stmnt.executeQuery("select newspaper from crime_news.news");  
			while(rs.next())  {
				if(rs.getString(1).equals("Gazzetta di Modena"))
			cont++;
			}
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return cont;
	}

	//numero di news del modenatoday nel DB
	public int getNewsNumberMT(Connection conn) {
	
		int cont =0;
		try {
			Statement stmnt=conn.createStatement();  
			ResultSet rs=stmnt.executeQuery("select newspaper from crime_news.news");  
			while(rs.next())  {
				if(rs.getString(1).equals("ModenaToday"))
			cont++;
			}
		}catch(Exception e){ 
			System.out.println(e);
			}  
		
		return cont;
	}

	// news per giorno
	public double getNewsforDay(Connection conn) {
		
		int n = 0;
		ArrayList <Date> date = new ArrayList<Date>();
		
		try {
			Statement stmnt=conn.createStatement();  
			ResultSet rs=stmnt.executeQuery("select date_publication from crime_news.news");  
			while(rs.next()) {
				n++;
				date.add(rs.getDate(1));
			}
		} catch(Exception e){ 
			System.out.println(e);
		}  
		
		ArrayList<Date> SingleDate = new ArrayList<Date>();
		for (int i = 0; i < date.size(); i++) {
			if(!SingleDate.contains(date.get(i))) {
				SingleDate.add(date.get(i));
			}
		}
		double sd = SingleDate.size();
		double d = (n / sd);
		return d;
	}

	public Integer IntToInteger(int i) {
		
		Integer in = new Integer(i);
		return in;
		
	}


	// news per ogni tipo di tag
	public HashMap<String, Integer> getNewsforTag(Connection conn) {
		
		int contTheft =0;
		int contAggression =0;
		int contDDealing =0;
		int contEvasion =0;
		int contFraud =0;
		int contLaundering =0;
		int contMistreatment =0;
		int contMurder =0;
		int contRobbery =0;
		int contSAssault =0;
		int contSeizures =0;
		int contScams =0;
		int contDrugs =0;
		
		
		HashMap<String, Integer> data = new HashMap<String, Integer>();
		
		
		try {
			Statement stmnt=conn.createStatement();  
			ResultSet rs=stmnt.executeQuery("select tag from crime_news.news");  
			while(rs.next())  {
				if(rs.getString(1).equals("aggressione"))
					contAggression ++;
				
				if(rs.getString(1).equals("furto"))
					contTheft++;
				
				if(rs.getString(1).equals("spaccio"))
					contDDealing++;
				
				if(rs.getString(1).equals("droga"))
					contDrugs++;
				
				if(rs.getString(1).equals("frode"))
					contFraud++;
				
				if(rs.getString(1).equals("evasione"))
					contEvasion++;
				
				if(rs.getString(1).equals("riciclaggio"))
					contLaundering++;
				
				if(rs.getString(1).equals("maltrattamento"))
					contMistreatment++;
				
				if(rs.getString(1).equals("omicidio"))
					contMurder++;
				
				if(rs.getString(1).equals("rapina"))
					contRobbery++;
				
				if(rs.getString(1).equals("violenza sessuale"))
					contSAssault++;
				
				if(rs.getString(1).equals("truffa"))
					contScams++;
				
				if(rs.getString(1).equals("sequestro"))
					contSeizures++;
				
			}
		} catch(Exception e){ 
			System.out.println(e);
		}  
		
		data.put("aggressione", IntToInteger(contAggression));
		data.put("furto", IntToInteger(contTheft));
		data.put("spaccio", IntToInteger(contDDealing));
		data.put("droga", IntToInteger(contDrugs));
		data.put("frode", IntToInteger(contFraud));
		data.put("evasione", IntToInteger(contEvasion));
		data.put("riciclaggio", IntToInteger(contLaundering));
		data.put("maltrattamento", IntToInteger(contMistreatment));
		data.put("omicidio", IntToInteger(contMurder));
		data.put("rapina", IntToInteger(contRobbery));
		data.put("violenza sessuale", IntToInteger(contSAssault));
		data.put("truffa", IntToInteger(contScams));
		data.put("sequestro", IntToInteger(contSeizures));
		
		return data;
		
	}
	
}
