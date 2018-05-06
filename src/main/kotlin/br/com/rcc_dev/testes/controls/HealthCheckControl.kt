package br.com.rcc_dev.testes.controls

import br.com.rcc_dev.testes.Utils
import br.com.rcc_dev.testes.controls.HealthCheckControl
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.healthchecks.HealthChecks
import io.vertx.ext.healthchecks.Status
import io.vertx.ext.web.client.WebClient
import org.slf4j.LoggerFactory

open class HealthCheckControl {


  private val LOGGER = LoggerFactory.getLogger(HealthCheckControl::class.java)

  private var healthCheck: HealthChecks
  private val correiosURL = "https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente?wsdl"
  private val http: WebClient

  init {
    val vertx = Vertx.vertx()
    this.http = WebClient.create(vertx)
    healthCheck = HealthChecks.create(vertx)

    healthCheck.register("online", 300) { future -> future.complete() }
    healthCheck.register("Correios online", 2000) { future ->
      val time1 = System.currentTimeMillis()
      http.getAbs(correiosURL).send { res ->
        val time2 = System.currentTimeMillis()
        val obj = JsonObject().put("executionTime", time2 - time1)
        val status: Status

        if (res.failed())
          status = Status.KO(obj)
        else
          status = Status.OK(obj)

        future.complete(status)
      }
    }
  }

  // ---------------------------------

  fun checkHealth(): Future<JsonObject> {
    Utils.requests.mark()
    val future = Future.future<JsonObject>()
    healthCheck.invoke { json -> future.complete(json) }
    return future
  }

}