package br.com.rcc_dev.testes.controls;

import br.com.rcc_dev.testes.startup.DatabaseControlMock;
import br.com.rcc_dev.testes.startup.GuiceModule;
import br.com.rcc_dev.testes.startup.GuiceModuleMock;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(VertxUnitRunner.class)
public class CepControlTest {
  
  private static Logger LOGGER = LoggerFactory.getLogger(CepControlTest.class);
  
  private CepControl cepControl;
  
  @Before
  public void setUp(TestContext tc) {
    DatabaseControlMock.start();
    Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new GuiceModuleMock());
    cepControl = injector.getInstance(CepControl.class);
  }
  
  @After
  public void tearDown(TestContext tc) {
  
  }
  
  // ------------------------------------------------------------
  
  @Test
  public void testCep(TestContext tc){
    Async async = tc.async();
  
    cepControl.findCEP("02072001").setHandler(res ->{
      tc.assertTrue( res.succeeded(), "CEP found" );
      if( res.failed() ){
        LOGGER.warn( res.cause().getMessage(), res.cause() );
      }else{
        LOGGER.info("Endereço encontrado: " + res.result().toString());
      }
      
      async.complete();
    });
  }
  
}
