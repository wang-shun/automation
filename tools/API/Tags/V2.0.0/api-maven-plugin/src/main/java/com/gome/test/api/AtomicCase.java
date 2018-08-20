package com.gome.test.api;

import java.util.HashSet;
import java.util.Set;

public class AtomicCase {

    private final String id;
    private final String name;
    private final String[] paramKeys;
    private final Object[] paramValues;
    private final String owner;
    private final String priority;
    private final Set<String> groups;

    public AtomicCase(String id, String name, String[] paramKeys,
                      Object[] paramValues, String owner, String priority, Set<String> groups) {
        this.id = id;
        this.name = name;
        this.paramKeys = paramKeys;
        this.paramValues = paramValues;
        this.owner = owner;
        this.priority = priority;
        this.groups = new HashSet<String>();
        this.groups.addAll(groups);
        this.groups.add("AtomicCase");
        this.groups.add("ALL");
        this.groups.add(owner);
        this.groups.add(String.format("Priority%s", priority));
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getParamKeys() {
        return paramKeys;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public String getOwner() {
        return owner;
    }

    public String getPriority() {
        return priority;
    }

    public Set<String> getGroups() {
        return groups;
    }
}
