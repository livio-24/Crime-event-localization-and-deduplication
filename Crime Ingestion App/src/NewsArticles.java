import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;



import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.postgresql.util.PSQLException;

import edu.stanford.nlp.pipeline.Annotation;
import eu.fbk.dh.tint.runner.TintPipeline;
import eu.fbk.dh.tint.runner.TintRunner;

public class NewsArticles {

	
	public int addNewsMT(Connection conn, Integer pages, String url_news, String crime_type) throws FileNotFoundException, IOException, SQLException, ParseException, Exception {

		int insertedNews = 0;

		List < String > areas = new ArrayList < String > ();
		areas = Utils.load_list("configuration/areaList_ModenaToday.txt");

		List < String > municipalities = new ArrayList < String > ();
		municipalities = Utils.load_list("configuration/municipalityList_ModenaToday.txt");

		List < String > municipalitiesSemplified = new ArrayList < String > ();
		municipalitiesSemplified = Utils.load_list("configuration/municipalitysemplifiedList_ModenaToday.txt");

		List < String > stopwords = new ArrayList < String > ();
		stopwords = Utils.load_list("configuration/stopwords.txt");

		for (int count = 1; count <= pages; count++) {
			String url_news_concatenated = url_news + count + "/";
			System.out.println(url_news_concatenated);
			Document document = Jsoup.connect(url_news_concatenated).get();

			String link = "";
			org.jsoup.select.Elements links = document.select("a[href][class='link']");
			for (Element element: links) {
				
				if ((element.attr("href").startsWith("/cronaca/") || element.attr("href").startsWith("/video/")) && element.attr("href").endsWith(".html")) {
					link = "https://www.modenatoday.it" + element.attr("href");

					// storing the link of the news in the database
					PreparedStatement stmt = conn.prepareStatement("INSERT INTO crime_news.link (url) VALUES (?)");
					stmt.setString(1, link);
					int result = -1;
					try {
						result = stmt.executeUpdate();
						if (result == 1) {
							System.out.println("Inserted: " + link);
							Document news = Jsoup.connect(link).timeout(300000).get();

							//ADDRESS
							List < String > addressNews = new ArrayList < String > ();
							String area = " ",
							municipality = " ",
							address = " ";
							org.jsoup.select.Elements addresselement = news.select("span[class='entry-label']");
							for (Element e: addresselement) {
								//System.out.println("Address: " + e.text());
								addressNews.add(e.text().replace("/", "").trim());
							}
							if (addressNews.size() == 1) {
								if (areas.contains(addressNews.get(0).toLowerCase())) {
									municipality = "Modena";
									area = addressNews.get(0);
								}
								else if (municipalities.contains(addressNews.get(0).toLowerCase())) {
									municipality = addressNews.get(0);
								}
								else {
									municipality = "Modena";
									address = addressNews.get(0);
								}
							}
							if (addressNews.size() == 2) {
								if (addressNews.get(0).toLowerCase().equals(addressNews.get(1).toLowerCase())) {
									municipality = "Modena";
									address = addressNews.get(0);
								}
								if (areas.contains(addressNews.get(0).toLowerCase())) {
									municipality = "Modena";
									area = addressNews.get(0);
									address = addressNews.get(1);
								}
								else if (municipalities.contains(addressNews.get(0).toLowerCase())) {
									municipality = addressNews.get(0);
									address = addressNews.get(1);
								}
							}

							
							//DATE
							long i = 0;
							Date data = new Date(i);
							java.sql.Time time = new java.sql.Time(0);
							org.jsoup.select.Elements date = news.select("time[class='datestamp']");
							SimpleDateFormat inFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ITALIAN);
							SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
							SimpleDateFormat outFormat1 = new SimpleDateFormat("HH:mm", Locale.ITALIAN);
							for (Element e: date) {
								//System.out.println(e.text());
								String date_string = outFormat.format(inFormat.parse(e.text()));
								time = new java.sql.Time(inFormat.parse(e.text()).getTime());
								data = Date.valueOf(date_string);
							}
							System.out.println(data);
							System.out.println(time);

							
							//TITLE
							String title = new String();
							org.jsoup.select.Elements t = news.select("meta[name='title'][content]");
							for (Element e: t) {
								title = t.attr("content");
							}
							System.out.println(title);

							
							//DESCRIPTION
							String description = new String();
							org.jsoup.select.Elements d = news.select("meta[name='description'][content]");
							for (Element e: d) {
								description = d.attr("content");
							}
							System.out.println(description);

							
							//TEXT
							Element body = news.body();
							org.jsoup.select.Elements paragraphs = body.getElementsByTag("p");
							String textBody = new String();
							for (Element paragraph: paragraphs) {
								if (!paragraph.text().toString().startsWith("Attendere un istante") && !paragraph.text().toString().startsWith("oppure fai il login") && !paragraph.text().toString().startsWith("Il meglio delle notizie dall'italia") && !paragraph.text().toString().startsWith("Leader nell'informazione") && !paragraph.text().toString().startsWith("� Copyright") && !paragraph.text().toString().startsWith("oppure usa il tuo account") && !paragraph.text().toString().startsWith("R:") && !paragraph.text().toString().isEmpty()) textBody = textBody.concat(paragraph.text() + " ");
							}
							textBody = textBody.replace(" Il meglio delle notizie dall'Italia e dal mondo", "");
							textBody = textBody.replace(description, "");
							System.out.println("Body " + textBody);

							
							// Trying to retrieve the stolen object, the object of aggression and so on
							String text = title + " " + description;
							String object = " ";
							if (crime_type.equals("furto")) object = Utils.TheftObject(text, stopwords);
							else if (crime_type.equals("aggressione")) object = Utils.AggressionObject(text, stopwords);
							else if (crime_type.equals("maltrattamento")) object = Utils.MistreatmentObject(text, stopwords);
							else if (crime_type.equals("droga")) object = Utils.DrugsObject(text, stopwords);
							else if (crime_type.equals("omicidio")) object = Utils.MurderObject(text, stopwords);
							else if (crime_type.equals("riciclaggio")) object = Utils.LaunderingObject(text, stopwords);
							else if (crime_type.equals("frode")) object = Utils.FraudObject(text, stopwords);
							else if (crime_type.equals("rapina")) object = Utils.RobberyObject(text, stopwords);
							else if (crime_type.equals("spaccio")) object = Utils.DDealingObject(text, stopwords);
							else if (crime_type.equals("truffa")) object = Utils.ScamsObject(text, stopwords);
							else if (crime_type.equals("sequestro")) object = Utils.SeizuresObject(text, stopwords);
							else if (crime_type.equals("evasione")) object = Utils.EvasionObject(text, stopwords);
							else if (crime_type.equals("violenza sessuale")) object = Utils.SAssoultObject(text, stopwords);

							
							//EVENT_DATE
							String concatenation = title + ' ' + description + ' ' + textBody;
							Date event_date = Heideltime.findEventDate(concatenation, data);
							
							
							// latitude, longitude
							String lat = "",
							lon = "";
							String comune = " ",
							phrase = " ";
							if (municipality.equals(" ") || municipality.equals("")) {
								if (comune.equals(" ") || comune.equals("")) {
									phrase = Utils.getComuneStringa(title);
									for (String s: municipalities) {
										if (phrase.trim().contains(s)) {
											municipality = s;
											break;
										}
									}
								}
								if (municipality.equals(" ") || municipality.equals("")) {
									for (String s: municipalitiesSemplified) {
										if (phrase.trim().contains(s)) {
											municipality = s;
											break;
										}
									}
								}
							}

							if (municipality.contentEquals("castelfranco")) {
								municipality = "castelfranco emilia";
							}

							else if (municipality.contentEquals("castelnuovo")) {
								municipality = "castelnuovo rangone";
							}

							else if (municipality.contentEquals("castelvetro")) {
								municipality = "castelvetro di modena";
							}

							else if (municipality.contentEquals("concordia")) {
								municipality = "concordia sulla secchia";
							}

							else if (municipality.contentEquals("finale")) {
								municipality = "finale emilia";
							}

							else if (municipality.contentEquals("fiorano")) {
								municipality = "fiorano modenese";
							}

							else if (municipality.contentEquals("marano")) {
								municipality = "marano sul panaro";
							}

							else if (municipality.contentEquals("pavullo")) {
								municipality = "pavullo nel frignano";
							}

							else if (municipality.contentEquals("novi")) {
								municipality = "novi di modena";
							}

							else if (municipality.contentEquals("prignano")) {
								municipality = "noviprignano sulla secchia";
							}

							else if (municipality.contentEquals("san cesario")) {
								municipality = "san cesario sul panaro";
							}

							else if (municipality.contentEquals("san felice")) {
								municipality = "san felice sul panaro";
							}

							else if (municipality.contentEquals("savignano")) {
								municipality = "savigano sul panaro";
							}

							if ((address.equals(" ") || address.equals("")) && textBody.length() > 2) {
								TintPipeline pipeline = new TintPipeline();
								pipeline.loadDefaultProperties();
								pipeline.load();
								Annotation stanfordAnnotation = pipeline.runRaw(textBody.replace('\"', ' ').replace('\t', ' ').replace('\n', ' '));
								File file = new File("new_JSON.json");
								OutputStream outputstream = new FileOutputStream(file);
								InputStream stream = new ByteArrayInputStream(textBody.getBytes(StandardCharsets.UTF_8));
								Annotation annotation = pipeline.run(stream, outputstream, TintRunner.OutputFormat.JSON);
								outputstream.close();
								Object obj = new JSONParser().parse(new FileReader("new_JSON.json"));
								JSONObject jo = (JSONObject) obj;
								List < String > pos = new ArrayList < String > ();
								String ind = "";
								String word = "";
								JSONArray ja = (JSONArray) jo.get("sentences");
								Iterator itr = ja.iterator();
								while (itr.hasNext()) {
									Iterator < Map.Entry > itr1 = ((Map) itr.next()).entrySet().iterator();
									while (itr1.hasNext()) {
										Map.Entry pair = itr1.next();
										if (pair.getKey().equals("tokens")) {
											JSONArray tokens_array = (JSONArray) pair.getValue();
											Iterator itr2 = tokens_array.iterator();
											while (itr2.hasNext()) {
												Iterator < Map.Entry > itr3 = ((Map) itr2.next()).entrySet().iterator();
												String ner = new String();
												while (itr3.hasNext()) {
													Map.Entry pair_tokens = itr3.next();
													if (pair_tokens.getKey().equals("ner")/*&& pair_tokens.getValue().equals("LOC")*/)
														ner = pair_tokens.getValue().toString();
													if (pair_tokens.getKey().equals("word")) word = pair_tokens.getValue().toString();
												}
												if (ner.equals("LOC")) pos.add(word.toLowerCase());
											}
										}
									}
								}
								System.out.println(pos);

								for (int j = 0; j < pos.size(); j++) {
									ind += pos.get(j) + " ";
								}
								ind = ind.replace(comune, "");
								ind = ind.replace("comune", "");

								int cont = 0;
								String location = "";
								String street = "";

								if (municipality.equals("") || municipality.equals(" ")) {
									for (String s: municipalities) {
										if (ind.trim().contains(s)) {
											municipality = s;
											break;
										}
									}
								}
								if (municipality.equals(" ") || municipality.equals("")) {
									for (String s: municipalitiesSemplified) {
										if (ind.trim().contains(s)) {
											municipality = s;
											break;
										}
									}
								}
								if (area.equals(" ") || area.equals(" ")) {
									if (municipality.equals("modena") || municipality.equals("Modena")) {
										for (String s: areas) {
											if (ind.trim().contains(s)) {
												area = s;
												break;
											}
										}
									}
								}

								for (int k = 0; k < pos.size(); k++) {
									if (pos.get(k).equals("via") || pos.get(k).equals("viale") || pos.get(k).equals("strada") || pos.get(k).equals("piazza") || pos.get(k).equals("parco")) {
										location = pos.get(k) + " ";
										for (int j = k + 1; j < pos.size(); j++) {
											if (!pos.get(j).equals("via") && !pos.get(j).equals("viale") && !pos.get(j).equals("strada") && !pos.get(j).equals("piazza") && !pos.get(j).equals("parco")) {
												cont = j;
												location += pos.get(j) + " ";
											}
											else break;
										}

										// latitude, longitude
										street = location.trim() + " " + municipality;
										if (street.trim() != "" && street.trim() != " ") {
											Map < String,
											Double > coords;
											coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
											try {
												lat = coords.get("lat").toString().trim();
												lon = coords.get("lon").toString().trim();
											}
											catch(java.lang.NullPointerException e) {

											}
											//System.out.println("latitude :" + lat);
											//System.out.println("longitude:" + lon);
										}

										if (!lat.equals("") && !lon.equals("")) {
											address = location;
											break;
										} // se � positivo
										else {
											k = cont;
										} // se � negativo riparte saltando la via trovata ma che non ha fornito coordinate
									}

								}
							}

							else {
								String street = address + " " + municipality;
								if (street.trim() != "" && street.trim() != " ") {
									Map < String,
									Double > coords;
									coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
									try {
										lat = coords.get("lat").toString();
										lon = coords.get("lon").toString();
									}
									catch(java.lang.NullPointerException e) {

									}
								}
							}

							System.out.println(municipality);
							System.out.println(area);
							System.out.println(address);
							System.out.println("latitude :" + lat);
							System.out.println("longitude:" + lon);

							if (!lat.equals("") && !lon.equals("")) {
								PreparedStatement insertNews = conn.prepareStatement("INSERT INTO crime_news.news (url, title, description, text, municipality, area, address, date_publication, time_publication, geom, object, newspaper, tag, is_general, date_event) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, public.ST_GeomFromText(?,4326), ?, ?, ?, ?, ?) on conflict do nothing");
								insertNews.setString(1, link);
								insertNews.setString(2, title);
								insertNews.setString(3, description);
								insertNews.setString(4, textBody);
								insertNews.setString(5, municipality);
								insertNews.setString(6, area);
								insertNews.setString(7, address);
								insertNews.setDate(8, data);
								insertNews.setTime(9, time);
								insertNews.setString(10, "POINT(" + lon + " " + lat + ")");
								insertNews.setString(11, object);
								insertNews.setString(12, "ModenaToday");
								insertNews.setString(13, crime_type);
								insertNews.setInt(14, 0);
								insertNews.setDate(15, event_date);
								insertNews.executeUpdate();
							}
							else {
								PreparedStatement insertNews = conn.prepareStatement("INSERT INTO crime_news.news (url, title, description, text, municipality, area, address, date_publication, time_publication, object, newspaper, tag, is_general, date_event) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict do nothing");
								insertNews.setString(1, link);
								insertNews.setString(2, title);
								insertNews.setString(3, description);
								insertNews.setString(4, textBody);
								insertNews.setString(5, municipality);
								insertNews.setString(6, area);
								insertNews.setString(7, address);
								insertNews.setDate(8, data);
								insertNews.setTime(9, time);
								insertNews.setString(10, object);
								insertNews.setString(11, "ModenaToday");
								insertNews.setString(12, crime_type);
								insertNews.setInt(13, 0);
								insertNews.setDate(14, event_date);
								insertNews.executeUpdate();
							}

							insertedNews++;
							
							// entities
							String concatenation_for_entities = title + ' ' + description + ' ' + textBody;
							if (concatenation_for_entities.length() > 2) {
								Entity.extractEntities(link, concatenation_for_entities.replace('\"', ' ').replace('\t', ' ').replace('\n', ' '), conn);
							}

							System.out.println("-----------------------------------------------------------------------");
						}
					} catch(PSQLException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println(e);
					} catch(java.lang.StringIndexOutOfBoundsException e) {
						
					} catch(java.lang.NullPointerException e) {
						
					} catch(IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		return insertedNews;

	}

	public int addNewsGM(Connection conn, Integer pages, String url_news, String crime_type) throws FileNotFoundException,
	SQLException,
	ParseException,
	Exception {

		int insertedNews = 0;

		List < String > areas = new ArrayList < String > ();
		areas = Utils.load_list("configuration/areaList_ModenaToday.txt");

		List < String > municipalities = new ArrayList < String > ();
		municipalities = Utils.load_list("configuration/municipalityList_ModenaToday.txt");

		List < String > municipalitiesSemplified = new ArrayList < String > ();
		municipalitiesSemplified = Utils.load_list("configuration/municipalitysemplifiedList_ModenaToday.txt");

		List < String > stopwords = new ArrayList < String > ();
		stopwords = Utils.load_list("configuration/stopwords.txt");

		for (int count = 1; count <= pages; count++) {
			Document document = Jsoup.connect(url_news + count).get();

			String link = "";
			org.jsoup.select.Elements links = document.select("a[href]");
			for (Element element: links) {
				try {
				if (element.attr("href").startsWith("https://gazzettadimodena.gelocal.it/modena/cronaca/") || element.attr("href").startsWith("https://gazzettadimodena.gelocal.it/modena/cronaca/")
				/* && element.attr("href").endsWith(".html")*/
				) {
					link = element.attr("href");

					// il link alla pagina della notizia appena trovato viene memorizzato nel database 'crime_news' nella tabella 'link'
					PreparedStatement stmt = conn.prepareStatement("INSERT INTO crime_news.link (url) VALUES (?)");
					stmt.setString(1, link);
					int result = -1;
					try {
						result = stmt.executeUpdate();
						if (result == 1) {
							System.out.println("Inserito: " + link);
							try {
								Document news = Jsoup.connect(link).timeout(300000).get();

								//DATE
								long i = 0;
								Date data = new Date(i);
								java.sql.Time time = new java.sql.Time(0);
								org.jsoup.select.Elements date = news.select("span[class='entry_date']");
								SimpleDateFormat inFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN);
								SimpleDateFormat outFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALIAN);
								//SimpleDateFormat outFormat1 = new SimpleDateFormat("HH:mm");
								for (Element e: date) {
									//System.out.println(e.text());
									String date_string = outFormat.format(inFormat.parse(e.text()));
									//time = new java.sql.Time(inFormat.parse(e.text()).getTime());
									data = Date.valueOf(date_string);
								}
								System.out.println(data);
								System.out.println(time);

								
								//TITLE
								String title = new String();
								org.jsoup.select.Elements t = news.select("meta[name='title'][content]");
								for (Element e: t) {
									title = t.attr("content");
								}
								title = title.replace("- cronaca - Gazzetta di Modena", ".");
								System.out.println(title);

								
								//DESCRIPTION 
								String description = new String();
								org.jsoup.select.Elements d = news.select("meta[name='description'][content]");
								for (Element e: d) {
									description = d.attr("content");
								}
								System.out.println(description);

								
								//TEXT 
								Element body = news.body();
								org.jsoup.select.Elements paragraphs = body.select("div[class='entry_content'][id='article-body']");
								String textBody = new String();
								for (Element paragraph: paragraphs) {
									if (!paragraph.text().toString().startsWith("Attendere un istante") && !paragraph.text().toString().startsWith("oppure fai il login") && !paragraph.text().toString().startsWith("Il meglio delle notizie dall'italia") && !paragraph.text().toString().startsWith("Leader nell'informazione") && !paragraph.text().toString().startsWith("� Copyright") && !paragraph.text().toString().startsWith("oppure usa il tuo account") && !paragraph.text().toString().startsWith("R:") && !paragraph.text().toString().isEmpty()) textBody = textBody.concat(paragraph.text() + " ");
								}
								System.out.println("Body " + textBody);

								//EVENT_DATE
								String concatenation = title + ' ' + description + ' ' + textBody;
								Date event_date = Heideltime.findEventDate(concatenation, data);
								
								//ADDRESS
								String area = " ",
								municipality = " ",
								address = " ";
								String phrase = "";
								// metodo creato per estrarre il comune provando la/le prime parole del testo o titolo
								String comune = "";
								comune = Utils.getComune(textBody);
								for (String j: municipalities) {
									if (j.trim().contains(comune.trim())) {
										municipality = comune.trim();
										break;
									}
								}
								if (municipality.equals(" ") || municipality.equals("")) {
									comune = Utils.getComune(title);
									for (String j: municipalities) {
										if (j.trim().contains(comune.trim())) {
											municipality = comune.trim();
											break;
										}
									}
								}
								if (municipality.equals(" ") || municipality.equals("")) {
									phrase = Utils.getComuneStringa(title);
									for (String s: municipalities) {
										if (phrase.trim().contains(s)) {
											municipality = s;
											break;
										}
									}
								}
								if (municipality.equals(" ") || municipality.equals("")) {
									for (String s: municipalitiesSemplified) {
										if (phrase.trim().contains(s)) {
											municipality = s;
											break;
										}
									}
								}
								if (municipality.equals("") || municipality.equals(" ")) {
									String aree = "";
									aree = Utils.getArea(textBody);
									for (String s: areas) {
										if (s.trim().contains(aree.trim())) {
											area = s;
											if (municipality.equals("") || municipality.equals(" ")) municipality = "modena";
											break;
										}
									}
									if (area.equals(" ") || area.equals("")) {
										aree = Utils.getArea(title);
										for (String s: areas) {
											if (s.trim().contains(aree.trim())) {
												area = s;
												if (municipality.equals("") || municipality.equals(" ")) municipality = "modena";
												break;
											}
										}
									}
								}

								if (municipality.contentEquals("castelfranco")) {
									municipality = "castelfranco emilia";
								}

								else if (municipality.contentEquals("castelnuovo")) {
									municipality = "castelnuovo rangone";
								}

								else if (municipality.contentEquals("castelvetro")) {
									municipality = "castelvetro di modena";
								}

								else if (municipality.contentEquals("concordia")) {
									municipality = "concordia sulla secchia";
								}

								else if (municipality.contentEquals("finale")) {
									municipality = "finale emilia";
								}

								else if (municipality.contentEquals("fiorano")) {
									municipality = "fiorano modenese";
								}

								else if (municipality.contentEquals("marano")) {
									municipality = "marano sul panaro";
								}

								else if (municipality.contentEquals("pavullo")) {
									municipality = "pavullo nel frignano";
								}

								else if (municipality.contentEquals("novi")) {
									municipality = "novi di modena";
								}

								else if (municipality.contentEquals("prignano")) {
									municipality = "noviprignano sulla secchia";
								}

								else if (municipality.contentEquals("san cesario")) {
									municipality = "san cesario sul panaro";
								}

								else if (municipality.contentEquals("san felice")) {
									municipality = "san felice sul panaro";
								}

								else if (municipality.contentEquals("savignano")) {
									municipality = "savigano sul panaro";
								}

								// determinare se � una notizia relativa ad un crimine o meno
								int notizia = 1;

								if (!municipality.equals(" ") || !municipality.equals("")) notizia = 0;

								TintPipeline pipeline = new TintPipeline();
								pipeline.loadDefaultProperties();
								pipeline.load();
								Annotation stanfordAnnotation = pipeline.runRaw(textBody.replace('\"', ' ').replace('\t', ' ').replace('\n', ' '));
								File file = new File("new_JSON.json");
								OutputStream outputstream = new FileOutputStream(file);
								InputStream stream = new ByteArrayInputStream(textBody.getBytes(StandardCharsets.UTF_8));
								Annotation annotation = pipeline.run(stream, outputstream, TintRunner.OutputFormat.JSON);
								outputstream.close();
								Object obj = new JSONParser().parse(new FileReader("new_JSON.json"));
								JSONObject jo = (JSONObject) obj;
								List < String > pos = new ArrayList < String > ();
								String ind = "";
								String word = "";
								JSONArray ja = (JSONArray) jo.get("sentences");
								Iterator itr = ja.iterator();
								while (itr.hasNext()) {
									Iterator < Map.Entry > itr1 = ((Map) itr.next()).entrySet().iterator();
									while (itr1.hasNext()) {
										Map.Entry pair = itr1.next();
										if (pair.getKey().equals("tokens")) {
											JSONArray tokens_array = (JSONArray) pair.getValue();
											Iterator itr2 = tokens_array.iterator();
											while (itr2.hasNext()) {
												Iterator < Map.Entry > itr3 = ((Map) itr2.next()).entrySet().iterator();
												String ner = new String();
												while (itr3.hasNext()) {
													Map.Entry pair_tokens = itr3.next();
													if (pair_tokens.getKey().equals("ner")/*&& pair_tokens.getValue().equals("LOC")*/)
														ner = pair_tokens.getValue().toString();
													if (pair_tokens.getKey().equals("word")) word = pair_tokens.getValue().toString();
												}
												if (ner.equals("LOC")) pos.add(word.toLowerCase());
												else pos.add(" ");
											}
										}
									}
								}
								System.out.println(pos);

								for (int j = 0; j < pos.size(); j++) {
									ind += pos.get(j) + " ";
								}
								ind = ind.replace("Comune", "");
								ind = ind.replace("comune", "");

								if (municipality.equals("modena") || municipality.equals("Modena")) {
									if (area.equals(" ") || area.equals("")) {
										for (String s: areas) {
											if (ind.contains(s)) {
												area = s;
											}
										}
									}
								}

								int cont = 0;
								String location = "";
								String lat = "",
								lon = "";
								String street = "";

								/*	if(municipality.equals("") || municipality.equals(" ")) {
									for(int z =0; z<pos.size();z++) {
										if(municipalities.contains(pos.get(z).toLowerCase()))
											municipality=pos.get(z);
									}
								}*/

								System.out.println(municipality);

								for (int k = 0; k < pos.size(); k++) {

									if (pos.get(k).equals("via") || pos.get(k).equals("viale") || pos.get(k).equals("strada") || pos.get(k).equals("piazza") || pos.get(k).equals("parco")) {

										location = pos.get(k) + " ";

										for (int j = k + 1; j < pos.size(); j++) {
											if (!pos.get(j).equals("via") && !pos.get(j).equals("viale") && !pos.get(j).equals("strada") && !pos.get(j).equals("piazza") && !pos.get(j).equals("parco")) {
												cont = j;
												location += pos.get(j) + " ";
											}

											else break;
										}

										System.out.println("Location: " + location);
										//estrazione coordinate
										//LAT/LON
										street = location.trim() + " " + municipality;
										if (street.trim() != "" && street.trim() != " ") {
											Map < String,
											Double > coords;
											coords = OpenStreetMapUtils.getInstance().getCoordinates(street);
											try {
												lat = coords.get("lat").toString().trim();
												lon = coords.get("lon").toString().trim();
											}
											catch(java.lang.NullPointerException e) {

											}
										}

										if (!lat.equals("") && !lon.equals("")) {
											address = location;
											break;
										} // se � positivo
										else {
											k = cont;
										} // se � negativo riparte saltando la via trovata ma che non ha fornito coordinate
									}

								}
								//-----------------------------------------------------
								System.out.println(address);
								System.out.println("latitude :" + lat);
								System.out.println("longitude:" + lon);

								// Trying to retrieve the stolen object, the object of aggression and so on
								String text = title + " " + description;
								String object = " ";
								if (crime_type.equals("furto")) object = Utils.TheftObject(text, stopwords);
								else if (crime_type.equals("aggressione")) object = Utils.AggressionObject(text, stopwords);
								else if (crime_type.equals("maltrattamento")) object = Utils.MistreatmentObject(text, stopwords);
								else if (crime_type.equals("droga")) object = Utils.DrugsObject(text, stopwords);
								else if (crime_type.equals("omicidio")) object = Utils.MurderObject(text, stopwords);
								else if (crime_type.equals("riciclaggio")) object = Utils.LaunderingObject(text, stopwords);
								else if (crime_type.equals("frode")) object = Utils.FraudObject(text, stopwords);
								else if (crime_type.equals("rapina")) object = Utils.RobberyObject(text, stopwords);
								else if (crime_type.equals("spaccio")) object = Utils.DDealingObject(text, stopwords);
								else if (crime_type.equals("truffa")) object = Utils.ScamsObject(text, stopwords);
								else if (crime_type.equals("sequestro")) object = Utils.SeizuresObject(text, stopwords);
								else if (crime_type.equals("evasione")) object = Utils.EvasionObject(text, stopwords);
								else if (crime_type.equals("violenza sessuale")) object = Utils.SAssoultObject(text, stopwords);

								if (!lat.equals("") && !lon.equals("")) {
									// tutte le informazioni estratte sono inserite nel database 'crime_news' nella tabella 'descr'
									PreparedStatement insertNews = conn.prepareStatement("INSERT INTO crime_news.news (url, title, description, text, municipality, area, address, date_publication, time_publication, geom, object, newspaper, tag, is_general, date_event) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, public.ST_GeomFromText(?,4326), ?, ?, ?, ?, ?) on conflict do nothing");
									insertNews.setString(1, link);
									insertNews.setString(2, title);
									insertNews.setString(3, description);
									insertNews.setString(4, textBody);
									insertNews.setString(5, municipality);
									insertNews.setString(6, area);
									insertNews.setString(7, address);
									insertNews.setDate(8, data);
									insertNews.setTime(9, time);
									insertNews.setString(10, "POINT(" + lon + " " + lat + ")");
									insertNews.setString(11, object);
									insertNews.setString(12, "Gazzetta di Modena");
									insertNews.setString(13, crime_type);
									insertNews.setInt(14, notizia);
									insertNews.setDate(15, event_date);
									insertNews.executeUpdate();
								}
								else {
									PreparedStatement insertNews = conn.prepareStatement("INSERT INTO crime_news.news (url, title, description, text, municipality, area, address, date_publication, time_publication, object, newspaper, tag, is_general, date_event) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) on conflict do nothing");
									insertNews.setString(1, link);
									insertNews.setString(2, title);
									insertNews.setString(3, description);
									insertNews.setString(4, textBody);
									insertNews.setString(5, municipality);
									insertNews.setString(6, area);
									insertNews.setString(7, address);
									insertNews.setDate(8, data);
									insertNews.setTime(9, time);
									insertNews.setString(10, object);
									insertNews.setString(11, "Gazzetta di Modena");
									insertNews.setString(12, crime_type);
									insertNews.setInt(13, notizia);
									insertNews.setDate(14, event_date);
									insertNews.executeUpdate();
								}

								insertedNews++;
								
								// entities
								String concatenation_for_entities = title + ' ' + description + ' ' + textBody;
								if (concatenation_for_entities.length() > 2) {
									Entity.extractEntities(link, concatenation_for_entities.replace('\"', ' ').replace('\t', ' ').replace('\n', ' '), conn);
								}
								
								System.out.println("-----------------------------------------------------------------------");

							}
							catch(NullPointerException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							catch(HttpStatusException e) {
								e.printStackTrace();
							}
							catch(IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

					} catch(PSQLException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
			}
			catch(Exception e){
				continue;
			}
		}

		

		}
		return insertedNews;
	}
	
	
}
