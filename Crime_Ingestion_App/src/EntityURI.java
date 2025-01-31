import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class EntityURI {

	static String camelCase(String str)
    {
        StringBuilder builder = new StringBuilder(str);
        boolean isLastSpace = true;
        
        // Iterate String from beginning to end.
        for(int i = 0; i < builder.length(); i++)
        {
            char ch = builder.charAt(i);
            
            if(isLastSpace && ch >= 'a' && ch <='z')
            {
                // Character need to be converted to uppercase
                builder.setCharAt(i, (char)(ch + ('A' - 'a') ));
                isLastSpace = false;
            }
            else if (ch != ' ')
                isLastSpace = false;
            else
                isLastSpace = true;
        }
    
        return builder.toString();
    }
	
	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, IOException, ParseException {
		// TODO Auto-generated method stub

		Object obj = new JSONParser().parse(new FileReader("configuration/config.json"));
    	JSONObject jo = (JSONObject) obj;
    	JSONObject db = (JSONObject) jo.get("database");
    	String username = (String) db.get("username");
    	String password = (String) db.get("password");
		
		Class.forName("org.postgresql.Driver");
		String url = "jdbc:postgresql://localhost/postgres?currentSchema=crime_news&user="+username+"&password="+password;
		try {
			Connection conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();

			ResultSet rs = st.executeQuery("SELECT entity, url_news, entity_type "
											+ "FROM crime_news.entity "
											+ "WHERE uri is null ");
			
			while (rs.next()) {
				
				String entity = camelCase(rs.getString(1)).replace(' ', '_');
				String url_news = rs.getString(2);
				String entity_type = rs.getString(3);
				
				HttpClient httpClient = HttpClientBuilder.create().build();
	
				try {
	
				    HttpGet request = new HttpGet("http://dbpedia.org/resource/"+entity);
				    System.out.println(request);
				    request.addHeader("accept", "application/json");
				    HttpResponse response = httpClient.execute(request);
				    HttpEntity http_entity = response.getEntity();
				    String content = EntityUtils.toString(http_entity);
				    //System.out.println(content);
				    if(content.equals("{ }\n")) {
				    	System.out.println("not found");
				    }
				    else {
				    	System.out.println("found");
				    	String uri = "http://dbpedia.org/resource/"+entity;
				    	String query = "update crime_news.entity set uri = ? where url_news = ? and entity = ? and entity_type = ?";
				    	PreparedStatement pstmt = conn.prepareStatement(query);
	                    pstmt.setString(1, uri);
	                    pstmt.setString(2, url_news);
	                    pstmt.setString(3, rs.getString(1));
	                    pstmt.setString(4, entity_type);
	    			    System.out.println("uri: " + uri);
	    			    System.out.println("url: " + url_news);
	    			    System.out.println("Entity: " + entity);
	    			    System.out.println("Entity-type: " + entity_type);
	                    int affectedrows = pstmt.executeUpdate();
	                    if(affectedrows!=1) {
	                    	System.out.println(affectedrows);
	                    	System.out.println("ERROR: update error");
	                    }
				    }
				    
				} catch (Exception ex) {
					
				} finally {
					
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e);
		}
	}

}
