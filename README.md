[![Build Status](https://travis-ci.org/Collaborne/json-schema-bean-generator-maven-plugin.svg?branch=master)](https://travis-ci.org/Collaborne/json-schema-bean-generator-maven-plugin)

JSON Schema Bean Generator Maven Plugin
==================================

An Apache Maven (3.2.1+) plugin that processes JSON schema files in the project into
Java beans.


Use Cases
---------

Assuming you already have a JSON schema for your data, this plugin helps to avoid code duplication
by generating Java classes that work with that data for you.

Installation
------------

The plugin is available in Sonatype's 'snapshots' repository, so you will first need to add this
repository:

    <repositories>
      <repository>
        <id>sonatype-snapshots</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
          <enabled>true</enabled>
        </snapshots>
      </repository>
    </repositories>

After that you should be able to add the plugin:

    <plugins>
      <plugin>
        <groupId>com.collaborne.maven</groupId>
        <artifactId>json-schema-bean-generator-maven-plugin</artifactId>
        <version>1.0-SNAPSHOTS</version>
        <executions>
          <execution>
            <id>generate-beans</id>
            <phase>generate-sources</phase>
            <goals>
              <goal>generate</goal>
            </goals>
            <configuration>
              <sourceDirectory>src/main/schemas</sourceDirectory>
              <includes>
                <include>*.json</include>
              </includes>
              <mappingDirectory>src/main/mappings</mappingDirectory>
              <mappingIncludes>
                <mappingInclude>*.json</mappingInclude>
              </mappingIncludes>
              <rootUri>http://example.com/schemas/</rootUri>
              <outputDirectory>${project.build.directory}/generated-sources/beans</outputDirectory>
            </configuration>
          </execution>
        </executions>
      </plugin>
      ...
    </plugins>


Goals
-----

There is only one available goal, `generate`. `generate` processes the included files in the
given `sourceDirectory` into beans in the `outputDirectory`. The `outputDirectory` is added
as additional source directory for the following compile steps.

Any mappings found in `mappingDirectory` are applied as part of the generation process, and relative
URIs are resolved against the given `rootUri`.


Configuration
-------------

Setting            | Default Value                                        | Description
-------------------|------------------------------------------------------|-------------
`sourceDirectory`  | `src/main/schemas`                                   | The directory to scan for JSON files to process
`includes`         | `*.json`                                             | A list of include patterns to process
`mappingDirectory` | `src/main/mappings`                                  | The directory to scan for mapping files
`mappingIncludes`  | `*.json`                                             | A list of include patterns for `mappingDirectory`
`rootUri`          | (none)                                               | URI to use as base when resolving relative references. If not given it is the URI for `sourceDirectory`.
`outputDirectory`  | `${project.build.directory}/generated-sources/beans` | The output directory for generated sources

License
-------

> Copyright 2015 Collaborne B.V.
>
> Licensed under the Apache License, Version 2.0 (the "License");
> you may not use this file except in compliance with the License.
> You may obtain a copy of the License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.

