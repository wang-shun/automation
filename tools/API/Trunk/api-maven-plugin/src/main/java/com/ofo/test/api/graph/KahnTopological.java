package com.ofo.test.api.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class KahnTopological {

    public static List<String> sort(Digraph di) {
        // STEP1: compute indegree for each node
        Map<String, Integer> indegrees = di.getIndegrees();
        // STEP2: put zero indegree node into zeros
        Queue<String> zeros = new LinkedList<String>();
        for (String node : indegrees.keySet()) {
            if (0 == indegrees.get(node)) {
                zeros.add(node);
            }
        }
        // STEP3: loop to remove node with zero indegree
        List<String> result = new ArrayList<String>();
        int edges = di.getE();
        while (!zeros.isEmpty()) {
            // STEP3.1: remove one node with zero indegree
            String node = zeros.poll();
            result.add(node);
            // STEP3.2: update indegrees of other node
            Set<String> adjs = di.getAdjs(node);
            for (String adj : adjs) {
                --edges;
                int indegree = indegrees.get(adj) - 1;
                indegrees.put(adj, indegree);
                if (0 == indegree) {
                    zeros.add(adj);
                }
            }
        }

        // cycle exists in digraph if edges isn't zero
        if (0 != edges) {
            StringBuilder sb = new StringBuilder();
            sb.append("Order case: ");
            for (String node : indegrees.keySet()) {
                int indegree = indegrees.get(node);
                if (0 != indegree) {
                    sb.append(node);
                    sb.append(" ");
                }
            }
            sb.append("has cycle!");
            throw new IllegalArgumentException(sb.toString());
        }
        return result;
    }
}
