import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
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

public class Entity {

	String url_news;
	String entity_word;
	String entity_type;
	String uri;
	Integer frequency;
	
	public Entity() {
		
	}
	
	public Entity(String url_news, String entity_word, String entity_type) {
		this.url_news = url_news;
		this.entity_word = entity_word;
		this.entity_type = entity_type;
	}
	
	public Entity(String url_news, String entity_word, String entity_type, String uri, Integer frequency) {
		this.url_news = url_news;
		this.entity_word = entity_word;
		this.entity_type = entity_type;
		this.uri = uri;
		this.frequency = frequency;
	}
	
	public Entity(String url_news, String entity_word, String entity_type, Integer frequency) {
		this.url_news = url_news;
		this.entity_word = entity_word;
		this.entity_type = entity_type;
		this.frequency = frequency;
	}
	
	static public void extractEntities(String url_news, String text, Connection conn) throws IOException, ParseException, SQLException {
		
		ArrayList<Entity> entities = new ArrayList<Entity>();
		ArrayList<Entity> entities_without_duplicate = new ArrayList<Entity>();
		
		TintPipeline pipeline = new TintPipeline();
		pipeline.loadDefaultProperties();
		pipeline.load();
		Annotation stanfordAnnotation = pipeline.runRaw(text);
		File file = new File("new_JSON.json");
		OutputStream outputstream = new FileOutputStream(file);
		InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
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
					long end_previous = 0;
					long begin_current = 0;
					long end_current = 0;
					while (itr2.hasNext()) {
						Iterator < Map.Entry > itr3 = ((Map) itr2.next()).entrySet().iterator();
						String ner = new String();
						
						while (itr3.hasNext()) {
							Map.Entry pair_tokens = itr3.next();
							if (pair_tokens.getKey().equals("ner"))
								ner = pair_tokens.getValue().toString();
							if (pair_tokens.getKey().equals("word"))
								word = pair_tokens.getValue().toString();
							if (pair_tokens.getKey().equals("characterOffsetBegin"))
								begin_current = (long) pair_tokens.getValue();
							if (pair_tokens.getKey().equals("characterOffsetEnd"))
								end_current = (long) pair_tokens.getValue();
						}
						
						String entity_type = ner;
						String entity_word = word.toLowerCase();
						
						if (entity_type.equals("LOC") || entity_type.equals("ORG") || entity_type.equals("PER")) {
							if (entities.size()-1>=0 && ner.equals(entities.get(entities.size()-1).entity_type) && begin_current == end_previous + 1) {
								entities.get(entities.size()-1).entity_word = entities.get(entities.size()-1).entity_word + " " + entity_word;
							} else {
								Entity entity = new Entity(url_news, entity_word, entity_type, 0);
								entities.add(entity);
							}
						}
						end_previous = end_current;
					}
				}
			}
		}
		
		Boolean found = false;
		for (int i = 0; i<entities.size(); i++) {
			found = false;
			for (int j = 0; j<entities_without_duplicate.size(); j++) {
				if (compareEntities(entities_without_duplicate.get(j), entities.get(i))) {
					entities_without_duplicate.get(j).frequency++;
					found = true;
					break;
				}
			}
			if (found == false)
				entities_without_duplicate.add(entities.get(i));
		}

		// Storing the entities into the database
		for(Entity e: entities_without_duplicate) {
			PreparedStatement insertNews = conn.prepareStatement("INSERT INTO crime_news.entity (url_news, entity, entity_type, uri, frequency) VALUES (?, ?, ?, ?, ?) on conflict do nothing");
			insertNews.setString(1, e.url_news);
			insertNews.setString(2, e.entity_word);
			insertNews.setString(3, e.entity_type);
			insertNews.setString(4, e.uri);
			insertNews.setInt(5, e.frequency);
			insertNews.executeUpdate();
		}

	}

	private static boolean compareEntities(Entity entity1, Entity entity2) {
		if(entity1.url_news.equals(entity2.url_news)) {
			if(entity1.entity_word.equals(entity2.entity_word)) {
				if(entity1.entity_type.equals(entity2.entity_type)) {
					return true;
				}
			}
		}
		return false;
	}

}
