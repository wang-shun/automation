package com.gome.test.api.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Digraph {

    private Set<String> nodes;
    private Map<String, Set<String>> edges;

    public Digraph() {
        nodes = new HashSet<String>();
        edges = new HashMap<String, Set<String>>();
    }

    public void addNode(String node) {
        nodes.add(node);
    }

    public void addEdge(String from, String to) {
        Set<String> adjacencies = edges.get(from);
        if (null == adjacencies) {
            adjacencies = new HashSet<String>();
            edges.put(from, adjacencies);
        }
        adjacencies.add(to);
    }

    public Map<String, Integer> getIndegrees() {
        Map<String, Integer> indegrees = new HashMap<String, Integer>();
        for (String node : nodes) {
            indegrees.put(node, 0);
        }

        for (String from : edges.keySet()) {
            Set<String> tos = edges.get(from);
            for (String to : tos) {
                indegrees.put(to, indegrees.get(to) + 1);
            }
        }
        return indegrees;
    }

    public Set<String> getAdjs(String node) {
        Set<String> adjs = edges.get(node);
        if (null == adjs) {
            adjs = new HashSet<String>();
        }
        return adjs;
    }

    public int getE() {
        int e = 0;
        for (String from : edges.keySet()) {
            e += edges.get(from).size();
        }
        return e;
    }
}
