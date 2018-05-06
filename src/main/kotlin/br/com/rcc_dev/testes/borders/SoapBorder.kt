package br.com.rcc_dev.testes.borders

import io.vertx.core.Vertx
import io.vertx.ext.web.Router

class SoapBorder {

  fun createRouter(vertx: Vertx): Router {
    val router = Router.router(vertx)

    return router
  }

}