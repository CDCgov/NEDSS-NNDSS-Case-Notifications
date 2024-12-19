package gov.cdc.stdprocessorservice.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "msgEntityManagerFactory",
        transactionManagerRef = "msgTransactionManager",
        basePackages = {
                "gov.cdc.stdprocessorservice.repository.msg",
        }
)
public class MsgDataSourceConfig {
    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Value("${spring.datasource.msg.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUserName;

    @Value("${spring.datasource.password}")
    private String dbUserPassword;

    @Bean(name = "msgDataSource")
    public DataSource msgDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();

        dataSourceBuilder.driverClassName(driverClassName);
        dataSourceBuilder.url(dbUrl);
        dataSourceBuilder.username(dbUserName);
        dataSourceBuilder.password(dbUserPassword);

        return dataSourceBuilder.build();
    }

    @Bean(name = "msgEntityManagerFactoryBuilder")
    public EntityManagerFactoryBuilder msgEntityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Bean(name = "msgEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean msgEntityManagerFactory(
            EntityManagerFactoryBuilder msgEntityManagerFactoryBuilder,
            @Qualifier("msgDataSource") DataSource msgDataSource) {
        return msgEntityManagerFactoryBuilder
                .dataSource(msgDataSource)
                .packages("gov.cdc.stdprocessorservice.repository.msg")
                .persistenceUnit("msg")
                .build();
    }

    @Primary
    @Bean(name = "msgTransactionManager")
    public PlatformTransactionManager msgTransactionManager(
            @Qualifier("msgEntityManagerFactory") EntityManagerFactory msgEntityManagerFactory) {
        return new JpaTransactionManager(msgEntityManagerFactory);
    }
}
