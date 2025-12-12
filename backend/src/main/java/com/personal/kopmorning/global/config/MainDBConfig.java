package com.personal.kopmorning.global.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.personal.kopmorning.domain",   // 메인 DB 도메인
        entityManagerFactoryRef = "mainEntityManager",
        transactionManagerRef = "mainTransactionManager"
)
public class MainDBConfig {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource mainDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean mainEntityManager(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(mainDataSource())
                .packages("com.personal.kopmorning.domain")
                .persistenceUnit("main")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager mainTransactionManager(
            @Qualifier("mainEntityManager") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
