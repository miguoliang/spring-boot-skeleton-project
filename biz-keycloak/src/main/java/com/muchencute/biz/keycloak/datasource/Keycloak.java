package com.muchencute.biz.keycloak.datasource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
  basePackages = "com.muchencute.biz.keycloak.repository",
  entityManagerFactoryRef = "keycloakEntityManager",
  transactionManagerRef = "keycloakTransactionManager")
public class Keycloak {

  private final Environment environment;

  @Autowired
  public Keycloak(Environment environment) {

    this.environment = environment;
  }

  @Bean
  @ConfigurationProperties(prefix = "app.datasource.keycloak")
  public DataSource keycloakDataSource() {

    return DataSourceBuilder.create().build();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean keycloakEntityManager() {

    final var em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(keycloakDataSource());
    em.setPackagesToScan("com.muchencute.biz.keycloak.model");
    em.setJpaVendorAdapter(keycloakHibernateJpaVendorAdapter());
    em.setJpaPropertyMap(Map.of(
      "hibernate.hbm2ddl.auto", "none",
      "hibernate.show_sql", environment.getProperty("spring.jpa.show-sql", "false"),
      "jakarta.persistence.sharedCache.mode", "UNSPECIFIED"
    ));
    return em;
  }

  @Bean
  public HibernateJpaVendorAdapter keycloakHibernateJpaVendorAdapter() {

    final var hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setShowSql(true);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setDatabasePlatform(
      environment.getProperty("app.datasource.keycloak.dialect"));
    return hibernateJpaVendorAdapter;
  }

  @Bean
  public JpaTransactionManager keycloakTransactionManager() {

    final var transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(
      keycloakEntityManager().getObject());
    return transactionManager;
  }
}
