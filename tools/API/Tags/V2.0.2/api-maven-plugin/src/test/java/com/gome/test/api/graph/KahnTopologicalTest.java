package com.gome.test.api.graph;

import com.gome.test.api.graph.Digraph;
import com.gome.test.api.graph.KahnTopological;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class KahnTopologicalTest {

    @Test(dataProvider = "testSortDataProvider")
    public void testSort(String[] nodes, String[][] edges) {
        Digraph graph = new Digraph();
        for (String node : nodes) {
            graph.addNode(node);
        }
        for (int i = 0; i < edges.length; ++i) {
            graph.addEdge(edges[i][0], edges[i][1]);
        }
        KahnTopological.sort(graph);
    }

    @DataProvider(name = "testSortDataProvider")
    private Object[][] testGetRelativePathDataProvider() {
        return new Object[][]{
                {
                        new String[]{
                                "ordercase_001",
                                "ordercase_002",
                                "ordercase_003",
                                "ordercase_004",
                                "ordercase_005"
                        },
                        new String[][]{
                                new String[]{"ordercase_003", "ordercase_001"},
                                new String[]{"ordercase_001", "ordercase_002"},
                                new String[]{"ordercase_005", "ordercase_004"},
                                new String[]{"ordercase_003", "ordercase_005"}
                        }
                }
        };
    }
}
