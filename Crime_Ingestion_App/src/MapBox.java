import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class MapBox {

	public MapBox() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, ClassNotFoundException, SQLException {
		String month = args[0];
		String year = args[1];
		String month_string = "";
		if(month.equals("01"))
			month_string = "January";
		else if(month.equals("02"))
			month_string = "February";
		else if(month.equals("03"))
			month_string = "March";
		else if(month.equals("04"))
			month_string = "April";
		else if(month.equals("05"))
			month_string = "May";
		else if(month.equals("06"))
			month_string = "June";
		else if(month.equals("07"))
			month_string = "July";
		else if(month.equals("08"))
			month_string = "August";
		else if(month.equals("09"))
			month_string = "September";
		else if(month.equals("10"))
			month_string = "October";
		else if(month.equals("11"))
			month_string = "November";
		else if(month.equals("12"))
			month_string = "December";
		PrintWriter writer = new PrintWriter("maps/map" + month_string + year + ".html");
		writer.println("<!DOCTYPE html>");
		writer.println("<html>");
		writer.println("<head>");
		writer.println("<meta charset='utf-8' />");
		writer.println("<meta name='viewport' content='initial-scale=1,maximum-scale=1,user-scalable=no' />");
		writer.println("<script src='https://api.tiles.mapbox.com/mapbox-gl-js/v0.48.0/mapbox-gl.js'></script>");
		writer.println("<link href='https://api.tiles.mapbox.com/mapbox-gl-js/v0.48.0/mapbox-gl.css' rel='stylesheet' />");
		writer.println("<style>");
		writer.println("body { margin:0; padding:0; }");
		writer.println("#map { position:absolute; top:0; bottom:0; width:100%; }");
		writer.println("</style>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<div id='map'></div>");
		writer.println("<script>");
		writer.println("mapboxgl.accessToken = 'pk.eyJ1IjoiZnJvbGxvIiwiYSI6ImNqbG5xZWthdzFndnQzcXIwYjhrNTgyMnUifQ.urtfcX07wO2y6r41jyL-ew';");
		writer.println("var map = new mapboxgl.Map({");
		writer.println("container: 'map',");
		writer.println("style: 'mapbox://styles/mapbox/basic-v9',");
		writer.println("center: [10.8516923,44.6501557],");
		writer.println("zoom: 9");
		writer.println("});");
		writer.println("map.on('load', function() {");
		writer.println("map.addSource(\"earthquakes\", {");
		writer.println("type: \"geojson\",");
		writer.println("data: {");
		writer.println("\"type\": \"FeatureCollection\",");
		writer.println("\"crs\": { \"type\": \"name\", \"properties\": { \"name\": \"urn:ogc:def:crs:OGC:1.3:CRS84\" } },");
		writer.println("\"features\": [");
				
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn =  DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=Tirocinio");
		Statement s = conn.createStatement();
		String select = "select url, title, description, text, municipality, address, date, object, lat, lon, newspaper, tag, is_general "
				+ "from crime_news.news "
				+ "where year(date)=" + year
				+ " and month(date)=" + month;
		ResultSet rs = s.executeQuery(select);
		while (rs.next()) {
		
			  String url = rs.getString("url");
			  String title = rs.getString("title");
			  title = title.replace("'", " ");
			  title = title.replace("\"", " ");
			  String description = rs.getString("description");
			  String municipality = rs.getString("municipality");
			  String address = rs.getString("address");
			  Date date = rs.getDate("date");
			  String object = rs.getString("object");
			  String lat = rs.getString("lat");
			  String lon = rs.getString("lon");
			  String newspaper = rs.getString("newspaper");
			  String tag = rs.getString("tag");
			  int is = rs.getInt("is_general");
			//if(rs.getString("newspaper").equals("ModenaToday") /* && rs.getString("tag").equals("furto")*/) {
				  //System.out.println(lat+" "+lon);44.6904605 10.6309812
			  if(!lon.equals(" ") && !lat.equals(" ") && !lon.equals("") && !lat.equals("")){
				
				  System.out.println(lat+" "+lon);
				  System.out.println(newspaper);
				  System.out.println(municipality);
				  System.out.println(address);
				  System.out.println("____________________________________________");
				  writer.println("{ \"type\": \"Feature\", \"properties\": {\"mag\": 1}, \"geometry\": { \"type\": \"Point\", \"coordinates\": [" + lon + "," + lat + ", 0.0 ] } },");				  
			  }
			 }
			//}
		

		
		writer.println("]},");
		writer.println("cluster: true,");
		writer.println("clusterMaxZoom: 14,");
		writer.println("clusterRadius: 50");
		writer.println("});");
		writer.println("map.addLayer({");
		writer.println("id: \"clusters\",");
		writer.println("type: \"circle\",");
		writer.println("source: \"earthquakes\",");
		writer.println("filter: [\"has\", \"point_count\"],");
		writer.println("paint: {");
		writer.println("\"circle-color\": [");
		writer.println("\"step\",");
		writer.println("[\"get\", \"point_count\"],");
		writer.println("\"#FF8141\",");
		writer.println("5,");
		writer.println("\"#FF0000\",");
		writer.println("15,");
		writer.println("\"#600000\"");
		writer.println("],");
		writer.println("\"circle-radius\": [");
		writer.println("\"step\",");
		writer.println("[\"get\", \"point_count\"],");
		writer.println("20,5,30,15,40");
		writer.println("]}});");
		writer.println("map.addLayer({");
		writer.println("id: \"cluster-count\",");
		writer.println("type: \"symbol\",");
		writer.println("source: \"earthquakes\",");
		writer.println("filter: [\"has\", \"point_count\"],");
		writer.println("layout: {");
		writer.println("\"text-field\": \"{point_count_abbreviated}\",");
		writer.println("\"text-font\": [\"DIN Offc Pro Medium\", \"Arial Unicode MS Bold\"],");
		writer.println("\"text-size\": 12");
		writer.println("}});");
		writer.println("map.addLayer({");
		writer.println("id: \"unclustered-point\",");
		writer.println("type: \"circle\",");
		writer.println("source: \"earthquakes\",");
		writer.println("filter: [\"!\", [\"has\", \"point_count\"]],");
		writer.println("paint: {");
		writer.println("\"circle-color\": \"#FF8141\",");
		writer.println("\"circle-radius\": 10,");
		writer.println("\"circle-stroke-width\": 1,");
		writer.println("\"circle-stroke-color\": \"#fff\"");
		writer.println("}});");
		writer.println("map.on('click', 'clusters', function (e) {");
		writer.println("var features = map.queryRenderedFeatures(e.point, { layers: ['clusters'] });");
		writer.println("var clusterId = features[0].properties.cluster_id;");
		writer.println("map.getSource('earthquakes').getClusterExpansionZoom(clusterId, function (err, zoom) {");
		writer.println("if (err) return;");
		writer.println("map.easeTo({");
		writer.println("center: features[0].geometry.coordinates,");
		writer.println("zoom: zoom");
		writer.println("});});});");
		writer.println("map.on('mouseenter', 'clusters', function () {");
		writer.println("map.getCanvas().style.cursor = 'pointer';");
		writer.println("});");
		writer.println("map.on('mouseleave', 'clusters', function () {");
		writer.println("map.getCanvas().style.cursor = '';");
		writer.println("});});");
		writer.println("</script>");
		writer.println("</body>");
		writer.println("</html>");
		writer.close();

	}

}
