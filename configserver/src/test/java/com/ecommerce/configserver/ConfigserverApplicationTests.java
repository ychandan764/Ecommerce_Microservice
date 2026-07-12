package com.ecommerce.configserver;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.config.server.config.ConfigServerHealthIndicator;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConfigserverApplicationTests {

	@Autowired
	private ConfigurationPropertiesRebinder rebinder;

	@Test
	void contextLoads() {
	}

	@Test
	void configServerHealthIndicatorIsExcludedFromRefreshRebinding() {
		assertThat(rebinder.getNeverRefreshable())
				.contains(ConfigServerHealthIndicator.class.getName());
		assertThat(rebinder.rebind(ConfigServerHealthIndicator.class)).isFalse();
	}

}
