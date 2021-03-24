/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck.developer;

public class RapidScanComponentDetail {
    private final String component;
    private final String version;
    private final String componentIdentifier;
    private final RapidScanComponentGroupDetail componentDetails;
    private final RapidScanComponentGroupDetail securityDetails;
    private final RapidScanComponentGroupDetail licenseDetails;

    public RapidScanComponentDetail(String component, String version, String componentIdentifier, RapidScanComponentGroupDetail componentDetails,
        RapidScanComponentGroupDetail securityDetails, RapidScanComponentGroupDetail licenseDetails) {
        this.component = component;
        this.version = version;
        this.componentIdentifier = componentIdentifier;
        this.componentDetails = componentDetails;
        this.securityDetails = securityDetails;
        this.licenseDetails = licenseDetails;
    }

    public boolean hasErrors() {
        return componentDetails.hasErrors() || securityDetails.hasErrors() || licenseDetails.hasErrors();
    }

    public boolean hasWarnings() {
        return componentDetails.hasWarnings() || securityDetails.hasWarnings() || licenseDetails.hasWarnings();
    }

    public String getComponent() {
        return component;
    }

    public String getVersion() {
        return version;
    }

    public String getComponentIdentifier() {
        return componentIdentifier;
    }

    public RapidScanComponentGroupDetail getComponentDetails() {
        return componentDetails;
    }

    public RapidScanComponentGroupDetail getSecurityDetails() {
        return securityDetails;
    }

    public RapidScanComponentGroupDetail getLicenseDetails() {
        return licenseDetails;
    }

    public int getComponentErrors() {
        return getGroupErrorCount(componentDetails);
    }

    public int getComponentWarnings() {
        return getGroupWarningCount(componentDetails);
    }

    public int getSecurityErrors() {
        return getGroupErrorCount(securityDetails);
    }

    public int getSecurityWarnings() {
        return getGroupWarningCount(securityDetails);
    }

    public int getLicenseErrors() {
        return getGroupErrorCount(licenseDetails);
    }

    public int getLicenseWarnings() {
        return getGroupWarningCount(licenseDetails);
    }

    private int getGroupErrorCount(RapidScanComponentGroupDetail groupDetail) {
        return groupDetail.getErrorMessages().size();
    }

    private int getGroupWarningCount(RapidScanComponentGroupDetail groupDetail) {
        return groupDetail.getWarningMessages().size();
    }
}
