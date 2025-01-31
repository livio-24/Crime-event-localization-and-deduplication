import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Map {

	public Map() {
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
		writer.println("<script type=\"text/javascript\" src=\"jquery/jquery.min.js\"></script>");
		writer.println("<script type=\"text/javascript\" src=\"tipsy/jquery.tipsy.js\"></script>");
		writer.println("<script type=\"text/javascript\" src=\"polymaps.js\"></script>");
		writer.println("<style type=\"text/css\">");
		writer.println("@import url(\"tipsy/tipsy.css\");\n"
				+ "@import url(\"example.css\");\n"
				+ ".layer circle {\n"
				+ "fill: lightcoral;\n"
				+ "fill-opacity: .5;\n"
				+ "stroke: brown;\n"
				+ "stroke-width: 1.5px;\n"
				+ "}\n"
				+ ".tipsy-inner {\n"
				+ "padding: 3px;\n"
				+ "line-height: 0;\n"
				+ "}\n"
				+ ".tipsy-inner img {\n"
				+ "-moz-border-radius: 3px;\n"
				+ "-webkit-border-radius: 3px;\n"
				+ "background: white;\n"
				+ "}");
		writer.println("</style>");
		writer.println("</head>");
		writer.println("<body>");
		writer.println("<script type=\"text/javascript\">");
		writer.println("var po = org.polymaps;\n"
				+ "var radius = 10, tips = {};\n"
				+ "var map = po.map()\n"
				+ ".container(document.body.appendChild(po.svg(\"svg\")))\n"
				+ ".center({lon: 10.851272, lat: 44.692448})\n"
				+ ".zoom(10)\n"
				+ ".add(po.interact())\n"
				+ ".on(\"move\", move)\n"
				+ ".on(\"resize\", move);\n"
				+ "map.add(po.image()\n"
				+ ".url(po.url(\"http://{S}tile.cloudmade.com/1a1b06b230af4efdbb989ea99e9841af/998/256/{Z}/{X}/{Y}.png\")\n"
				+ ".hosts([\"a.\", \"b.\", \"c.\", \"\"])));\n"
				+ "map.add(po.geoJson()\n"
				+ ".on(\"load\", load)\n"
				+ ".on(\"show\", show)\n"
				+ ".features([\n");
		
		Class.forName("com.mysql.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=root");
		Statement s = conn.createStatement();
		String select = "select url, title, description, municipality, address, date, object, lat, lon "
				+ "from news.news "
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
			  if(!lat.equals(" ") && !lon.equals(" ")){
				  writer.println("{");
				  writer.println("\"id\": \"" + url + "\",");
				  writer.println("\"properties\": {");
				  writer.print("\"html\": \"<h2><a href=\\\" " + url + "\\\" target=\\\"blank\\\">news</a></h2><br><br><h2>");
				  if(!municipality.equals("") && !municipality.equals(" "))
					  writer.print("<p>" + municipality + "<p><br>");
				  if(!address.equals("") && !address.equals(" "))
					  writer.print("<p>" + address + "<p><br>");
				  if(!object.equals("") && !object.equals(" "))
					  writer.print("<p>" + object + "<p><br>");
				  if(!date.equals("") && !date.equals(" "))
					  writer.print("<p>" + date + "<p><br>");
				  writer.println("</h2>\",");
				  writer.println("},");
				  writer.println("\"geometry\": {");
				  writer.println("\"coordinates\": [" + lon + ", " + lat + "],");
				  writer.println("\"type\": \"Point\"");
				  writer.println("}");
				  writer.println("},");
			  }
			}
		
		writer.println("]));");
		writer.println("map.add(po.compass()\n"
				+ ".pan(\"none\"));\n"
				+ "function load(e) {\n"
				+ "for (var i = 0; i < e.features.length; i++) {\n"
				+ "var f = e.features[i];\n"
				+ "f.element.setAttribute(\"r\", radius);\n"
				+ "f.element.addEventListener(\"mousedown\", toggle(f.data), false);\n"
				+ "f.element.addEventListener(\"dblclick\", cancel, false);\n"
				+ "}\n"
				+ "}\n"
				+ "function show(e) {\n"
				+ "for (var i = 0; i < e.features.length; i++) {\n"
				+ "var f = e.features[i], tip = tips[f.data.id];\n"
				+ "tip.feature = f.data;\n"
				+ "tip.location = {\n"
				+ "lat: f.data.geometry.coordinates[1],\n"
				+ "lon: f.data.geometry.coordinates[0]\n"
				+ "};\n"
				+ "update(tip);\n"
				+ "}\n"
				+ "}\n"
				+ "function move() {\n"
				+ "for (var id in tips) {\n"
				+ "update(tips[id]);\n"
				+ "}\n"
				+ "}\n"
				+ "function cancel(e) {\n"
				+ "e.stopPropagation();\n"
				+ "e.preventDefault();\n"
				+ "}\n"
				+ "function update(tip) {\n"
				+ "if (!tip.visible) return;\n"
				+ "var p = map.locationPoint(tip.location);\n"
				+ "tip.anchor.style.left = p.x - radius + \"px\";\n"
				+ "tip.anchor.style.top = p.y - radius + \"px\";\n"
				+ "$(tip.anchor).tipsy(\"show\");\n"
				+ "}\n"
				+ "function toggle(f) {\n"
				+ "var tip = tips[f.id];\n"
				+ "if (!tip) {\n"
				+ "tip = tips[f.id] = {\n"
				+ "anchor: document.body.appendChild(document.createElement(\"a\")),\n"
				+ "visible: false,\n"
				+ "toggle: function(e) {\n"
				+ "tip.visible = !tip.visible;\n"
				+ "update(tip);\n"
				+ "$(tip.anchor).tipsy(tip.visible ? \"show\" : \"hide\");\n"
				+ "cancel(e);\n"
				+ "}\n"
				+ "};\n"
				+ "tip.anchor.style.position = \"absolute\";\n"
				+ "tip.anchor.style.visibility = \"hidden\";\n"
				+ "tip.anchor.style.width = radius * 2 + \"px\";\n"
				+ "tip.anchor.style.height = radius * 2 + \"px\";\n"
				+ "$(tip.anchor).tipsy({\n"
				+ "html: true,\n"
				+ "fallback: f.properties.html,\n"
				+ "gravity: $.fn.tipsy.autoNS,\n"
				+ "trigger: \"manual\"\n"
				+ "});\n"
				+ "}\n"
				+ "return tip.toggle;\n"
				+ "}\n");
		writer.println("</script>");
		writer.println("<span id=\"copy\">");
		writer.println("&copy; 2010");
		writer.println("<a href=\"http://www.cloudmade.com/\">CloudMade</a>,");
		writer.println("<a href=\"http://www.openstreetmap.org/\">OpenStreetMap</a> contributors,");
		writer.println("<a href=\"http://creativecommons.org/licenses/by-sa/2.0/\">CCBYSA</a>.");
		writer.println("</span>");
		writer.println("</body>");
		writer.println("</html>");
		writer.close();

	}

}
