/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gome.test.api.ide.bo;

import com.gome.test.api.ide.model.TreeNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author Zonglin.Li
 */
public class DirTreeNodeBo {
    @Value(value = "${workspace.path}")
    private String workspacePath;

    public List<TreeNode> getChildren(String node, String type) throws Exception {
        if ("folder".equals(type)) {
            return getDirChildrenUnderFolder(node);
        } else {
            return new ArrayList<TreeNode>();
        }
    }

    private List<TreeNode> getDirChildrenUnderFolder(String node) {
        List<TreeNode> dirTreeNodes = new ArrayList<TreeNode>();
        List<TreeNode> orderDirTreeNodes = new ArrayList<TreeNode>();
        File dirPath = new File(String.format("%s/%s", workspacePath, node));
        String osName = System.getProperty("os.name");
        for (File f : dirPath.listFiles()) {
            if (f.isHidden()) {
                continue;
            }
            if (f.isDirectory()) {
                if ("".equals(node)) {
                    TreeNode treeNode = new TreeNode(f.getName(), f.getName(), "folder", true, null);
                    dirTreeNodes.add(treeNode);
                } else {
                    TreeNode treeNode = new TreeNode(String.format("%s/%s", node, f.getName()), f.getName(), "folder", true, null);
                    dirTreeNodes.add(treeNode);
                }
            } else if (f.getName().endsWith(".bat") && osName.startsWith("Windows")) {
                TreeNode treeNode = new TreeNode("", f.getName(), "bat", false, null);
                dirTreeNodes.add(treeNode);
            } else if (f.getName().endsWith(".xlsx")) {
                TreeNode treeNode = new TreeNode("", f.getName(), "xlsx", false, null);
                dirTreeNodes.add(treeNode);
            } else if (f.getName().endsWith(".sh") && (!osName.startsWith("Windows"))) {
                TreeNode treeNode = new TreeNode("", f.getName(), "sh", false, null);
                dirTreeNodes.add(treeNode);
            }
        }
        orderDirTreeNodes = folderPrefer(dirTreeNodes);
        return orderDirTreeNodes;
    }

    private List<TreeNode> folderPrefer(List<TreeNode> dirTreeNodes) {
        List<TreeNode> orderDirTreeNodes = new ArrayList<TreeNode>();

        for (int i = 0; i < dirTreeNodes.size(); i++) {
            if ("folder".equals(dirTreeNodes.get(i).getType())) {
                orderDirTreeNodes.add(dirTreeNodes.get(i));
            }
        }
        for (int i = 0; i < dirTreeNodes.size(); i++) {
            if (!"folder".equals(dirTreeNodes.get(i).getType())) {
                orderDirTreeNodes.add(dirTreeNodes.get(i));
            }
        }
        return orderDirTreeNodes;
    }

    public void openFile(String filePath) {
        String fullFilePath = String.format("%s/%s", workspacePath, filePath);
        File file = new File(fullFilePath);
        String absolutePath = file.getAbsolutePath();
        String osName = System.getProperty("os.name");
        try {
            System.out.println(osName);
            if (osName.startsWith("Windows")) {
                //Windows 
                Runtime.getRuntime().exec("cmd.exe /c start " + absolutePath);
            } else {
                //assume Mac,Unix or Linux 
                if (absolutePath.endsWith(".sh")) {
                    Runtime.getRuntime().exec("sh " + absolutePath);
                } else {
                    Runtime.getRuntime().exec("open " + absolutePath);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}


