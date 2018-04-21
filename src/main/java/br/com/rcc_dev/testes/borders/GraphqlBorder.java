package br.com.rcc_dev.testes.borders;

import br.com.rcc_dev.testes.controls.CepControl;
import br.com.rcc_dev.testes.controls.HealthCheckControl;
import br.com.rcc_dev.testes.entities.CepOutput;
import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.coxautodev.graphql.tools.SchemaParser;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.language.IntValue;
import graphql.language.ObjectField;
import graphql.language.ObjectValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class GraphqlBorder {
  
  private static Logger LOGGER = LoggerFactory.getLogger(GraphqlBorder.class);
  
  @Inject
  private CepControl cepControl;
  @Inject
  private HealthCheckControl healthCheckControl;
  
  public Router createRouter(Vertx vertx){
    Router router = Router.router(vertx);
    GraphQL graphql = createGraphQL();
    
    router.route().handler(ctx -> {
      String queryParam = ctx.request().getParam("query");
      String body = ctx.getBodyAsString();
      if( body == null || body.length() == 0 ) body = queryParam;
      ExecutionResult executionResult = graphql.execute(body);
      Object data = executionResult.toSpecification();
      String jsonStr = Json.encode(data);
      List<GraphQLError> errors = executionResult.getErrors();
      if( errors != null && errors.size() > 0 ){
        ctx.response().setStatusCode(400)
            .putHeader("Content-type", "application/json")
            .end(jsonStr); // "Error: we cannot execute your query!"
      }else{
        String resp = data.toString();
        ctx.response().putHeader("Content-type", "application/json").end(jsonStr);
      }
    });
    
    return router;
  }
  
  public GraphQL createGraphQL(){
    GraphQLScalarType jsonType = new GraphQLScalarType("Json", "Json scalar type", new JsonCoercing());
  
    SchemaParser parser = SchemaParser.newParser()
        .scalars(jsonType)
        .file("schema.graphql")
        .dictionary("Cep", CepOutput.class)
        .resolvers(
            new QueryResolver()
        )
        .build()
        ;
    GraphQLSchema schema = parser.makeExecutableSchema();
    return GraphQL.newGraphQL(schema).build();
  }
  
  public static class JsonCoercing implements Coercing<JsonObject, Object> {
    public Object serialize(Object dataFetcherResult){
      LOGGER.info("JsonCoercing.serialize: " + dataFetcherResult);
      return dataFetcherResult;
    }
    public JsonObject parseValue(Object input){
      LOGGER.info("JsonCoercing.parseValue: " + input);
      return null;
    }
    public JsonObject parseLiteral(Object input){
      LOGGER.info("JsonCoercing.parseLiteral: " + input);
      if( input instanceof ObjectValue ){
        JsonObject json = new JsonObject();
        ObjectValue inputObj = (ObjectValue) input;
        for(ObjectField field : inputObj.getObjectFields()){
          Value value = field.getValue();
          if( value instanceof IntValue ){
            json.put( field.getName(), ((IntValue)value).getValue().intValue() );
          }
          
        }
        return json;
      }
      return null;
    }
  }
  
  // -------------------------------------------------------
  
  private <T> Future<T> toJavaFuture(io.vertx.core.Future<T> inFuture){
    CompletableFuture<T> future = new CompletableFuture<>();
    inFuture.setHandler(res ->{
      if(res.failed()){
        future.cancel(false);
      }else{
        future.complete(res.result());
      }
    });
    return future;
  }
  
  // -------------------------------------------------------
  
  public class QueryResolver implements GraphQLQueryResolver {
    
    public Future<CepOutput> cep(String cep){
      return toJavaFuture( cepControl.findCEP(cep) );
    }
    
    public Future<JsonObject> health(){
      return toJavaFuture( healthCheckControl.checkHealth() );
    }
    
    public JsonObject test(JsonObject json){
      if( json == null ) return null;
      return json;
    }
    
  }
  
}
