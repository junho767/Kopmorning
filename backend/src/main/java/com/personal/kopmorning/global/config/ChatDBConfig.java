package com.personal.kopmorning.global.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.personal.kopmorning.chatDomain.chat",  // Chat 도메인 패키지
        entityManagerFactoryRef = "chatEntityManager",
        transactionManagerRef = "chatTransactionManager"
)
public class ChatDBConfig {
    @Bean
    @ConfigurationProperties("chatdb.datasource")
    public DataSource chatDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean chatEntityManager(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(chatDataSource())
                .packages("com.personal.kopmorning.chatDomain.chat")
                .persistenceUnit("chat")
                .build();
    }

    @Bean
    public PlatformTransactionManager chatTransactionManager(
            @Qualifier("chatEntityManager") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
