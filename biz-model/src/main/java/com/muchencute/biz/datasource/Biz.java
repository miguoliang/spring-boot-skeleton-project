package com.muchencute.biz.datasource;

import com.muchencute.biz.repository.BaseRepositoryImpl;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        repositoryBaseClass = BaseRepositoryImpl.class,
        basePackages = "com.muchencute.biz.repository",
        entityManagerFactoryRef = "bizEntityManager",
        transactionManagerRef = "bizTransactionManager")
public class Biz {

  private final Environment environment;

  @Autowired
  public Biz(Environment environment) {

    this.environment = environment;
  }

  private static SpringLiquibase springLiquibase(DataSource dataSource,
                                                 LiquibaseProperties properties) {

    final var liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog(properties.getChangeLog());
    liquibase.setContexts(properties.getContexts());
    liquibase.setDefaultSchema(properties.getDefaultSchema());
    liquibase.setDropFirst(properties.isDropFirst());
    liquibase.setShouldRun(properties.isEnabled());
    liquibase.setLabelFilter(properties.getLabelFilter());
    liquibase.setChangeLogParameters(properties.getParameters());
    liquibase.setRollbackFile(properties.getRollbackFile());
    return liquibase;
  }

  @Bean
  @ConfigurationProperties(prefix = "app.datasource.biz")
  public DataSourceProperties bizDataSourceProperties() {

    return new DataSourceProperties();
  }

  @Bean
  @Primary
  public HikariDataSource bizDataSource() {

    return bizDataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource.class)
            .build();
  }

  @Bean(name = "bizEntityManager")
  @Primary
  @Profile("test")
  public LocalContainerEntityManagerFactoryBean bizEntityManagerForTest() {

    final var showSql = environment.getProperty("app.show-sql", "false");
    LocalContainerEntityManagerFactoryBean em
            = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(bizDataSource());
    em.setPackagesToScan("com.singhand.sd.tpservice.bizmodel.model");
    em.setJpaVendorAdapter(bizHibernateJpaVendorAdapter());
    em.setJpaPropertyMap(Map.of(
            // 因为 H2 和 PostgresSQL 的大文本类型不一致，因此不能开启 validate，否则会出异常
            "hibernate.hbm2ddl.auto", "none",
            "hibernate.show_sql", showSql
    ));
    return em;
  }

  @Bean
  @Primary
  public LocalContainerEntityManagerFactoryBean bizEntityManager() {

    final var showSql = environment.getProperty("app.show-sql", "false");
    LocalContainerEntityManagerFactoryBean em
            = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(bizDataSource());
    em.setPackagesToScan("com.muchencute.biz.model");
    em.setJpaVendorAdapter(bizHibernateJpaVendorAdapter());
    em.setJpaPropertyMap(Map.of(
            "hibernate.hbm2ddl.auto", "validate",
            "hibernate.show_sql", showSql
    ));
    return em;
  }

  @Bean
  @Primary
  public HibernateJpaVendorAdapter bizHibernateJpaVendorAdapter() {

    final var hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setShowSql(true);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setDatabasePlatform(
            environment.getProperty("app.datasource.biz.dialect"));
    return hibernateJpaVendorAdapter;
  }

  @Bean
  @Primary
  public JpaTransactionManager bizTransactionManager() {

    JpaTransactionManager transactionManager
            = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(
            bizEntityManager().getObject());
    return transactionManager;
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "app.datasource.biz.liquibase")
  public LiquibaseProperties bizLiquibaseProperties() {

    return new LiquibaseProperties();
  }

  @Bean
  @Primary
  public SpringLiquibase bizLiquibase() {

    return springLiquibase(bizDataSource(), bizLiquibaseProperties());
  }
}
