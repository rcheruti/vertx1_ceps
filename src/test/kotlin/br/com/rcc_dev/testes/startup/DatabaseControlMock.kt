package br.com.rcc_dev.testes.startup

import io.ebean.EbeanServer
import io.ebean.EbeanServerFactory
import io.ebean.config.ServerConfig
import java.util.*

object DatabaseControlMock {

  fun start(): EbeanServer {
    val props = Properties()
    props.setProperty("ebean.db.ddl.generate", "true")
    props.setProperty("ebean.db.ddl.run", "true")
    props.setProperty("datasource.db.username", "sa")
    props.setProperty("datasource.db.password", "")
    props.setProperty("datasource.db.databaseUrl", "jdbc:h2:mem:tests")
    props.setProperty("datasource.db.databaseDriver", "org.h2.Driver")
    val config = ServerConfig()
    config.loadFromProperties(props)
    config.name = "h2"
    config.isDefaultServer = true
    return EbeanServerFactory.create(config)
  }

}