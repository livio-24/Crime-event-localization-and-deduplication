import java.util.Date;

public class News {

	Integer id;
	String url;
	String title;
	String description;
	String text;
	String municipality;
	Date date;
	String newspaper;
	String tag;
	
	
	
	public News(Integer id, String url, String title, String description, String text, String municipality, Date date, String newspaper, String tag) {
		
		this.id = id;
		this.url = url;
		this.title = title;
		this.description = description;
		this.text = text;
		this.municipality = municipality;
		this.date = date;
		this.newspaper= newspaper;
		this.tag = tag;
		
	}
	

	
	public String toString() {
		
		return id+" "+url+" "+title+" "+municipality+" "+date.toString()+" "+newspaper+" "+tag;
	}
	
	
	


}
