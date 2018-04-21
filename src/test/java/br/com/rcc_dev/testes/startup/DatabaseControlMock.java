package br.com.rcc_dev.testes.startup;

import io.ebean.EbeanServer;
import io.ebean.EbeanServerFactory;
import io.ebean.config.ServerConfig;

import java.util.Properties;

public class DatabaseControlMock {
  
  public static EbeanServer start(){
    Properties props = new Properties();
    props.setProperty("ebean.db.ddl.generate",        "true");
    props.setProperty("ebean.db.ddl.run",             "true");
    props.setProperty("datasource.db.username",       "sa");
    props.setProperty("datasource.db.password",       "");
    props.setProperty("datasource.db.databaseUrl",    "jdbc:h2:mem:tests");
    props.setProperty("datasource.db.databaseDriver", "org.h2.Driver");
    ServerConfig config = new ServerConfig();
    config.loadFromProperties(props);
    config.setName("h2");
    config.setDefaultServer(true);
    EbeanServer db = EbeanServerFactory.create(config);
    return db;
  }

}
