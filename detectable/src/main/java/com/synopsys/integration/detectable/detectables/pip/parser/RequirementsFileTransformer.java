package com.synopsys.integration.detectable.detectables.pip.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class RequirementsFileTransformer {

    private static final List<String> OPERATORS_IN_PRIORITY_ORDER = Arrays.asList("==", ">=", "~=", "<=", ">", "<");
    private static final List<String> IGNORE_AFTER_CHARACTERS = Arrays.asList("#", ";", ",");
    private static final List<String> TOKEN_CLEANUP_CHARS = Arrays.asList("==", ",", "\"");
    private RequirementsFileDependencyVersionParser requirementsFileDependencyVersionParser;
    public RequirementsFileTransformer(
        RequirementsFileDependencyVersionParser requirementsFileDependencyVersionParser
    ) {
        this.requirementsFileDependencyVersionParser = requirementsFileDependencyVersionParser;
    }
    public List<RequirementsFileDependency> transform(File requirementsFileObject) throws IOException {

        List<RequirementsFileDependency> dependencies = new LinkedList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(requirementsFileObject))) {
            for (String line; (line = bufferedReader.readLine()) != null; ) {

                // Ignore comments (i.e. lines starting with #) and empty/whitespace lines.
                String formattedLine = formatLine(line);
                if (formattedLine.isEmpty() || formattedLine.startsWith("#")) {
                    continue;
                }

                // Extract tokens before and after the operator that was found in the line
                List<List<String>> extractedTokens = extractTokens(formattedLine);
                List<String> tokensBeforeOperator = extractedTokens.get(0);
                List<String> tokensAfterOperator = extractedTokens.get(1);

                // Extract dependency. This will always be the first token or a substring of first token for each valid line.
                // Format and cleanup each token
                String dependency = "";
                if (tokensBeforeOperator != null && !tokensBeforeOperator.isEmpty()) {
                    dependency = formatToken(tokensBeforeOperator.get(0));
                }

                // Extract version. Version extracted will be the next token after operator.
                String version = "";
                if (tokensAfterOperator != null && !tokensAfterOperator.isEmpty()) {
                    version = formatToken(tokensAfterOperator.get(0));
                }

                // Create a dependency entry and add it to the list
                // Version can be an empty string but dependency name should always be non-empty
                if (!dependency.isEmpty()) {
                    RequirementsFileDependency requirementsFileDependency = new RequirementsFileDependency(dependency, version);
                    dependencies.add(requirementsFileDependency);
                }
            }
        }
        return dependencies;
    }

    private List<List<String>> extractTokens(String formattedLine) {
        List<String> tokensBeforeOperator = null;
        List<String> tokensAfterOperator = null;
        // Find the operator with its index that separates a dependency from its version.
        List<Object> operatorWithIndex = findOperatorWithIndex(formattedLine);
        String operatorFound = (String) operatorWithIndex.get(0);
        if (!operatorFound.isEmpty()) {
            int operatorStartIndex = (int) operatorWithIndex.get(1);
            int operatorEndIndex = operatorStartIndex + operatorFound.length() - 1;

            // Get strings before and after operator
            String stringBeforeOperator = formattedLine.substring(0, operatorStartIndex).trim();
            String stringAfterOperator = formattedLine.substring(operatorEndIndex + 1).trim();

            // Tokenize based on whitespace as the parser should allow special characters in version and dependency strings
            tokensBeforeOperator = Arrays.asList(stringBeforeOperator.split(" "));
            tokensAfterOperator = Arrays.asList(stringAfterOperator.split(" "));
        }
        return Arrays.asList(tokensBeforeOperator, tokensAfterOperator);
    }

    private List<Object> findOperatorWithIndex(String line) {
        int operatorIndex;
        List<Object> operatorWithIndex = new ArrayList<>();
        for (String operator : OPERATORS_IN_PRIORITY_ORDER) {
            operatorIndex = line.indexOf(operator);
            if (operatorIndex != -1) {
                operatorWithIndex.add(operator);
                operatorWithIndex.add(operatorIndex);
                return operatorWithIndex;
            }
        }
        return Arrays.asList("", -1);
    }

    private String formatLine(String line) {
        int ignoreAfterIndex;
        String formattedLine = line.trim();
        for (String ignoreAfterChar : IGNORE_AFTER_CHARACTERS) {
            ignoreAfterIndex = formattedLine.indexOf(ignoreAfterChar);
            if (ignoreAfterIndex >= 0) {
                formattedLine = formattedLine.substring(0, ignoreAfterIndex);
            }
        }
        return formattedLine;
    }

    private String formatToken(String token) {
        // Clean up any irrelevant symbols/chars from token
        for (String charToRemove : TOKEN_CLEANUP_CHARS) {
            token = token.replace(charToRemove, "");
        }
        // Remove any strings in square brackets. For example, if token is requests["foo", "bar"], it should be cleaned up to show as "requests"
        int bracketIndex = token.indexOf("[");
        if (bracketIndex > 0) {
            token = token.substring(0, bracketIndex);
        }
        return token;
    }
}
