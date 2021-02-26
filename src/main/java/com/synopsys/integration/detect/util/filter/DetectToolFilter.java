/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.util.filter;

import java.util.Optional;

import com.synopsys.integration.detect.configuration.ExcludeIncludeEnumFilter;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;

public class DetectToolFilter {
    private final ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter;
    private final Optional<Boolean> deprecatedSigScanDisabled;
    private final Optional<Boolean> deprecatedPolarisEnabled;

    public DetectToolFilter(ExcludeIncludeEnumFilter<DetectTool> excludedIncludedFilter, final Optional<Boolean> deprecatedSigScanDisabled, final Optional<Boolean> deprecatedPolarisEnabled) {
        this.excludedIncludedFilter = excludedIncludedFilter;

        this.deprecatedSigScanDisabled = deprecatedSigScanDisabled;
        this.deprecatedPolarisEnabled = deprecatedPolarisEnabled;
    }

    public boolean shouldInclude(final DetectTool detectTool) {

        if (detectTool == DetectTool.SIGNATURE_SCAN && deprecatedSigScanDisabled.isPresent()) {
            return !deprecatedSigScanDisabled.get();
        } else if (detectTool == DetectTool.POLARIS && deprecatedPolarisEnabled.isPresent()) {
            return deprecatedPolarisEnabled.get();
        }

        return excludedIncludedFilter.shouldInclude(detectTool);
    }
}
