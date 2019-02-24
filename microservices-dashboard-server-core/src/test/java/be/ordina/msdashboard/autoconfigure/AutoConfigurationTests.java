package be.ordina.msdashboard.autoconfigure;

import org.junit.Test;

import be.ordina.msdashboard.applicationinstance.ApplicationInstanceHealthWatcher;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tim Ysewyn
 */
public class AutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(
					MicroservicesDashboardServerAutoConfiguration.class,
					CompositeDiscoveryClientAutoConfiguration.class));

	@Test
	public void shouldLoadContext() {
		this.contextRunner.run(context -> {
			assertThat(context.getBean(ApplicationInstanceHealthWatcher.class)).isNotNull();
		});
	}

}
