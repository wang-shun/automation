package com.gome.test.mq.dao;


import com.jolbox.bonecp.BoneCPDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by zhangjiadi on 15/12/22.
 */
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    // ==============
    // PRIVATE FIELDS
    // ==============
    @Autowired
    private AppConfiguration env;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LocalContainerEntityManagerFactoryBean entityManagerFactory;

    // ==============
    // PUBLIC METHODS
    // ==============
    /**
     * DataSource definition for database connection. Settings are read from the
     * application.properties file (using the env object).
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        /**
         * 带连接池的BoneCPDataSource
         */
        BoneCPDataSource dataSource = new BoneCPDataSource();
        dataSource.setDriverClass(env.getProperty("db.driver"));
        dataSource.setJdbcUrl(env.getProperty("db.url"));
        dataSource.setUsername(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));

        dataSource.setIdleConnectionTestPeriodInMinutes(Integer.valueOf(env.getProperty("bonecp.idleConnectionTestPeriodInMinutes")));
        dataSource.setIdleMaxAgeInMinutes(Integer.valueOf(env.getProperty("bonecp.idleMaxAgeInMinutes")));
        dataSource.setMaxConnectionsPerPartition(Integer.valueOf(env.getProperty("bonecp.maxConnectionsPerPartition")));
        dataSource.setMinConnectionsPerPartition(Integer.valueOf(env.getProperty("bonecp.minConnectionsPerPartition")));
        dataSource.setStatementsCacheSize(Integer.valueOf(env.getProperty("bonecp.statementsCacheSize")));
        dataSource.setPartitionCount(Integer.valueOf(env.getProperty("bonecp.partitionCount")));
        dataSource.setAcquireIncrement(Integer.valueOf(env.getProperty("bonecp.acquireIncrement")));

        /**
         * 不带连接池的DataSource
         */
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(env.getProperty("db.driver"));
//        dataSource.setUrl(env.getProperty("db.url"));
//        dataSource.setUsername(env.getProperty("db.username"));
//        dataSource.setPassword(env.getProperty("db.password"));
        return dataSource;
    }

    /**
     * Declare the JPA entity manager factory.
     *
     * @return
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();

        entityManagerFactory.setDataSource(dataSource);

        // Classpath scanning of @Component, @Service, etc annotated class
        entityManagerFactory.setPackagesToScan(env.getProperty("entitymanager.packagesToScan"));

        // Vendor adapter
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        // Hibernate properties
        Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect",env.getProperty("hibernate.dialect"));
        additionalProperties.put("hibernate.show_sql",env.getProperty("hibernate.show_sql"));
        additionalProperties.put("hibernate.hbm2ddl.auto",env.getProperty("hibernate.hbm2ddl.auto"));
        entityManagerFactory.setJpaProperties(additionalProperties);

        return entityManagerFactory;
    }

    /**
     * Declare the transaction manager.
     *
     * @return
     */
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return transactionManager;
    }

    /**
     * PersistenceExceptionTranslationPostProcessor is a bean post processor
     * which adds an advisor to any bean annotated with Repository so that any
     * platform-specific exceptions are caught and then rethrown as one Spring's
     * unchecked data access exceptions (i.e. a subclass of
     * DataAccessException).
     *
     * @return
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

} // class DatabaseConfig
