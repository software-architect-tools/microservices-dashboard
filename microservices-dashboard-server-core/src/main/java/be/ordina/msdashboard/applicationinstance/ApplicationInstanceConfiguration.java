/*
 * Copyright 2015-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.ordina.msdashboard.applicationinstance;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.LinkDiscoverers;
import org.springframework.hateoas.core.JsonPathLinkDiscoverer;
import org.springframework.hateoas.hal.HalLinkDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.plugin.core.OrderAwarePluginRegistry;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for the application instance.
 *
 * @author Tim Ysewyn
 */
@Configuration
public class ApplicationInstanceConfiguration {

	@Bean
	public ApplicationInstanceService applicationInstanceService(ApplicationInstanceRepository repository,
			ActuatorEndpointsDiscovererService actuatorEndpointsDiscovererService) {
		return new ApplicationInstanceService(repository, actuatorEndpointsDiscovererService);
	}

	@Bean
	ActuatorEndpointsDiscovererService actuatorEndpointsDiscovererService(
			@Qualifier("machine-to-machine-web-client") WebClient webClient,
			List<ActuatorEndpointsDiscoverer> actuatorEndpointsDiscoverers) {
		return new ActuatorEndpointsDiscovererService(halActuatorEndpointsDiscoverer(webClient),
				actuatorEndpointsDiscoverers);
	}

	@Bean
	public ApplicationInstanceHealthWatcher applicationInstanceHealthWatcher(
			ApplicationInstanceService applicationInstanceService,
			@Qualifier("machine-to-machine-web-client") WebClient webClient, ApplicationEventPublisher publisher) {
		return new ApplicationInstanceHealthWatcher(applicationInstanceService, webClient, publisher);
	}

	private HalActuatorEndpointsDiscoverer halActuatorEndpointsDiscoverer(WebClient webClient) {
		return new HalActuatorEndpointsDiscoverer(webClient,
				new LinkDiscoverers(OrderAwarePluginRegistry.create(Arrays.asList(new HalLinkDiscoverer(),
						new JsonPathLinkDiscoverer("$._links..['%s']..href",
								MediaType.parseMediaType("application/*+json"))))));
	}

	@Bean
	ApplicationInstanceUpdater applicationInstanceUpdater(ApplicationInstanceService service) {
		return new ApplicationInstanceUpdater(service);
	}

}
