import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	/*
	 * Metodo per caricare il contenuto di un file di testo in un ArrayList
	 */
	static public List<String> load_list(String filename) throws FileNotFoundException, IOException {
		List<String> list = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	list.add(line.trim());
		    }
		}
		return list;
	}
	
	//metodo per provare a trovare il comune 	
	static public String getComuneStringa(String text) throws FileNotFoundException, IOException {
		
		ArrayList a= new ArrayList();		
	    String line = text;
	    String comune =" ";
		line = line.toLowerCase();
		line = line.replaceAll("[0-9'.:,;_()\\��\\-<>]", "");
		String[] r = line.split("\\s");
		String frase="";
		for (int x=0; x<r.length; x++){
	    	if(r[x].endsWith(".") || r[x].endsWith(",") || r[x].endsWith(":") || r[x].endsWith("�") || r[x].endsWith("�") ){
	    		a.add(r[x]);
	    		break;
	    	}
	    	else a.add(r[x]);
	    }
	    for(int z=0; z<a.size();z++) {
	    	frase += (a.get(z)+" ");
	    }
	    return frase;	
	}

	/*
	 * Method which tries to retrieve the stolen object
	 */
	static public String TheftObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("furto") || ss.get(i).equals("furti") 
					|| ss.get(i).equals("ladro") || ss.get(i).equals("ladra") || ss.get(i).equals("ladri")
					|| ss.get(i).equals("deruba") || ss.get(i).equals("derubato")
					|| ss.get(i).equals("ruba") || ss.get(i).equals("rubano")
					|| ss.get(i).equals("rubato") || ss.get(i).equals("rubata")
					|| ss.get(i).equals("rubati") || ss.get(i).equals("rubate")
					|| ss.get(i).equals("rubando")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	
	static public String AggressionObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("aggressione") || ss.get(i).equals("agressioni") 
					|| ss.get(i).equals("violenza") || ss.get(i).equals("violenze") || ss.get(i).equals("aggredita") || ss.get(i).equals("aggredito") 
					|| ss.get(i).equals("malmenato") || ss.get(i).equals("malmenata")
					|| ss.get(i).equals("colpito") || ss.get(i).equals("colpita")
					|| ss.get(i).equals("colpiti") || ss.get(i).equals("attaccato")
					|| ss.get(i).equals("attaccata") || ss.get(i).equals("attaccati")
					){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	/*
	 * Metodo che estrae il tipo di oggetto rubato
	 */
	static public String MistreatmentObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("maltrattamento") || ss.get(i).equals("maltrattamenti") 
					|| ss.get(i).equals("agressione") || ss.get(i).equals("aggressioni") || ss.get(i).equals("violenza") || ss.get(i).equals("violenze") ||
					ss.get(i).equals("percosse")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	/*
	 * Metodo che estrae il tipo di oggetto rubato
	 */
	static public String DrugsObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("spaccio") || ss.get(i).equals("spacci")
					|| ss.get(i).equals("spacciare") || ss.get(i).equals("spacciatore")
					|| ss.get(i).equals("droga") || ss.get(i).equals("droghe")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	static public String MurderObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("omicidio") || ss.get(i).equals("omicidi") 
					|| ss.get(i).equals("aggredito") || ss.get(i).equals("aggrediti") || ss.get(i).equals("colpito") || ss.get(i).equals("colpita") ||
					ss.get(i).equals("colpiti")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	static public String LaunderingObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("riciclaggio") || ss.get(i).equals("denaro") 
					|| ss.get(i).equals("ripiego") || ss.get(i).equals("illecito")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	static public String FraudObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("frode") || ss.get(i).equals("frodi") 
					|| ss.get(i).equals("truffa") || ss.get(i).equals("truffe") || ss.get(i).equals("raggiro") || ss.get(i).equals("raggiri")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	static public String RobberyObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("rapina") || ss.get(i).equals("rapine") 
					|| ss.get(i).equals("rubati") || ss.get(i).equals("rubato") || ss.get(i).equals("aggredita") || ss.get(i).equals("rapinatore") ||
					ss.get(i).equals("rapinatori") || ss.get(i).equals("rubando")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	static public String DDealingObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("spaccio") || ss.get(i).equals("spacci")
					|| ss.get(i).equals("spacciare") || ss.get(i).equals("spacciatore")
					|| ss.get(i).equals("droga") || ss.get(i).equals("droghe")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	
	static public String ScamsObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("truffa") || ss.get(i).equals("truffe") 
					|| ss.get(i).equals("truffato") || ss.get(i).equals("truffati") || ss.get(i).equals("raggirato") || ss.get(i).equals("raggirati")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	static public String SeizuresObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("sequestro") || ss.get(i).equals("sequestri") 
					|| ss.get(i).equals("sequestrato") || ss.get(i).equals("sequestrati") || ss.get(i).equals("rapito") || ss.get(i).equals("rapiti") ||
					ss.get(i).equals("rapita") || ss.get(i).equals("sequestrata")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}

	
	static public String EvasionObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("evasione") || ss.get(i).equals("evasioni") 
					|| ss.get(i).equals("evaso") || ss.get(i).equals("evasi") || ss.get(i).equals("scappato") || ss.get(i).equals("evasa") ||
					ss.get(i).equals("scappato") || ss.get(i).equals("scappati")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}

	static public String SAssoultObject(String text, List<String> stopwords) throws FileNotFoundException, IOException{
		String object = "";
		text = text.toLowerCase();
		text = text.replaceAll("[0-9'.:,;_()\\��\\-]", " ");
		String[] s = text.split(" ");
		List<String> ss = new ArrayList<String>();
		for(int i=0; i<s.length; i++){
			if(!s[i].equals(" ") && !s[i].equals(""))
				ss.add(s[i].trim());
		}
		ss.removeAll(stopwords);
		for(int i=0; i<ss.size(); i++){
			if(ss.get(i).equals("aggressione") || ss.get(i).equals("agressioni") 
					|| ss.get(i).equals("violenza") || ss.get(i).equals("violenze") || ss.get(i).equals("aggredita") || ss.get(i).equals("aggredito") ||
					ss.get(i).equals("violentata") || ss.get(i).equals("violentato")){
				i++;
				if(i<ss.size()){
					object = ss.get(i);
					return object;
				}
			}
		  }
		return object;
	}
	
	// metodo per trovare il comune primo caso
	static public String getComune(String text) throws FileNotFoundException, IOException{
		ArrayList a= new ArrayList();
	    String line = text;
	    String comune =" ";
		line = line.toLowerCase();
		line = line.replaceAll("[0-9';\\-()//��<>]", " ");		
		String[] r = line.split("\\s");	
		for (int x=0; x<r.length; x++){
	    	if(r[x].endsWith(".") || r[x].endsWith(",") || r[x].endsWith(":") || r[x].endsWith("�") || r[x].endsWith("�") ){
	    		a.add(r[x]);
	    		break;
	    	}
	    	else a.add(r[x]);
	    }
		
		if(a.size()==1) {
			comune = String.valueOf(a.get(0));
			comune= comune.replace(".", "");
			comune= comune.replace(",", "");
			comune= comune.replace(":", "");
			comune= comune.replace("�", "");
			comune= comune.replace("�", "");
			return comune;				
		}
		
		else if(a.size()==2) {
			comune = String.valueOf(a.get(0))+" "+String.valueOf(a.get(1));	
			comune= comune.replace(".", "");
			comune= comune.replace(",", "");
			comune= comune.replace(":", "");
			comune= comune.replace("�", "");
			comune= comune.replace("�", "");
			return comune;
		}
		
		else if(a.size()==3) {
			comune = String.valueOf(a.get(0))+" "+String.valueOf(a.get(1))+" "+String.valueOf(a.get(2));	
			comune= comune.replace(".", "");
			comune= comune.replace(",", "");
			comune= comune.replace(":", "");
			comune= comune.replace("�", "");
			comune= comune.replace("�", "");
			return comune;
		}
		else
			return comune;
	}
	
	// metodo per trovare l'area 		
	static public String getArea(String text) throws FileNotFoundException, IOException{
		ArrayList a= new ArrayList();
	    String line = text;
	    String area =" ";
		line = line.toLowerCase();
		line = line.replaceAll("[0-9';\\()//��<>]\\-", " ");
		String[] r = line.split("\\s");
		
	    for (int x=0; x<r.length; x++){
	    	if(r[x].endsWith(".") || r[x].endsWith(",") || r[x].endsWith(":") || r[x].endsWith("�") || r[x].endsWith("�") ){
	    		a.add(r[x]);
	    		break;
	    	}
	    	else a.add(r[x]);
	    }
		
		if(a.size()==1) {
			area = String.valueOf(a.get(0));
			area= area.replace(".", "");
			area= area.replace(",", "");
			area= area.replace(":", "");
			area= area.replace("�", "");
			area= area.replace("�", "");
			return area;
		}
		else if(a.size()==2) {
			area = String.valueOf(a.get(0))+" "+String.valueOf(a.get(1));	
			area= area.replace(".", "");
			area= area.replace(",", "");
			area= area.replace(":", "");
			area= area.replace("�", "");
			area= area.replace("�", "");
			return area;				
		}
		else if(a.size()==3) {
			area = String.valueOf(a.get(0))+" "+String.valueOf(a.get(1))+" "+String.valueOf(a.get(2));	
			area= area.replace(".", "");
			area= area.replace(",", "");
			area= area.replace(":", "");
			area= area.replace("�", "");
			area= area.replace("�", "");
			return area;
		}
				
		else if(a.size()==4) {
			area = String.valueOf(a.get(0))+" "+String.valueOf(a.get(1))+" "+String.valueOf(a.get(2))+" "+String.valueOf(a.get(3));	
			area= area.replace(".", "");
			area= area.replace(",", "");
			area= area.replace(":", "");
			area= area.replace("�", "");
			area= area.replace("�", "");
			return area;
		}
		else
			return area;
	}
	
}
