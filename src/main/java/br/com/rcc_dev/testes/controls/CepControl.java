package br.com.rcc_dev.testes.controls;

import br.com.rcc_dev.testes.Utils;
import br.com.rcc_dev.testes.entities.CepOutput;
import br.com.rcc_dev.testes.entities.database.Cep;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.ebean.Ebean;
import io.ebean.ExpressionFactory;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CepControl {
  
  private static Logger LOGGER = LoggerFactory.getLogger(CepControl.class);
  
  private String correiosURL = "https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente";
  private String consultaCepTemplate = Utils.fileContentsJar("consultaCEP.xml");
  private WebClient http;

  public CepControl(){
    this.http = WebClient.create(Vertx.vertx());
  }
  
  // ---------------------------------
  
  /**
   * This method will ask Correios about address info related with the CEP (Brazilian Zip Code) informed.
   *
   * @param cep CEP that you want info from Correios
   * @return Info about the CEP
   */
  public Future<CepOutput> findCEP(String cep){
    Utils.requests.mark();
  
    // get CEP without non-numeric chars
    cep = cep.replaceAll("\\D+", "");
    if( cep == null || cep.isEmpty() ) return Future.failedFuture("The 'cep' parameter is required for the search");
    
    
    // SOAP request to Correios and Future to response
    String reqStr = this.consultaCepTemplate.replace("$cep", cep);
    Future<CepOutput> future = Future.future();
    
    ExpressionFactory ef = Ebean.getExpressionFactory();
    List<Cep> cepList = Ebean.find(Cep.class).where(ef.eq("cep", cep)).findList();
    if( !cepList.isEmpty() ){
      CepOutput output = new CepOutput( cepList.get(0) );
      output.setDatabase(true);
      future.complete( output );
      return future;
    }
    
    // HTTP request to Correios
    http.postAbs(this.correiosURL).ssl(true).sendBuffer(Buffer.buffer(reqStr), res ->{
      if( res.succeeded() ){
        String body = res.result().bodyAsString(Utils.charISO_8859_1);
        try {
          JsonNode json = new XmlMapper().readTree(body);
          if( json.findPath("Body").has("Fault") ){
            future.fail("That was not possible to find your CEP");
          }else{
            JsonObject jsonResp = new JsonObject( Json.mapper.convertValue(json.findPath("Body").findPath("consultaCEPResponse").findPath("return"), Map.class) );
            Cep cepObj = new Cep();
            cepObj.setBairro(         jsonResp.getString("bairro") );
            cepObj.setCep(            jsonResp.getString("cep") );
            cepObj.setCidade(         jsonResp.getString("cidade") );
            cepObj.setComplemento(    jsonResp.getString("complemento") );
            cepObj.setComplemento2(   jsonResp.getString("complemento2") );
            cepObj.setEndereco(       jsonResp.getString("end") );
            cepObj.setUf(             jsonResp.getString("uf") );
            
            Ebean.save(cepObj); // save on database
            
            future.complete( new CepOutput( cepObj ) );
          }
        } catch (IOException e) {
          future.fail(e);
        }
      }else{
        future.fail(res.cause());
      }
    });
    
    return future;
  }
  
  
  /**
   * This method will ask Correios about address info related with the CEP (Brazilian Zip Code) informed.
   *
   * @param params Required parameters to get CEPs info from Correios
   * @return
   */
  @Deprecated
  public Future<JsonObject> apply(JsonObject params){
    Utils.requests.mark();
    
    JsonObject response = this.checkParameters(params);
    if( response != null ) return Future.succeededFuture(response);
    
    // get CEP without non-numeric chars
    String cep = params.getString("cep").replaceAll("\\D+", "");
    
    // SOAP request to Correios and Future to response
    String reqStr = this.consultaCepTemplate.replace("$cep", cep);
    Future<JsonObject> future = Future.future();
  
    ExpressionFactory ef = Ebean.getExpressionFactory();
    List<Cep> cepList = Ebean.find(Cep.class).where(ef.eq("cep", cep)).findList();
    if( !cepList.isEmpty() ){
      future.complete(JsonObject.mapFrom( cepList.get(0) ).put("database", true));
      return future;
    }
    
    // HTTP request to Correios
    http.postAbs(this.correiosURL).ssl(true).sendBuffer(Buffer.buffer(reqStr), res ->{
      if( res.succeeded() ){
        String body = res.result().bodyAsString(Utils.charISO_8859_1);
        try {
          JsonNode json = new XmlMapper().readTree(body);
          if( json.findPath("Body").has("Fault") ){
            future.complete(new JsonObject().put("status", 400).put("msg", "Não foi possível encontrar o CEP informado"));
          }else{
            JsonObject jsonResp = new JsonObject( Json.mapper.convertValue(json.findPath("Body").findPath("consultaCEPResponse").findPath("return"), Map.class) );
            Cep cepObj = new Cep();
            cepObj.setBairro( jsonResp.getString("bairro") );
            cepObj.setCep( jsonResp.getString("cep") );
            cepObj.setCidade( jsonResp.getString("cidade") );
            cepObj.setComplemento( jsonResp.getString("complemento") );
            cepObj.setComplemento2( jsonResp.getString("complemento2") );
            cepObj.setEndereco( jsonResp.getString("end") );
            cepObj.setUf( jsonResp.getString("uf") );
  
            Ebean.save(cepObj); // save on database
  
            future.complete( JsonObject.mapFrom(cepObj).put("database", false) );
          }
        } catch (IOException e) {
          future.fail(e);
        }
      }else{
        future.complete(new JsonObject().put("error", true).put("msg", res.cause().getMessage()));
      }
    });
    
    return future;
  }
  
  
  // ---------------------------------
  
  private JsonObject checkParameters(JsonObject params){
    if( !params.containsKey("cep") ){
      return new JsonObject().put("error", true).put("cep","The 'cep' parameter is required for the search");
    }
    return null;
  }
  
}
