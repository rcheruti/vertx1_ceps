package br.com.rcc_dev.testes.controls

import br.com.rcc_dev.testes.startup.DatabaseControlMock
import br.com.rcc_dev.testes.startup.GuiceModuleMock
import com.google.inject.Guice
import com.google.inject.Stage
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.LoggerFactory

@RunWith(VertxUnitRunner::class)
class CepControlTest {

  private var cepControl: CepControl? = null

  @Before
  fun setUp(tc: TestContext) {
    DatabaseControlMock.start()
    val injector = Guice.createInjector(Stage.DEVELOPMENT, GuiceModuleMock())
    cepControl = injector.getInstance(CepControl::class.java)
  }

  @After
  fun tearDown(tc: TestContext) {

  }

  // ------------------------------------------------------------

  @Test
  fun testCep(tc: TestContext) {
    val async = tc.async()

    cepControl!!.findCEP("02072001").setHandler { res ->
      tc.assertTrue(res.succeeded(), "CEP found")
      if (res.failed()) {
        LOGGER.warn(res.cause().message, res.cause())
      } else {
        LOGGER.info("Endereço encontrado: " + res.result().toString())
      }

      async.complete()
    }
  }

  companion object {

    private val LOGGER = LoggerFactory.getLogger(CepControlTest::class.java)
  }

}
