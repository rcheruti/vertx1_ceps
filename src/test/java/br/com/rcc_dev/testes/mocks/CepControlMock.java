package br.com.rcc_dev.testes.mocks;

import br.com.rcc_dev.testes.controls.CepControl;
import br.com.rcc_dev.testes.entities.CepOutput;
import br.com.rcc_dev.testes.entities.database.Cep;
import io.vertx.core.Future;

public class CepControlMock extends CepControl {
  
  public Future<CepOutput> findCEP(String cep){
    CepOutput obj = new CepOutput();
  
    obj.setCep(cep);
    obj.setCidade("Cidade aqui");
    obj.setBairro("Bairro aqui");
    obj.setId(1);
    
    return Future.succeededFuture(obj);
  }
  
}
