package br.com.rcc_dev.testes.borders

import br.com.rcc_dev.testes.loadConfig
import br.com.rcc_dev.testes.startServer
import br.com.rcc_dev.testes.startup.DatabaseControlMock
import br.com.rcc_dev.testes.startup.GuiceModuleMock
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Stage
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.ext.unit.TestContext
import org.junit.After
import org.junit.Before


//@RunWith(VertxUnitRunner.class)
class RestBorderTest {

  private var injector: Injector? = null
  private var vertx: Vertx? = null

  @Before
  fun setUp(tc: TestContext) {
    DatabaseControlMock.start()
    val config = loadConfig()
    vertx = Vertx.vertx(VertxOptions(config))
    injector = Guice.createInjector(Stage.DEVELOPMENT, GuiceModuleMock())
    startServer(vertx!!, config, injector!!)
  }

  @After
  fun tearDown(tc: TestContext) {

  }

  // ---------------------------------------------------

  //@Test
  fun testRestBorder(tc: TestContext) {
    val async = tc.async()


    //async.complete();
  }

}