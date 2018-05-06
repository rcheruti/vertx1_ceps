package br.com.rcc_dev.testes.borders

import br.com.rcc_dev.testes.controls.CepControl
import br.com.rcc_dev.testes.controls.HealthCheckControl
import br.com.rcc_dev.testes.entities.Cep
import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.coxautodev.graphql.tools.SchemaParser
import graphql.GraphQL
import graphql.language.IntValue
import graphql.language.ObjectValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future
import javax.inject.Inject

class GraphqlBorder {

  private val LOGGER = LoggerFactory.getLogger(GraphqlBorder::class.java)

  @Inject
  private var cepControl: CepControl? = null
  @Inject
  private var healthCheckControl: HealthCheckControl? = null

  // ---------------------------------------------


  fun createRouter(vertx: Vertx): Router {
    val router = Router.router(vertx)
    val graphql = createGraphQL()

    router.route().handler { ctx ->
      val queryParam = ctx.request().getParam("query")
      var body: String? = ctx.bodyAsString
      if (body == null || body.length == 0) body = queryParam
      val executionResult = graphql.execute(body)
      val data = executionResult.toSpecification()
      val jsonStr = Json.encode(data)
      val errors = executionResult.errors
      if (errors != null && errors.size > 0) {
        ctx.response().setStatusCode(400)
            .putHeader("Content-type", "application/json")
            .end(jsonStr) // "Error: we cannot execute your query!"
      } else {
        val resp = data.toString()
        ctx.response().putHeader("Content-type", "application/json").end(jsonStr)
      }
    }

    return router
  }

  fun createGraphQL(): GraphQL {
    val jsonType = GraphQLScalarType("Json", "Json scalar type", JsonCoercing())

    val parser = SchemaParser.newParser()
        .scalars(jsonType)
        .file("schema.graphql")
        .dictionary("Cep", Cep::class.java)
        .resolvers(
            QueryResolver()
        )
        .build()
    val schema = parser.makeExecutableSchema()
    return GraphQL.newGraphQL(schema).build()
  }

  class JsonCoercing : Coercing<JsonObject, Any> {
    private val LOGGER = LoggerFactory.getLogger(JsonCoercing::class.java)

    override fun serialize(dataFetcherResult: Any): Any {
      LOGGER.info("JsonCoercing.serialize: $dataFetcherResult")
      return dataFetcherResult
    }

    override fun parseValue(input: Any): JsonObject? {
      LOGGER.info("JsonCoercing.parseValue: $input")
      return null
    }

    override fun parseLiteral(input: Any): JsonObject? {
      LOGGER.info("JsonCoercing.parseLiteral: $input")
      if (input is ObjectValue) {
        val json = JsonObject()
        for (field in input.objectFields) {
          val value = field.value
          if (value is IntValue) {
            json.put(field.name, value.value.toInt())
          }

        }
        return json
      }
      return null
    }
  }

  // -------------------------------------------------------

  private fun <T> toJavaFuture(inFuture: io.vertx.core.Future<T>): Future<T> {
    val future = CompletableFuture<T>()
    inFuture.setHandler { res ->
      if (res.failed()) {
        future.cancel(false)
      } else {
        future.complete(res.result())
      }
    }
    return future
  }

  // -------------------------------------------------------

  inner class QueryResolver : GraphQLQueryResolver {

    fun cep(cep: String): Future<Cep> {
      return toJavaFuture(cepControl!!.findCEP(cep))
    }

    fun health(): Future<JsonObject> {
      return toJavaFuture(healthCheckControl!!.checkHealth())
    }

    fun test(json: JsonObject?): JsonObject? {
      return json
    }

  }

}