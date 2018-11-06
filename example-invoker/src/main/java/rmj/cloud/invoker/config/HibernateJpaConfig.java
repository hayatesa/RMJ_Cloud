package rmj.cloud.invoker.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages="rmj.cloud.invoker.domain")
@EnableJpaRepositories(basePackages= "rmj.cloud.invoker.repository")
@EnableTransactionManagement
public class HibernateJpaConfig {

}
