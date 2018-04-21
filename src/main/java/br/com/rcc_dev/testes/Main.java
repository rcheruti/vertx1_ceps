package br.com.rcc_dev.testes;


import br.com.rcc_dev.testes.borders.GraphqlBorder;
import br.com.rcc_dev.testes.borders.RestBorder;
import br.com.rcc_dev.testes.borders.SoapBorder;
import br.com.rcc_dev.testes.startup.DatabaseControl;
import br.com.rcc_dev.testes.startup.GuiceModule;
import br.com.rcc_dev.testes.startup.MetricsControl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

import java.io.IOException;

public class Main {
  
  public static void main( String[] args ){
  
    MetricsControl.start();
    DatabaseControl.start();
    Injector injector = Guice.createInjector(Stage.PRODUCTION, new GuiceModule());
  
    JsonObject config = loadConfig() ;
    Vertx vertx = Vertx.vertx(new VertxOptions(config)) ;
  
    startServer(vertx, config, injector);
    
  }
  
  
  
  
  public static void startServer(Vertx vertx, JsonObject config, Injector injector){
    // starting HTTP server
    Router router = Router.router(vertx) ;
    router.route().handler(BodyHandler.create());
    router.mountSubRouter("/",          injector.getInstance(RestBorder.class).createRouter(vertx));
    router.mountSubRouter("/soap",      injector.getInstance(SoapBorder.class).createRouter(vertx));
    router.mountSubRouter("/graphql",   injector.getInstance(GraphqlBorder.class).createRouter(vertx));
  
    HttpServer server = vertx.createHttpServer() ;
    server.requestHandler( router::accept ).listen(Utils.jInt(config, "http.port"), res ->{
      System.out.println("Deploy Main: " + res.succeeded());
    });
  }
  
  public static JsonObject loadConfig(){
    // reading configs for execution
    String configYAML = Utils.fileContentsJar("config.yaml");
    JsonObject config = null;
    try {
      JsonNode node = new YAMLMapper().readTree(configYAML);
      config = new JsonObject( node.toString() );
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println("Config: " + config.encodePrettily());
    return config;
  }
  
}
