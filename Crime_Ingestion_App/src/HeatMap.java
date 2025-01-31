import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class HeatMap {

	public HeatMap() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ClassNotFoundException, SQLException {
		String year = args[0];
		PrintWriter writer = new PrintWriter("heatmap" + year + ".json");
		writer.println("eqfeed_callback({\"type\":\"FeatureCollection\",\"features\":[");
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=root");
		Statement s = conn.createStatement();
		String select = "select lat, lon, count(*) as count "
				+ "from news.news "
				+ "where year(date)=" + year
				+ " group by lat, lon";
		ResultSet rs = s.executeQuery(select);
		while (rs.next()) {
			  String lat = rs.getString("lat");
			  String lon = rs.getString("lon");
			  int count = rs.getInt("count");
			  if(!lat.equals(" ") && !lon.equals(" ")){
				  writer.println("{\"type\":\"Feature\",\"properties\":{\"count\":" + count + "},\"geometry\":{\"type\":\"Point\",\"coordinates\":[" + lon + "," + lat + "]}},");
			  }
			}

		
		writer.println("]});");
				
		writer.close();

	}

}
