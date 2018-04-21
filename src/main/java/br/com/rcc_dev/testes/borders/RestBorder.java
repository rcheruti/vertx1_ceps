package br.com.rcc_dev.testes.borders;

import br.com.rcc_dev.testes.controls.CepControl;
import br.com.rcc_dev.testes.controls.HealthCheckControl;
import br.com.rcc_dev.testes.entities.CepOutput;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import java.io.IOException;
import java.io.StringWriter;

public class RestBorder {
  
  @Inject
  private CepControl cepControl;
  
  @Inject
  private HealthCheckControl healthCheckControl;
  
  // --------------------------------------------------------
  
  public Router createRouter(Vertx vertx){
    Router router = Router.router(vertx);
  
    router.get("/cep/:cep").handler(ctx -> {
      String cep = ctx.pathParam("cep");
      JsonObject json = new JsonObject().put("cep", cep);
      Future<CepOutput> future = cepControl.findCEP(cep);
      
      future.setHandler( res -> writeResponse(ctx, res) );
    }).failureHandler( RestBorder::failure );
  
    router.route("/health").handler(ctx -> {
      Future<JsonObject> future = healthCheckControl.checkHealth();
  
      future.setHandler( res -> writeResponse(ctx, res) );
    }).failureHandler( RestBorder::failure );
    
    return router;
  }
  
  // ---------------------------------------------------------
  
  private static void failure(RoutingContext ctx){
    ctx.response().putHeader("Content-type", "text/plain").setStatusCode(500)
        .end("Some error occured: " + ctx.failure().getMessage());
  }
  
  private static void writeResponse(RoutingContext ctx, AsyncResult<? extends Object> res){
    HttpServerResponse response = ctx.response();
    if( res.failed() ){
      response.setStatusCode(500).end( res.cause().getMessage() );
    }else{
      String mode = ctx.request().getParam("mode");
      Object respObj = res.result();
      String respStr = writeResponseForMode(mode, respObj, response);
      
      response.setStatusCode( 200 ).end( respStr );
    }
  }
  
  private static String writeResponseForMode(String mode, Object respObj, HttpServerResponse response){
    if( mode == null ) mode = "";
    StringWriter stream = new StringWriter();
    try {
      switch (mode.toLowerCase()) {
        case "xml":
          response.putHeader("Content-type", "application/xml");
          new XmlMapper().writeValue(stream, respObj);
          break;
        case "yaml":
          response.putHeader("Content-type", "application/yaml");
          new YAMLMapper().writeValue(stream, respObj);
          break;
        default:
          response.putHeader("Content-type", "application/json");
          return JsonObject.mapFrom(respObj).toString();
      }
    }catch (IOException ex){
      return "error";
    }
    return stream.toString();
  }
  
}
