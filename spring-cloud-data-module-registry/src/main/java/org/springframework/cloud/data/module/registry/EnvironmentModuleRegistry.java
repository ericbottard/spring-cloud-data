/*
 * Copyright 2015 the original author or authors.
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

package org.springframework.cloud.data.module.registry;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.data.core.ModuleCoordinates;

/**
 * A ModuleRegistry that is statically configured from the Environment,
 * typically through {@code application.yml}:
 * <pre>
 * spring:
 *   cloud:
 *     data:
 *       registry:
 *         contents:
 *           "source:time": org.springframework.cloud.stream.module:time-source:1.0.0.BUILD-SNAPSHOT
 *           "sink:log":    org.springframework.cloud.stream.module:log-sink:1.0.0.BUILD-SNAPSHOT
 * </pre>
 */
public class EnvironmentModuleRegistry implements ModuleRegistry {

	private final Logger logger = LoggerFactory.getLogger(EnvironmentModuleRegistry.class);

	private Settings settings;

	private Map<String, ModuleCoordinates> mappings = new HashMap<>();

	@Autowired
	public void setSettings(Settings settings) {
		this.settings = settings;
		for (Map.Entry<String, String> kv : settings.getContents().entrySet()) {
			ModuleCoordinates coordinates = ModuleCoordinates.parse(kv.getValue());
			String key = kv.getKey().replace(" ", "");
			ModuleCoordinates before = mappings.put(key, coordinates);
			if (before != null) {
				logger.warn("Duplicate Mapping for module '{}:' both {} and {} registered", key, before, coordinates);
			}
		}
	}
	@Override
	public ModuleCoordinates findByNameAndType(String name, String type) {
		return mappings.get(type + ":" + name);
	}


	@ConfigurationProperties(prefix = "spring.cloud.data.registry")
	public static class Settings {

		private Map<String, String> contents = new HashMap<>();

		public Map<String, String> getContents() {
			return contents;
		}

		public void setContents(Map<String, String> contents) {
			this.contents = contents;
		}

	}
}
