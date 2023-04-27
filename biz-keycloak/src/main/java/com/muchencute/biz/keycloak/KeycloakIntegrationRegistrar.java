package com.muchencute.biz.keycloak;

import com.muchencute.biz.keycloak.repository.*;
import com.muchencute.biz.keycloak.service.*;
import jakarta.persistence.EntityManagerFactory;
import lombok.SneakyThrows;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.lang.NonNull;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Map;

public class KeycloakIntegrationRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {


  private Environment environment;

  @Override
  public void setEnvironment(@NonNull Environment environment) {
    this.environment = environment;
  }

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, @NonNull BeanDefinitionRegistry registry) {

    final var attributes = importingClassMetadata.getAnnotationAttributes(EnableKeycloakIntegration.class.getName());

    if (attributes == null) {
      return;
    }

    final var beanNamePrefix = attributes.get("beanNamePrefix").toString();
    final var propertyPrefix = attributes.get("propertyPrefix").toString();

    registerKeycloakDataSource(beanNamePrefix, propertyPrefix, registry);
    registerKeycloakHibernateJpaVendorAdapter(beanNamePrefix, propertyPrefix, registry);
    registerKeycloakEntityManagerFactory(beanNamePrefix, registry);
    registerKeycloakTransactionManager(beanNamePrefix, registry);
    registerKeycloakJpaRepositoryFactory(beanNamePrefix, registry);
    registerKeycloakJpaRepository(beanNamePrefix, registry);
    registerKeycloakAdminClient(beanNamePrefix, propertyPrefix, registry);
    registerKeycloakService(beanNamePrefix, propertyPrefix, registry);
    registerKeycloakClientService(beanNamePrefix, registry);
    registerKeycloakUserService(beanNamePrefix, registry);
    registerKeycloakRoleService(beanNamePrefix, registry);
    registerKeycloakGroupService(beanNamePrefix, registry);
  }

  private void registerKeycloakDataSource(String beanNamePrefix, String propertyPrefix, BeanDefinitionRegistry registry) {

    final var dataSourceProperties = new DataSourceProperties();
    dataSourceProperties.setUrl(environment.getProperty(propertyPrefix + ".datasource.url"));
    dataSourceProperties.setDriverClassName(environment.getProperty(propertyPrefix + ".datasource.driver-class-name"));
    dataSourceProperties.setUsername(environment.getProperty(propertyPrefix + ".datasource.username"));
    dataSourceProperties.setPassword(environment.getProperty(propertyPrefix + ".datasource.password"));
    final var dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(DataSource.class, () -> dataSource)
      .getBeanDefinition();
    final var dataSourceBeanName = beanNamePrefix + "DataSource";
    registry.registerBeanDefinition(dataSourceBeanName, beanDefinition);
  }

  private void registerKeycloakHibernateJpaVendorAdapter(String beanNamePrefix, String propertyPrefix, BeanDefinitionRegistry registry) {

    final var hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
    hibernateJpaVendorAdapter.setShowSql(true);
    hibernateJpaVendorAdapter.setGenerateDdl(true);
    hibernateJpaVendorAdapter.setDatabasePlatform(
      environment.getProperty(propertyPrefix + ".datasource.hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(HibernateJpaVendorAdapter.class, () -> hibernateJpaVendorAdapter)
      .getBeanDefinition();
    final var hibernateJpaVendorAdapterBeanName = beanNamePrefix + "HibernateJpaVendorAdapter";
    registry.registerBeanDefinition(hibernateJpaVendorAdapterBeanName, beanDefinition);
  }

  private void registerKeycloakEntityManagerFactory(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var dataSource = getBean(registry, beanNamePrefix + "DataSource", DataSource.class);
    final var hibernateJpaVendorAdapter = getBean(registry, beanNamePrefix + "HibernateJpaVendorAdapter", HibernateJpaVendorAdapter.class);

    final var em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.muchencute.biz.keycloak.model");
    em.setJpaVendorAdapter(hibernateJpaVendorAdapter);
    em.setJpaPropertyMap(Map.of(
      "hibernate.hbm2ddl.auto", "none",
      "hibernate.show_sql", environment.getProperty("spring.jpa.show-sql", "false"),
      "jakarta.persistence.sharedCache.mode", "UNSPECIFIED"
    ));
    em.afterPropertiesSet();
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(LocalContainerEntityManagerFactoryBean.class, () -> em)
      .getBeanDefinition();
    final var entityManagerBeanName = beanNamePrefix + "EntityManagerFactory";
    registry.registerBeanDefinition(entityManagerBeanName, beanDefinition);
  }

  @SneakyThrows
  private void registerKeycloakTransactionManager(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var proxy = getProxy(registry, beanNamePrefix + "EntityManagerFactory");
    final var transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory((EntityManagerFactory) proxy);
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(JpaTransactionManager.class, () -> transactionManager)
      .getBeanDefinition();
    final var transactionManagerBeanName = beanNamePrefix + "TransactionManager";
    registry.registerBeanDefinition(transactionManagerBeanName, beanDefinition);
  }

  private void registerKeycloakAdminClient(String beanNamePrefix, String propertyPrefix, BeanDefinitionRegistry registry) {

    final var keycloakAdminClient = Keycloak.getInstance(
      environment.getProperty(propertyPrefix + ".auth-server-url"),
      environment.getProperty(propertyPrefix + ".realm"),
      environment.getProperty(propertyPrefix + ".username"),
      environment.getProperty(propertyPrefix + ".password"),
      environment.getProperty(propertyPrefix + ".client-id"));
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Keycloak.class, () -> keycloakAdminClient)
      .getBeanDefinition();
    final var keycloakAdminClientBeanName = beanNamePrefix + "KeycloakAdminClient";
    registry.registerBeanDefinition(keycloakAdminClientBeanName, beanDefinition);
  }

  @SneakyThrows
  private void registerKeycloakJpaRepositoryFactory(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var proxy = getProxy(registry, beanNamePrefix + "EntityManagerFactory");
    final var entityManager = ((EntityManagerFactory) proxy).createEntityManager();
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(JpaRepositoryFactory.class,
      () -> new JpaRepositoryFactory(entityManager)).getBeanDefinition();
    final var jpaRepositoryFactoryBeanName = beanNamePrefix + "JpaRepositoryFactory";
    registry.registerBeanDefinition(jpaRepositoryFactoryBeanName, beanDefinition);
  }

  @SneakyThrows
  private void registerKeycloakJpaRepository(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var keycloakJpaRepositoryFactory = getBean(registry, beanNamePrefix + "JpaRepositoryFactory", JpaRepositoryFactory.class);
    registerRepository(UserEntityRepository.class, keycloakJpaRepositoryFactory, beanNamePrefix, registry);
    registerRepository(KeycloakRoleRepository.class, keycloakJpaRepositoryFactory, beanNamePrefix, registry);
    registerRepository(KeycloakGroupRepository.class, keycloakJpaRepositoryFactory, beanNamePrefix, registry);
    registerRepository(EventEntityRepository.class, keycloakJpaRepositoryFactory, beanNamePrefix, registry);
    registerRepository(UserAttributeRepository.class, keycloakJpaRepositoryFactory, beanNamePrefix, registry);
  }

  private <T> void registerRepository(Class<T> repositoryClass, JpaRepositoryFactory factory, String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var beanDefinition = BeanDefinitionBuilder
      .genericBeanDefinition(repositoryClass, () -> factory.getRepository(repositoryClass))
      .getBeanDefinition();
    final var jpaRepositoryBeanName = beanNamePrefix + repositoryClass.getSimpleName();
    registry.registerBeanDefinition(jpaRepositoryBeanName, beanDefinition);
  }

  private void registerKeycloakService(String beanNamePrefix, String propertyPrefix, BeanDefinitionRegistry registry) {

    final var keycloakAdminClient = getBean(registry, beanNamePrefix + "KeycloakAdminClient", Keycloak.class);
    final var realm = environment.getProperty(propertyPrefix + ".realm");
    final var clientId = environment.getProperty(propertyPrefix + ".client-id");
    final var authServerUrl = environment.getProperty(propertyPrefix + ".auth-server-url");
    final var userEntityRepository = getBean(registry, beanNamePrefix + "UserEntityRepository", UserEntityRepository.class);
    final var keycloakGroupRepository = getBean(registry, beanNamePrefix + "KeycloakGroupRepository", KeycloakGroupRepository.class);
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(KeycloakService.class,
      () -> new KeycloakService(keycloakAdminClient, realm, clientId, authServerUrl, userEntityRepository, keycloakGroupRepository)).getBeanDefinition();
    final var keycloakServiceBeanName = beanNamePrefix + "KeycloakService";
    registry.registerBeanDefinition(keycloakServiceBeanName, beanDefinition);
  }

  private void registerKeycloakGroupService(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var keycloakService = getBean(registry, beanNamePrefix + "KeycloakService", KeycloakService.class);
    final var keycloakGroupRepository = getBean(registry, beanNamePrefix + "GroupEntityRepository", KeycloakGroupRepository.class);
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(KeycloakGroupService.class,
      () -> new KeycloakGroupService(keycloakService, keycloakGroupRepository)).getBeanDefinition();
    final var keycloakGroupServiceBeanName = beanNamePrefix + "KeycloakGroupService";
    registry.registerBeanDefinition(keycloakGroupServiceBeanName, beanDefinition);
  }

  private void registerKeycloakUserService(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var keycloakService = getBean(registry, beanNamePrefix + "KeycloakService", KeycloakService.class);
    final var userEntityRepository = getBean(registry, beanNamePrefix + "UserEntityRepository", UserEntityRepository.class);
    final var eventEntityRepository = getBean(registry, beanNamePrefix + "EventEntityRepository", EventEntityRepository.class);
    final var userAttributeRepository = getBean(registry, beanNamePrefix + "UserAttributeRepository", UserAttributeRepository.class);
    final var keycloakRoleRepository = getBean(registry, beanNamePrefix + "KeycloakRoleRepository", KeycloakRoleRepository.class);
    final var keycloakGroupRepository = getBean(registry, beanNamePrefix + "GroupEntityRepository", KeycloakGroupRepository.class);
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(KeycloakUserService.class,
      () -> new KeycloakUserService(keycloakService, userEntityRepository, eventEntityRepository, userAttributeRepository, keycloakRoleRepository, keycloakGroupRepository)).getBeanDefinition();
    final var keycloakUserServiceBeanName = beanNamePrefix + "KeycloakUserService";
    registry.registerBeanDefinition(keycloakUserServiceBeanName, beanDefinition);
  }

  private void registerKeycloakRoleService(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var keycloakService = getBean(registry, beanNamePrefix + "KeycloakService", KeycloakService.class);
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(KeycloakRoleService.class,
      () -> new KeycloakRoleService(keycloakService)).getBeanDefinition();
    registry.registerBeanDefinition(beanNamePrefix + "KeycloakRoleService", beanDefinition);
  }

  private void registerKeycloakClientService(String beanNamePrefix, BeanDefinitionRegistry registry) {

    final var keycloakService = getBean(registry, beanNamePrefix + "KeycloakService", KeycloakService.class);
    final var beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(KeycloakClientService.class,
      () -> new KeycloakClientService(keycloakService)).getBeanDefinition();
    registry.registerBeanDefinition(beanNamePrefix + "KeycloakClientService", beanDefinition);
  }

  @SneakyThrows
  private <T> T getBean(BeanDefinitionRegistry registry, String beanName, Class<T> beanClass) {
    if (registry instanceof DefaultListableBeanFactory beanFactory) {
      return beanFactory.getBean(beanName, beanClass);
    }
    throw new IllegalStateException("Unable to retrieve bean because registry is not a DefaultListableBeanFactory");
  }

  @SneakyThrows
  private Object getProxy(BeanDefinitionRegistry registry, String beanName) {
    if (registry instanceof DefaultListableBeanFactory beanFactory) {
      return beanFactory.getBean(beanName);
    }
    throw new IllegalStateException("Unable to retrieve bean because registry is not a DefaultListableBeanFactory");
  }
}
