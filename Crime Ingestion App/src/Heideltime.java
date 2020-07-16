import java.io.FileWriter;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import de.unihd.dbs.heideltime.standalone.DocumentType;
import de.unihd.dbs.heideltime.standalone.HeidelTimeStandalone;
import de.unihd.dbs.heideltime.standalone.OutputType;
import de.unihd.dbs.uima.annotator.heideltime.HeidelTime;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;

public class Heideltime {

    //private static final Logger LOGGER = LoggerFactory.getLogger(HeidelTime.class);

    public static boolean validateJavaDate(String strDate) {
    	
	 	if (strDate.trim().equals("")) {
	 	    return false;
	 	}
	 	
 	    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM-dd");
 	    sdfrmt.setLenient(false);
 	    
 	    try {
 	    	java.util.Date javaDate = sdfrmt.parse(strDate);
 	    }
 	    catch (ParseException e) {
 	        return false;
 	    }
 	    
 	    return true;
    }
    
    public static boolean validateJavaDateWithoutDay(String strDate) {
    	
	 	if (strDate.trim().equals("")) {
	 	    return false;
	 	}
	 	
 	    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy-MM");
 	    sdfrmt.setLenient(false);
 	    
 	    try {
 	    	java.util.Date javaDate = sdfrmt.parse(strDate);
 	    }
 	    catch (ParseException e) {
 	        return false;
 	    }
 	    
 	    return true;
    }
    
    public static boolean validateJavaDateWithTime(String strDate) {
    	
	 	if (strDate.trim().equals("")) {
	 	    return false;
	 	}
	 	
 	    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy.MM.dd'T'HH:MM");
 	    sdfrmt.setLenient(false);
 	    
 	    try {
 	        java.util.Date javaDate = sdfrmt.parse(strDate);
 	    }
 	    catch (ParseException e) {
 	        return false;
 	    }
 	    
 	    return true;
    }
 	
    public static boolean validateJavaDateWithTimeWithoutDay(String strDate) {
    	
	 	if (strDate.trim().equals("")) {
	 	    return false;
	 	}
	 	
 	    SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy.MM'T'HH:MM");
 	    sdfrmt.setLenient(false);
 	    
 	    try {
 	    	java.util.Date javaDate = sdfrmt.parse(strDate);
 	    }
 	    catch (ParseException e) {
 	        return false;
 	    }
 	    
 	    return true;
    }

    private static boolean isBeforeOrEqual(String before, String after) throws ParseException {
		//controllo che before sia una data precedente o uguale a after
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	java.util.Date before_date = sdf.parse(before);
    	java.util.Date after_date = sdf.parse(after);
    	long before_millis = before_date.getTime();
    	long after_millis = after_date.getTime();
    	
    	if(before_millis <= after_millis)
    		return true;
		return false;
	}
    
