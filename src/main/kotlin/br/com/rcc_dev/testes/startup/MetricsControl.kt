package br.com.rcc_dev.testes.startup

import br.com.rcc_dev.testes.Utils
import com.codahale.metrics.jmx.JmxReporter

object MetricsControl {

  fun start() {
    /*
    // start to log metrics to console
    ConsoleReporter reporter = ConsoleReporter.forRegistry(Utils.metrics)
        .convertRatesTo(TimeUnit.SECONDS)
        .convertDurationsTo(TimeUnit.MILLISECONDS)
        .build();
    reporter.start(1, TimeUnit.SECONDS);
    */

    // start the "JMX Reporter" of DropWizard
    val reporterJMX = JmxReporter.forRegistry(Utils.metrics).build()
    reporterJMX.start()
  }

}