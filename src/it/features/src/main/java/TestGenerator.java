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
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

import com.collaborne.jsonschema.generator.AbstractGenerator;
import com.collaborne.jsonschema.generator.CodeGenerationException;
import com.collaborne.jsonschema.generator.java.ClassName;

public class TestGenerator extends AbstractGenerator {
	private static final Feature<Integer> FEATURE_INTEGER = new Feature<Integer>("http://example.com/features/integer", Integer.class, null);
	private static final Feature<Boolean> FEATURE_BOOLEAN = new Feature<Boolean>("http://example.com/features/boolean", Boolean.class, null);
	private static final Feature<Double> FEATURE_DOUBLE = new Feature<Double>("http://example.com/features/double", Double.class, null);
	private static final Feature<String> FEATURE_STRING = new Feature<String>("http://example.com/features/string", String.class, null);

	@Override
	public ClassName generate(URI type) throws CodeGenerationException {
		// Just validate our features
		Object value = getFeature(FEATURE_INTEGER);
		if (!Integer.valueOf(1).equals(value)) {
			throw new CodeGenerationException(type, "Integer: " + value);
		}
		value = getFeature(FEATURE_BOOLEAN);
		if (!Boolean.TRUE.equals(value)) {
			throw new CodeGenerationException(type, "Boolean: " + value);
		}
		value = getFeature(FEATURE_DOUBLE);
		if (!Double.valueOf(1.0).equals(value)) {
			throw new CodeGenerationException(type, "Double: " + value);
		}
		value = getFeature(FEATURE_STRING);
		if (!"value".equals(value)) {
			throw new CodeGenerationException(type, "String: " + value);
		}

		// Write a marker that we were executed. The verify script checks that one.
		try {
			Files.createDirectories(getOutputDirectory());
			Files.write(getOutputDirectory().resolve("marker"), new byte[0]);
		} catch (IOException e) {
			throw new CodeGenerationException(type, "Cannot create marker", e);
		}

		return null;
	}
}
