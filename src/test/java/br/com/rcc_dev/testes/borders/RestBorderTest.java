package br.com.rcc_dev.testes.borders;

import br.com.rcc_dev.testes.Main;
import br.com.rcc_dev.testes.startup.DatabaseControlMock;
import br.com.rcc_dev.testes.startup.GuiceModuleMock;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.Router;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RestBorderTest {
  
  private Injector injector;
  private Vertx vertx;
  
  @Before
  public void setUp(TestContext tc) {
    DatabaseControlMock.start();
    JsonObject config = Main.loadConfig();
    vertx = Vertx.vertx(new VertxOptions(config));
    injector = Guice.createInjector(Stage.DEVELOPMENT, new GuiceModuleMock());
    Main.startServer(vertx, config, injector);
  }
  
  @After
  public void tearDown(TestContext tc) {
  
  }
  
  // ---------------------------------------------------
  
  @Test
  public void testRestBorder(TestContext tc){
    Async async = tc.async();
  
    
    //async.complete();
  }
  
}
