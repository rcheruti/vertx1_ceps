package br.com.rcc_dev.testes;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
  
  
  public static final MetricRegistry metrics = new MetricRegistry();
  public static final Meter requests = metrics.meter("requests");
  
  public static final String charUTF_8 = "UTF-8";
  public static final String charISO_8859_1 = "ISO-8859-1";
  
  
  public static String fileContentsJar(String name){
    String lf = System.getProperty("line.separator");
    InputStream in = Utils.class.getClassLoader().getResourceAsStream(name);
    return new BufferedReader( new InputStreamReader(in)).lines().reduce("", (a, b) -> a + lf + b);
  }
  
  public static int jInt(JsonObject obj, String path){
    String[] paths = path.split("\\.");
    JsonObject node = obj;
    for( int i = 0; i < paths.length -1; i++ ){
      node = node.getJsonObject(paths[i]);
    }
    return node.getInteger( paths[paths.length -1] );
  }
  
}
