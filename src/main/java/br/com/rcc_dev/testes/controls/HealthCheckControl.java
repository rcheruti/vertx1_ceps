package br.com.rcc_dev.testes.controls;

import br.com.rcc_dev.testes.Utils;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthCheckControl {
  
  private static Logger LOGGER = LoggerFactory.getLogger(HealthCheckControl.class);
  
  private HealthChecks healthCheck;
  private String correiosURL = "https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente?wsdl";
  private WebClient http;
  
  public HealthCheckControl(){
    Vertx vertx = Vertx.vertx();
    this.http = WebClient.create(vertx);
    healthCheck = HealthChecks.create(vertx);
    
    healthCheck.register("online", 300, future ->{
      future.complete();
    });
    healthCheck.register("Correios online", 2000, future ->{
      long time1 = System.currentTimeMillis();
      http.getAbs(correiosURL).send(res -> {
        long time2 = System.currentTimeMillis();
        JsonObject obj = new JsonObject().put("executionTime", time2 - time1);
        Status status;
      
        if( res.failed() ) status = Status.KO(obj);
        else status = Status.OK(obj);
      
        future.complete(status);
      });
    });
  }
  
  // ---------------------------------
  
  public Future<JsonObject> checkHealth(){
    Utils.requests.mark();
    Future<JsonObject> future = Future.future();
    healthCheck.invoke( json -> future.complete(json) );
    return future;
  }
  
  
  
}
