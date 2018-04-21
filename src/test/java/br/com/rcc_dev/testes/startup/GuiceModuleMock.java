package br.com.rcc_dev.testes.startup;

import br.com.rcc_dev.testes.controls.CepControl;
import br.com.rcc_dev.testes.mocks.CepControlMock;
import com.google.inject.AbstractModule;

public class GuiceModuleMock extends AbstractModule {
  
  @Override
  protected void configure(){
    bind(CepControl.class).to(CepControlMock.class);
  }
  
}
