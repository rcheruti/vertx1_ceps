package br.com.rcc_dev.testes;


public class App{
  public static void main( String[] args ){
    Vertx vertx = Vertx.vertx() ;
    ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
  }
}
