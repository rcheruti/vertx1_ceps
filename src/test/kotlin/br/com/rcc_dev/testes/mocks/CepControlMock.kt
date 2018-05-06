package br.com.rcc_dev.testes.mocks

import br.com.rcc_dev.testes.controls.CepControl
import br.com.rcc_dev.testes.entities.Cep
import io.vertx.core.Future

class CepControlMock: CepControl() {

  override fun findCEP(cep: String): Future<Cep> {
    val obj = Cep(
        id =      1 ,
        cep =     cep ,
        cidade =  "Cidade aqui" ,
        bairro =  "Bairro aqui"
    )

    return Future.succeededFuture<Cep>(obj)
  }

}