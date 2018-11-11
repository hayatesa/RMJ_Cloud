package rmj.cloud.example.provider.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EntityScan(basePackages = "rmj.cloud.provider.domain")
@EnableJpaRepositories(basePackages = "rmj.cloud.provider.repository")
@EnableTransactionManagement
public class HibernateJpaConfig {

}
