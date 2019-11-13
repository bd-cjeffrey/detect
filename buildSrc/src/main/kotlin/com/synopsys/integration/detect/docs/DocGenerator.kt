/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.docs

import com.google.gson.Gson
import com.synopsys.integration.detect.docs.copied.HelpJsonData
import com.synopsys.integration.detect.docs.copied.HelpJsonExitCode
import com.synopsys.integration.detect.docs.copied.HelpJsonOption
import freemarker.template.Configuration
import freemarker.template.Template
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream

open class GenerateDocsTask : DefaultTask() {
    @TaskAction
    fun generateDocs() {
        val file = File("synopsys-detect-${project.version}-help.json")
        val helpJson: HelpJsonData = Gson().fromJson(file.reader(), HelpJsonData::class.java)

        val outputDir = File("docs/generated");
        outputDir.deleteRecursively()
        outputDir.mkdirs()

        val templateProvider = TemplateProvider()

        FileUtils.copyDirectory(File("docs/static"), outputDir)

        createFromFreemarker(templateProvider, outputDir, "exit-codes", ExitCodePage(helpJson.exitCodes))
        createFromFreemarker(templateProvider, outputDir, "content/index", IndexPage(project.version.toString()))

        handleDetectors(templateProvider, outputDir, helpJson)
        handleProperties(templateProvider, outputDir, helpJson)
    }

    private fun createFromFreemarker(templateProvider: TemplateProvider, outputDir: File, templateName: String, data: Any) {
        createFromFreemarker(templateProvider, "$templateName.ftl", File(outputDir, "$templateName.md"), data);
    }

    private fun createFromFreemarker(templateProvider: TemplateProvider, templatePath: String, to: File, data: Any) {
        to.parentFile.mkdirs()
        println("******** templatePath: $templatePath")
        val template = templateProvider.getTemplate(templatePath)
        FileOutputStream(to, true).buffered().writer().use { writer ->
            template.process(data, writer)
        }
    }

    private fun handleDetectors(templateProvider: TemplateProvider, outputDir: File, helpJson: HelpJsonData) {
        val build = helpJson.buildDetectors.groupBy { it.detectorType }
                .map { group -> DetectorGroup(group.key, group.value.map { detector -> detector.detectorName }) }

        val buildless = helpJson.buildlessDetectors.groupBy { it.detectorType }
                .map { group -> DetectorGroup(group.key, group.value.map { detector -> detector.detectorName }) }

        createFromFreemarker(templateProvider, outputDir, "detectors", DetectorsPage(buildless, build))
    }

    private fun handleProperties(templateProvider: TemplateProvider, outputDir: File, helpJson: HelpJsonData) {
        val superGroups = createSuperGroupLookup(helpJson);
        val groupLocations = superGroups.entries.associate { it.key to it.value + "/" + it.key } //ex: superGroup/group

        //Updating the location on all the json options so that a new object with only 1 new property did not have to be created (and then populated) from the existing.
        helpJson.options.forEach {
            val groupLocation = groupLocations[it.group] ?: error("Missing group location: ${it.group}")
            it.location = "${groupLocation}#${it.propertyName.replace(" ", "-").toLowerCase()}" //ex: superGroup/group#property_name
        }

        val groupedOptions = helpJson.options.groupBy { it -> it.group }
        val splitGroupOptions = groupedOptions.map { group ->
            val deprecated = group.value.filter { it.deprecated }
            val simple = group.value.filter { !deprecated.contains(it) && it.category == "simple" }
            val advanced = group.value.filter { !simple.contains(it) && !deprecated.contains(it) }
            val superGroupName = superGroups[group.key] ?: error("Missing super group: ${group.key}");
            val groupLocation = groupLocations[group.key] ?: error("Missing group location: ${group.key}")
            SplitGroup(group.key, superGroupName, groupLocation, simple, advanced, deprecated)
        }

        val propertiesFolder = File(outputDir, "properties")
        splitGroupOptions.forEach { group ->
            val superGroupFolder = File(propertiesFolder, group.superGroup)
            val targetMarkdown = File(superGroupFolder, "${group.groupName}.md")
            createFromFreemarker(templateProvider, "property-group.ftl", targetMarkdown, group)
        }

        val simplePropertyTableData = splitGroupOptions
                .filter { it.simple.isNotEmpty() }
                .map { SimplePropertyTableGroup(it.groupName, groupLocations[it.groupName] ?: error("Missing group location: ${it.groupName}"), it.simple) }

        createFromFreemarker(templateProvider, propertiesFolder, "basic-properties", SimplePropertyTablePage(simplePropertyTableData))
        createFromFreemarker(templateProvider, propertiesFolder, "all-properties", AdvancedPropertyTablePage(splitGroupOptions))
    }

    //Technically each group has exactly 1 super group (but this is not enforced in the json) so here we check that assumption and return the mapping.
    //TODO: Add a new object to the helpJson which is a super group lookup so that the super group lookup is not just embedded in the object and then we don't have to do this at all.
    //TODO: Add the default "Configuration" to the help json instead of blank and having this populate.
    private fun createSuperGroupLookup(helpJson: HelpJsonData): HashMap<String, String> {
        val lookup = HashMap<String, String>();
        helpJson.options.forEach { option ->
            val superGroup = if (StringUtils.isBlank(option.superGroup)) "Configuration" else option.superGroup
            if (lookup.containsKey(option.group) && lookup[option.group] != superGroup) {
                throw RuntimeException("The created detect help JSON had a group '${option.group}' whose super group '${superGroup}' did not match a different options super group in the same group '${lookup[option.group]}'.")
            } else if (!lookup.containsKey(option.group)) {
                lookup[option.group] = superGroup
            }
        }
        return lookup;
    }
}

class TemplateProvider {
    private val configuration: Configuration = Configuration(Configuration.VERSION_2_3_26);

    init {
        configuration.setDirectoryForTemplateLoading(File("docs/templates"))
        configuration.defaultEncoding = "UTF-8"
    }

    fun getTemplate(templateName: String): Template {
        return configuration.getTemplate(templateName)
    }
}

data class IndexPage(val version: String) {}
data class ExitCodePage(val exitCodes: List<HelpJsonExitCode>) {}
data class DetectorsPage(val buildless: List<DetectorGroup>, val build: List<DetectorGroup>) {}
data class DetectorGroup(val groupName: String, val detectors: List<String>) {}

data class SimplePropertyTablePage(val groups: List<SimplePropertyTableGroup>) {}
data class SimplePropertyTableGroup(val groupName: String, val location: String, val options: List<HelpJsonOption>) {}
data class AdvancedPropertyTablePage(val groups: List<SplitGroup>) {}
data class SplitGroup(val groupName: String, val superGroup: String, val location: String, val simple: List<HelpJsonOption>, val advanced: List<HelpJsonOption>, val deprecated: List<HelpJsonOption>) {}

