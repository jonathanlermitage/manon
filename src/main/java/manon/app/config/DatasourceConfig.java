package manon.app.config;

import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

import static manon.app.Globals.Datasources.MAIN_DATASOURCE;
import static manon.app.Globals.Datasources.SPRINGBATCH_DATASOURCE;

@Configuration
public class DatasourceConfig {
    
    /**
     * Main datasource.
     */
    @Bean(name = MAIN_DATASOURCE)
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource metierDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    /**
     * Datasource used by Spring Batch.
     */
    @Bean(name = SPRINGBATCH_DATASOURCE)
    @BatchDataSource
    @ConfigurationProperties(prefix = "manon.batch.datasource")
    public DataSource jobDataSource() {
        return DataSourceBuilder.create().build();
    }
}
