@file:JvmName("Main")
package br.com.rcc_dev.testes

import br.com.rcc_dev.testes.Utils
import br.com.rcc_dev.testes.borders.GraphqlBorder
import br.com.rcc_dev.testes.borders.RestBorder
import br.com.rcc_dev.testes.borders.SoapBorder
import br.com.rcc_dev.testes.startup.DatabaseControl
import br.com.rcc_dev.testes.startup.GuiceModule
import br.com.rcc_dev.testes.startup.MetricsControl
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

import java.io.IOException

fun main(args: Array<String>){
  MetricsControl.start()
  DatabaseControl.start()
  val injector = Guice.createInjector(Stage.PRODUCTION, GuiceModule())

  val config = loadConfig()
  val vertx = Vertx.vertx(VertxOptions(config))

  startServer(vertx, config, injector)
}

fun startServer(vertx: Vertx, config: JsonObject, injector: Injector) {
  // starting HTTP server
  val router = Router.router(vertx)
  router.route().handler(BodyHandler.create())
  router.mountSubRouter("/", injector.getInstance(RestBorder::class.java).createRouter(vertx))
  router.mountSubRouter("/soap", injector.getInstance(SoapBorder::class.java).createRouter(vertx))
  router.mountSubRouter("/graphql", injector.getInstance(GraphqlBorder::class.java).createRouter(vertx))

  val server = vertx.createHttpServer()
  server.requestHandler({ router.accept(it) }).listen(Utils.jInt(config, "http.port")) { res -> println("Deploy Main: " + res.succeeded()) }
}

fun loadConfig(): JsonObject {
  // reading configs for execution
  val configYAML = Utils.fileContentsJar("config.yaml")
  var config: JsonObject? = null
  try {
    val node = YAMLMapper().readTree(configYAML)
    config = JsonObject(node.toString())
  } catch (e: IOException) {
    e.printStackTrace()
  }

  println("Config: " + config!!.encodePrettily())
  return config
}