    private static long dayDistance(String date1, String date2) throws ParseException {
		
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	java.util.Date date1_parsed = sdf.parse(date1);
    	java.util.Date date2_parsed = sdf.parse(date2);
    	
    	long diffInMillies = Math.abs(date1_parsed.getTime() - date2_parsed.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
		return diff;
	}
    
	public static Date findEventDate(String text, Date date_sql) {
        
        HeidelTimeStandalone heidelTimeStandalone = new HeidelTimeStandalone(Language.ITALIAN, DocumentType.NEWS, OutputType.XMI, "config.props");

    	int cont = 0;
    	int found = 0;
    	int not_found = 0;
    	
	    java.util.Date date = new java.util.Date(date_sql.getTime());
	    long millis = date.getTime();
	
	    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
        String date_string = dateFormat.format(date);  
        
        try {
        	String process = heidelTimeStandalone.process(text, new Date(millis));
        	
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            InputSource inputFile = new InputSource(new StringReader(process));
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("heideltime:Timex3");
            
            HashMap<String, Integer> hmap = new HashMap<String, Integer>();
            
            for (int temp = 0; temp < nList.getLength(); temp++) {
               Node nNode = nList.item(temp);
               if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                  Element eElement = (Element) nNode;
                  String tmp = eElement.getAttribute("timexValue");
                  if(eElement.getAttribute("timexType").equals("DATE")) {
                	  if (validateJavaDate(eElement.getAttribute("timexValue")) && 
                		  isBeforeOrEqual(eElement.getAttribute("timexValue"), date_string)) {
		                  int begin = Integer.parseInt(eElement.getAttribute("begin"));
		                  int end = Integer.parseInt(eElement.getAttribute("end"));
		                  String word = text.substring(begin, end).toLowerCase();
		                  if(hmap.get(eElement.getAttribute("timexValue"))!=null) {
		                	  int n = hmap.get(eElement.getAttribute("timexValue"));
		                	  hmap.put(eElement.getAttribute("timexValue"), ++n);
		                  }
		                  else
		                	  hmap.put(eElement.getAttribute("timexValue"), 1);
		                  
		                  System.out.println(word + "\n");
		                  System.out.println("timexValue : " + eElement.getAttribute("timexValue") + "\n");
                	  }
                	  else if(validateJavaDateWithoutDay(eElement.getAttribute("timexValue"))) {
                		  String new_date = eElement.getAttribute("timexValue") + "-01";
                		  if(isBeforeOrEqual(new_date, date_string)) {
                			  int begin = Integer.parseInt(eElement.getAttribute("begin"));
    		                  int end = Integer.parseInt(eElement.getAttribute("end"));
    		                  String word = text.substring(begin, end).toLowerCase();
    		                  if(hmap.get(new_date)!=null) {
    		                	  int n = hmap.get(new_date);
    		                	  hmap.put(new_date, ++n);
    		                  }
    		                  else
    		                	  hmap.put(new_date, 1);
    		                  
    		                  System.out.println(word + "\n");
    		                  System.out.println("timexValue : " + new_date + "\n");
                		  }
                	  }
                	  else if(validateJavaDateWithTime(eElement.getAttribute("timexValue"))) {
                		  String new_date = eElement.getAttribute("timexValue").split("T")[0].replace(".", "-");
                		  if(isBeforeOrEqual(new_date, date_string)) {
                			  int begin = Integer.parseInt(eElement.getAttribute("begin"));
    		                  int end = Integer.parseInt(eElement.getAttribute("end"));
    		                  String word = text.substring(begin, end).toLowerCase();
    		                  if(hmap.get(new_date)!=null) {
    		                	  int n = hmap.get(new_date);
    		                	  hmap.put(new_date, ++n);
    		                  }
    		                  else
    		                	  hmap.put(new_date, 1);
    		                  
    		                  System.out.println(word + "\n");
    		                  System.out.println("timexValue : " + new_date + "\n");
                		  }
                	  }
                	  else if(validateJavaDateWithTimeWithoutDay(eElement.getAttribute("timexValue"))) {
                		  String new_date = eElement.getAttribute("timexValue").split("T")[0].replace(".", "-") + "-01";
                		  if(isBeforeOrEqual(new_date, date_string)) {
                			  int begin = Integer.parseInt(eElement.getAttribute("begin"));
    		                  int end = Integer.parseInt(eElement.getAttribute("end"));
    		                  String word = text.substring(begin, end).toLowerCase();
    		                  if(hmap.get(new_date)!=null) {
    		                	  int n = hmap.get(new_date);
    		                	  hmap.put(new_date, ++n);
    		                  }
    		                  else
    		                	  hmap.put(new_date, 1);
    		                  
    		                  System.out.println(word + "\n");
    		                  System.out.println("timexValue : " + new_date + "\n");
                		  }
                	  }
                	  else if(eElement.getAttribute("timexValue").equals("PRESENT_REF")) {
                		  String new_date = date_string;
            			  int begin = Integer.parseInt(eElement.getAttribute("begin"));
		                  int end = Integer.parseInt(eElement.getAttribute("end"));
		                  String word = text.substring(begin, end).toLowerCase();
		                  if(hmap.get(new_date)!=null) {
		                	  int n = hmap.get(new_date);
		                	  hmap.put(new_date, ++n);
		                  }
		                  else
		                	  hmap.put(new_date, 1);
		                  
		                  System.out.println(word + "\n");
		                  System.out.println("timexValue : " + new_date + "\n");
                	  }
                	  
                  }
                  else if(eElement.getAttribute("timexType").equals("TIME") && 
                		  validateJavaDate(eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3)) && 
                		  isBeforeOrEqual(eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3), date_string)) {
	                  int begin = Integer.parseInt(eElement.getAttribute("begin"));
	                  int end = Integer.parseInt(eElement.getAttribute("end"));
	                  String word = text.substring(begin, end).toLowerCase();
	                  if(hmap.get(eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3))!=null) {
	                	  int n = hmap.get(eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3));
	                	  hmap.put(eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3), ++n);
	                  }
	                  else
	                	  hmap.put(eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3), 1);
	                  
	                  System.out.println(word + "\n");
	                  System.out.println("timexValue : " + eElement.getAttribute("timexValue").substring(0, eElement.getAttribute("timexValue").length()-3) + "\n");
                  }
               }
            }
            
            System.out.println("Checking results...\n");
            
            int max = 0;
            //String max_date = "";
            Set set = hmap.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) {
               Map.Entry mentry = (Map.Entry)iterator.next();
               //System.out.println("key is: "+ mentry.getKey() + " & Value is: " + mentry.getValue().toString() + "\n");
               if(Integer.parseInt(mentry.getValue().toString()) > max) {
            	   max = Integer.parseInt(mentry.getValue().toString());
            	   //max_date = mentry.getKey().toString();
               }
            }
            
            List<String> list_max = new ArrayList<String>();
            Set set_max = hmap.entrySet();
            Iterator iterator_max = set_max.iterator();
            while(iterator_max.hasNext()) {
               Map.Entry mentry_max = (Map.Entry)iterator_max.next();
               if(Integer.parseInt(mentry_max.getValue().toString()) == max) {
            	   list_max.add(mentry_max.getKey().toString());
               }
            }
            
            long min_days = 100;
            String min = "";
            for (String l: list_max) {
            	//calcolo la distanza di giorni da date_string
            	long days = dayDistance(date_string, l);
            	if(days<min_days) {
            		min_days = days;
            		min = l;
            	}
            }
            
            if(!min.equals("")) {
            	found++;
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            	java.util.Date min_date = sdf.parse(min);
            	java.sql.Date min_date_sql = new java.sql.Date(min_date.getTime());
            	System.out.println("Max probability and most near date: " + min_date_sql.toString() + "\n");
            	return min_date_sql;
            }
            else {
            	not_found++;
            }
            
         } catch (Exception e) {
            e.printStackTrace();
         }
        cont++;
    	
		return null;
	}

}
