package net.aplayfullife.identity;

import java.io.BufferedReader; 
import java.net.HttpURLConnection; 
import java.net.URL; 
import java.net.URLEncoder; 
import org.json.simple.*;

public class Thesaurus { 
  final String endpoint = "http://thesaurus.altervista.org/thesaurus/v1"; 

  public JSONArray SendRequest(String word) { 
    String language = "en_US";
    String output = "json";
    String key = "VmMAF4xvnkWFt1IFwMBs";
    try { 
      URL serverAddress = new URL(endpoint + "?word="+URLEncoder.encode(word, "UTF-8")+"&language="+language+"&key="+key+"&output="+output); 
      HttpURLConnection connection = (HttpURLConnection)serverAddress.openConnection(); 
      connection.connect(); 
      int rc = connection.getResponseCode(); 
      if (rc == 200) { 
        String line = null; 
        BufferedReader br = new BufferedReader(new java.io.InputStreamReader(connection.getInputStream())); 
        StringBuilder sb = new StringBuilder(); 
        while ((line = br.readLine()) != null) 
          sb.append(line + '\n'); 
        JSONObject obj = (JSONObject) JSONValue.parse(sb.toString()); 
        JSONArray array = (JSONArray)obj.get("response");
		return array;
      } else System.out.println("HTTP error:"+rc); 
      connection.disconnect(); 
    } catch (java.net.MalformedURLException e) { 
      e.printStackTrace(); 
    } catch (java.net.ProtocolException e) { 
      e.printStackTrace(); 
    } catch (java.io.IOException e) { 
      e.printStackTrace(); 
    } 
  } 
}
