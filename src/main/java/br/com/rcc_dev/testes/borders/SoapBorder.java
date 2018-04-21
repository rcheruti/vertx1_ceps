package br.com.rcc_dev.testes.borders;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public class SoapBorder {
  
  // TODO: criar as bordas SOAP
  public Router createRouter(Vertx vertx){
    Router router = Router.router(vertx);
    
    return router;
  }
  
}
