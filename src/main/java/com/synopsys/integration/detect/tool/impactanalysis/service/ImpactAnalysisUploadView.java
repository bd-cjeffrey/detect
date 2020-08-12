/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.util.HashMap;
import java.util.Map;

import com.synopsys.integration.blackduck.api.core.BlackDuckView;
import com.synopsys.integration.blackduck.api.core.response.LinkResponse;
import com.synopsys.integration.blackduck.api.core.response.LinkSingleResponse;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;

public class ImpactAnalysisUploadView extends BlackDuckView {
    public static final Map<String, LinkResponse> links = new HashMap<>();
    public static final String CODE_LOCATION_LINK = "codelocation";
    public static final String SCAN_BOM_ENTRIES_LINK = "scan-bom-entries";

    public static final LinkSingleResponse<CodeLocationView> CODE_LOCATION_LINK_RESPONSE = new LinkSingleResponse<>(CODE_LOCATION_LINK, CodeLocationView.class);
    // public static final LinkMultipleResponses SCAN_BOM_ENTRIES_LINK_RESPONSE = new LinkSingleResponse(SCAN_BOM_ENTRIES_LINK, String.class); TODO: Unknown type.

    static {
        links.put(CODE_LOCATION_LINK, CODE_LOCATION_LINK_RESPONSE);
        // links.put(SCAN_BOM_ENTRIES_LINK, SCAN_BOM_ENTRIES_LINK_RESPONSE);
    }

    private String serverVersion;
    private String status;
    private String statusMessage;
    private String createdAt;
    private String createdByUserName;
    private Integer matchCount;
    private Integer directoryCount;
    private Integer fileCount;
    private String baseDirectory;
    private String hostName;
    private String scanType;
    private String updatedAt;

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByUserName() {
        return createdByUserName;
    }

    public void setCreatedByUserName(String createdByUserName) {
        this.createdByUserName = createdByUserName;
    }

    public Integer getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(Integer matchCount) {
        this.matchCount = matchCount;
    }

    public Integer getDirectoryCount() {
        return directoryCount;
    }

    public void setDirectoryCount(Integer directoryCount) {
        this.directoryCount = directoryCount;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getScanType() {
        return scanType;
    }

    public void setScanType(String scanType) {
        this.scanType = scanType;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
