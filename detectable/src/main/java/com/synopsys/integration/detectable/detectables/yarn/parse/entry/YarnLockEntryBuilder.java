package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;

public class YarnLockEntryBuilder {
    private boolean metadataEntry = false;
    private final List<YarnLockEntryId> ids = new LinkedList<>();
    private final Map<String, YarnLockEntryId> idMap = new HashMap<>();
    private String version;
    private final Map<String, YarnLockDependency> dependencies = new HashMap<>();

    public YarnLockEntryBuilder addId(YarnLockEntryId id) {
        ids.add(id);
        if (ids.size() > 20) {
            idMap.put(id.getName(), id);
        } else if (ids.size() == 20) {
            ids.forEach(entryId -> {idMap.put(version, entryId);});
        }
        return this;
    }

    public YarnLockEntryBuilder setMetadataEntry(boolean value) {
        this.metadataEntry = value;
        return this;
    }

    public YarnLockEntryBuilder setVersion(String version) {
        this.version = version;
        return this;
    }

    public YarnLockEntryBuilder addDependency(YarnLockDependency dependency) {
        dependencies.put(dependency.getName(), dependency);
        return this;
    }

    public Map<String, YarnLockDependency> getDependencies() {
        return dependencies;
    }

    public boolean valid() {
        return (metadataEntry && (version != null)) || (!ids.isEmpty() && (version != null));
    }

    public YarnLockEntry build() {
        if (!valid()) {
            throw new UnsupportedOperationException("Attempted to build an incomplete yarn.lock entry");
        }
        return new YarnLockEntry(metadataEntry, ids, idMap, version, new LinkedList<>(dependencies.values()));
    }

    public Optional<YarnLockEntry> buildIfValid() {
        if (!valid()) {
            return Optional.empty();
        }
        return Optional.of(new YarnLockEntry(metadataEntry, ids, idMap, version, new LinkedList<>(dependencies.values())));
    }
}
