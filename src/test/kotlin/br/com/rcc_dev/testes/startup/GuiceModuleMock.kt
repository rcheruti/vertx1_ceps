package br.com.rcc_dev.testes.startup

import br.com.rcc_dev.testes.controls.CepControl
import br.com.rcc_dev.testes.mocks.CepControlMock
import com.google.inject.AbstractModule

class GuiceModuleMock: AbstractModule() {

  override fun configure() {
    super.configure()
    bind(CepControl::class.java).to(CepControlMock::class.java)
  }

}