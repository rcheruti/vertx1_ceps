package br.com.rcc_dev.testes.borders

import br.com.rcc_dev.testes.controls.CepControl
import br.com.rcc_dev.testes.controls.HealthCheckControl
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import io.vertx.core.AsyncResult
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import java.io.IOException
import java.io.StringWriter
import javax.inject.Inject

class RestBorder {

  @Inject
  private var cepControl: CepControl? = null

  @Inject
  private var healthCheckControl: HealthCheckControl? = null

  // --------------------------------------------------------

  fun createRouter(vertx: Vertx): Router {
    val router = Router.router(vertx)

    router.get("/cep/:cep").handler { ctx ->
      val cep = ctx.pathParam("cep")
      val json = JsonObject().put("cep", cep)
      val future = cepControl!!.findCEP(cep)

      future.setHandler { res -> writeResponse(ctx, res) }
    }.failureHandler({ failure(it) })

    router.route("/health").handler { ctx ->
      val future = healthCheckControl!!.checkHealth()

      future.setHandler { res -> writeResponse(ctx, res) }
    }.failureHandler({ failure(it) })

    return router
  }
  //---------------------------------------------

  private fun failure(ctx: RoutingContext) {
    ctx.response().putHeader("Content-type", "text/plain").setStatusCode(500)
        .end("Some error occured: " + ctx.failure().message)
  }

  private fun writeResponse(ctx: RoutingContext, res: AsyncResult<out Any>) {
    val response = ctx.response()
    if (res.failed()) {
      response.setStatusCode(500).end(res.cause().message)
    } else {
      val mode = ctx.request().getParam("mode")
      val respObj = res.result()
      val respStr = writeResponseForMode(mode, respObj, response)

      response.setStatusCode(200).end(respStr)
    }
  }

  private fun writeResponseForMode(mode: String?, respObj: Any, response: HttpServerResponse): String {
    var mode = mode
    if (mode == null) mode = ""
    val stream = StringWriter()
    try {
      when (mode.toLowerCase()) {
        "xml" -> {
          response.putHeader("Content-type", "application/xml")
          XmlMapper().writeValue(stream, respObj)
        }
        "yaml" -> {
          response.putHeader("Content-type", "application/yaml")
          YAMLMapper().writeValue(stream, respObj)
        }
        else -> {
          response.putHeader("Content-type", "application/json")
          return JsonObject.mapFrom(respObj).toString()
        }
      }
    } catch (ex: IOException) {
      return "error"
    }

    return stream.toString()
  }


}