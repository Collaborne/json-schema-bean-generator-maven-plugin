/**
 * Copyright (C) 2015 Collaborne B.V. (opensource@collaborne.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.collaborne.jsonschema.generator.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.DirectoryScanner;

import com.collaborne.jsonschema.generator.CodeGenerationException;
import com.collaborne.jsonschema.generator.Generator;
import com.collaborne.jsonschema.generator.Generator.Feature;
import com.collaborne.jsonschema.generator.driver.GeneratorDriver;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Goal which processes JSON schema files into (Java) sources
 */
@Mojo(name="generate", defaultPhase=LifecyclePhase.PROCESS_RESOURCES, threadSafe=true)
public class GenerateMojo extends AbstractMojo {
	@Parameter(defaultValue="src/main/schemas")
	private File sourceDirectory;
	@Parameter(defaultValue="*.json")
	private String[] includes;

	@Parameter
	private File mappingDirectory;
	@Parameter(defaultValue="*.json")
	private String[] mappingIncludes;

	// TODO: maybe replace with a "format" instead, and discover the class via other means
	@Parameter(defaultValue="com.collaborne.jsonschema.generator.pojo.PojoGenerator")
	private String generator;

	@Parameter(defaultValue="${project.build.directory}/generated-sources/beans")
	private File outputDirectory;

	@Parameter
	private Map<URI, String> features;

	@Parameter
	private URI rootUri;

	@Override
	public void execute() throws MojoExecutionException {
		Class<? extends Generator> generatorClass;
		try {
			generatorClass = Class.forName(generator).asSubclass(Generator.class);
		} catch (ClassNotFoundException e) {
			throw new MojoExecutionException("Cannot find generator class " + generator, e);
		}

		Injector injector = Guice.createInjector();

		Generator generator = injector.getInstance(generatorClass);
		if (features != null) {
			for (Map.Entry<URI, String> feature : features.entrySet()) {
				// XXX: Find a better way of discovering features
				Feature<Object> tmp = new Feature<>(feature.getKey().toASCIIString(), Object.class);
				// Try parsing: a) boolean, b) numberish, otherwise c) string
				if ("true".equalsIgnoreCase(feature.getValue())) {
					generator.setFeature(tmp, Boolean.TRUE);
				} else if ("false".equalsIgnoreCase(feature.getValue())) {
					generator.setFeature(tmp, Boolean.FALSE);
				} else {
					try {
						Double value = Double.valueOf(feature.getValue());
						generator.setFeature(tmp, value);
					} catch (NumberFormatException e) {
						generator.setFeature(tmp, feature.getValue());
					}
				}
			}
		}
		generator.setOutputDirectory(outputDirectory.toPath());

		GeneratorDriver driver = new GeneratorDriver(generator);

		DirectoryScanner scanner = new DirectoryScanner();
		if (mappingDirectory != null) {
			scanner.setBasedir(mappingDirectory);
			scanner.setIncludes(mappingIncludes);
			scanner.scan();

			for (String includedFileName : scanner.getIncludedFiles()) {
				Path mappingsFile = mappingDirectory.toPath().resolve(includedFileName);
				try {
					driver.addMappings(mappingsFile);
				} catch (IOException e) {
					throw new MojoExecutionException("Cannot load mappings from " + mappingsFile, e);
				}
			}
		}

		List<Path> schemaFiles = new ArrayList<>();
		scanner = new DirectoryScanner();
		scanner.setBasedir(sourceDirectory);
		scanner.setIncludes(includes);
		scanner.scan();

		for (String includedFileName : scanner.getIncludedFiles()) {
			Path schemaFile = sourceDirectory.toPath().resolve(includedFileName);
			schemaFiles.add(schemaFile);
		}

		if (rootUri == null) {
			rootUri = sourceDirectory.toURI();
		}

		// XXX: which directory should be the "base" directory actually?
		// For now we just take the source directory, but it seems we need to split this up
		// differently to allow multiple
		try {
			driver.run(sourceDirectory.toPath(), rootUri, schemaFiles);
		} catch (IOException e) {
			// XXX: IOException is not specific enough, we need a better handling here
			throw new MojoExecutionException("Cannot load schemas", e);
		} catch (CodeGenerationException e) {
			throw new MojoExecutionException("Cannot generate code", e);
		}
	}
}
