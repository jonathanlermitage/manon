package manon.app.config;

import manon.app.Cfg;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import static manon.app.Globals.Datasources.SPRINGBATCH_DATASOURCE;
import static manon.app.Globals.Properties.ENABLE_FLYWAY_ON_BATCH_DATASOURCE;

@ConditionalOnProperty(name = ENABLE_FLYWAY_ON_BATCH_DATASOURCE)
@Configuration
public class FlywaySlaveInitializerConfig {
    
    private final Cfg cfg;
    private final DataSource sprinbatchDatasource;
    
    public FlywaySlaveInitializerConfig(Cfg cfg, @Qualifier(SPRINGBATCH_DATASOURCE) DataSource sprinbatchDatasource) {
        this.cfg = cfg;
        this.sprinbatchDatasource = sprinbatchDatasource;
    }
    
    @PostConstruct
    public void migrateFlyway() {
        Flyway.configure()
            .dataSource(sprinbatchDatasource)
            .locations(cfg.getBatchFlywayLocation())
            .baselineOnMigrate(cfg.getBatchFlywayBaselineOnMigrate())
            .load()
            .migrate();
    }
}
