package br.com.rcc_dev.testes.controls

import br.com.rcc_dev.testes.Utils
import br.com.rcc_dev.testes.entities.Cep
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import io.ebean.Ebean
import io.vertx.core.Future
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.WebClient
import org.slf4j.LoggerFactory
import java.io.IOException

open class CepControl {

  private val LOGGER = LoggerFactory.getLogger(CepControl::class.java)

  private val correiosURL = "https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente"
  private val consultaCepTemplate = Utils.fileContentsJar("consultaCEP.xml")
  private val http: WebClient = WebClient.create(Vertx.vertx())

  // ---------------------------------

  /**
   * This method will ask Correios about address info related with the CEP (Brazilian Zip Code) informed.
   *
   * @param cep CEP that you want info from Correios
   * @return Info about the CEP
   */
  open fun findCEP(cep: String): Future<Cep> {
    Utils.requests.mark()

    // get CEP without non-numeric chars
    val cepStr = cep.replace("\\D+".toRegex(), "")
    if (cepStr.isEmpty()) return Future.failedFuture("The 'cep' parameter is required for the search")


    // SOAP request to Correios and Future to response
    val reqStr = this.consultaCepTemplate.replace("\$cep", cepStr)
    val future = Future.future<Cep>()

    val ef = Ebean.getExpressionFactory()
    val cepList = Ebean.find(Cep::class.java).where(ef.eq("cep", cepStr)).findList()
    if (!cepList.isEmpty()) {
      val output = cepList[0]
      output.database = true
      future.complete(output)
      return future
    }

    // HTTP request to Correios
    http.postAbs(this.correiosURL).ssl(true).sendBuffer(Buffer.buffer(reqStr)) { res ->
      if (res.succeeded()) {
        val body = res.result().bodyAsString(Utils.charISO_8859_1)
        try {
          val json = XmlMapper().readTree(body)
          if (json.findPath("Body").has("Fault")) {
            future.fail("That was not possible to find your CEP")
          } else {
            val jsonResp = json.findPath("Body").findPath("consultaCEPResponse").findPath("return")
            val cepObj = Cep(
                bairro =        jsonResp.findPath("bairro").asText() ,
                cep =           jsonResp.findPath("cep").asText() ,
                cidade =        jsonResp.findPath("cidade").asText() ,
                complemento =   jsonResp.findPath("complemento").asText() ,
                complemento2 =  jsonResp.findPath("complemento2").asText() ,
                endereco =      jsonResp.findPath("end").asText() ,
                uf =            jsonResp.findPath("uf").asText()
            )

            Ebean.save(cepObj) // save on database
            future.complete(cepObj)
          }
        } catch (e: IOException) {
          future.fail(e)
        }

      } else {
        future.fail(res.cause())
      }
    }

    return future
  }

  // ---------------------------------

  private fun checkParameters(params: JsonObject): JsonObject? {
    return if (!params.containsKey("cep")) {
      JsonObject().put("error", true).put("cep", "The 'cep' parameter is required for the search")
    } else null
  }
}