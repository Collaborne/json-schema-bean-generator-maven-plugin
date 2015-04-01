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

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.experimental.theories.suppliers.TestedOn;
import org.junit.runner.RunWith;

import com.collaborne.jsonschema.generator.AbstractGenerator;
import com.collaborne.jsonschema.generator.CodeGenerationException;
import com.collaborne.jsonschema.generator.Generator;
import com.collaborne.jsonschema.generator.Generator.Feature;
import com.collaborne.jsonschema.generator.java.ClassName;

@RunWith(Theories.class)
public class GenerateMojoTest {
	private static class DummyGenerator extends AbstractGenerator {
		@Override
		public ClassName generate(URI type) throws CodeGenerationException {
			throw new UnsupportedOperationException("Generator#generate() is not implemented");
		}
	}

	@Theory
	public void configureFeatureBooleanSetsFeature(boolean value) {
		Generator generator = new DummyGenerator();

		URI featureUri = URI.create("http://example.com/feature/boolean");
		GenerateMojo mojo = new GenerateMojo();
		mojo.configureFeature(generator, featureUri.toASCIIString(), Boolean.toString(value));
		assertEquals(Boolean.valueOf(value), generator.getFeature(new Feature<>(featureUri.toASCIIString(), Boolean.class, Boolean.FALSE)));
	}

	@Theory
	public void configureFeatureIntegerSetsFeature(@TestedOn(ints={-1, 0, 5}) int value) {
		Generator generator = new DummyGenerator();

		URI featureUri = URI.create("http://example.com/feature/integer");
		GenerateMojo mojo = new GenerateMojo();
		mojo.configureFeature(generator, featureUri.toASCIIString(), Integer.toString(value));
		assertEquals(Integer.valueOf(value), generator.getFeature(new Feature<>(featureUri.toASCIIString(), Integer.class, Integer.MIN_VALUE)));
	}

	@Theory
	public void configureFeatureNumberWithIntegerValueSetsFeature(@TestedOn(ints={-1, 0, 5}) int value) {
		Generator generator = new DummyGenerator();

		URI featureUri = URI.create("http://example.com/feature/number");
		GenerateMojo mojo = new GenerateMojo();
		mojo.configureFeature(generator, featureUri.toASCIIString(), Integer.toString(value));
		assertEquals(value, generator.getFeature(new Feature<>(featureUri.toASCIIString(), Number.class, Double.MIN_VALUE)).doubleValue(), 0.0);
	}

	@Theory
	public void configureFeatureNumberWithDoubleValueSetsFeature(@TestedOn(ints={-1, 0, 5}) int value) {
		Generator generator = new DummyGenerator();

		URI featureUri = URI.create("http://example.com/feature/number");
		GenerateMojo mojo = new GenerateMojo();
		mojo.configureFeature(generator, featureUri.toASCIIString(), Double.toString(value));
		assertEquals(value, generator.getFeature(new Feature<>(featureUri.toASCIIString(), Number.class, Double.MIN_VALUE)).doubleValue(), 0.0);
	}

	@Test
	public void configureFeatureStringSetsFeature() {
		String value = UUID.randomUUID().toString();
		Generator generator = new DummyGenerator();

		URI featureUri = URI.create("http://example.com/feature/string");
		GenerateMojo mojo = new GenerateMojo();
		mojo.configureFeature(generator, featureUri.toASCIIString(), value);
		assertEquals(value, generator.getFeature(new Feature<>(featureUri.toASCIIString(), String.class, null)));
	}
}
