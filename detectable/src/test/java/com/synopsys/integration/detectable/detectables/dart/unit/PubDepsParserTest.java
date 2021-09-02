package com.synopsys.integration.detectable.detectables.dart.unit;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.dart.pubdep.PubDepsParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class PubDepsParserTest {
    @Test
    public void test() throws IOException, URISyntaxException {
        File pubDeps = new File(this.getClass().getClassLoader().getResource("detectables/unit/dart/pubDeps.txt").toURI());
        List<String> pubDepsLines = FileUtils.readLines(pubDeps, StandardCharsets.UTF_8);

        PubDepsParser pubDepsParser = new PubDepsParser(new ExternalIdFactory());

        DependencyGraph dependencyGraph = pubDepsParser.parse(pubDepsLines);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.DART, dependencyGraph);

        graphAssert.hasRootSize(4);
        graphAssert.hasRootDependency("charcode", "1.1.3");
        graphAssert.hasRootDependency("pedantic", "1.9.2");
        graphAssert.hasRootDependency("test", "1.15.3");
        graphAssert.hasRootDependency("typed_data", "1.2.0");

        graphAssert.hasParentChildRelationship("analyzer", "0.39.17", "_fe_analyzer_shared", "7.0.0");
        graphAssert.hasParentChildRelationship("glob", "1.2.0", "node_io", "1.1.1");
        graphAssert.hasParentChildRelationship("node_interop", "1.1.1", "js", "1.1.0");
        graphAssert.hasParentChildRelationship("js", "1.1.0", "async", "2.2.1");
        graphAssert.hasParentChildRelationship("async", "2.2.1", "charcode", "1.1.3");
        graphAssert.hasParentChildRelationship("typed_data", "1.2.0", "webkit_inspection_protocol", "0.7.3");

    }
}
