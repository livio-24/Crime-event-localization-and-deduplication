import java.util.ArrayList;

public class Duplicate {
	
	String url1;
	Integer id1;
	String url2;
	Integer id2;
	Double score;
	Integer date_distance;
	
	public Duplicate (Integer id1, String url1, String url2 , Integer id2,Double score, Integer date_distance) {
		this.id1 = id1;
		this.url1 = url1;
		this.url2 = url2;
		this.id2 = id2;
		this.score= score;
		this.date_distance = date_distance;
		
	}

	
/*	public boolean equals(Object obj) {
		if (obj == null) {
		return false;
		}

		if (!(obj instanceof Duplicate)) {
		return false; 
		} 

		Duplicate d = (Duplicate) obj;
		return d.url1.equals(this.url1) 
				//&& d.url2.equals(this.url2)
				;
		}*/
	
	
	public String toString() {
		
	/*	String urls = "";
		for(int i =0; i< url2.size();i++) {
			urls += url2.get(i).toString()+"\n";
		}*/
			return "-----------------------------------------------------"+"\n"+id1+" "+url1+"\n"+id2+" "+url2+"\n"+score+" "+date_distance+"\n";
	}
	
} 

	

