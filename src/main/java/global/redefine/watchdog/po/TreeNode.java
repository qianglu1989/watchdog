package global.redefine.watchdog.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {

    public Node node;

    public Boolean parentStatus = false;

    public List<TreeNode> childNode = new ArrayList<>();

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public List<TreeNode> getChildNode() {
        return childNode;
    }

    public void setChildNode(List<TreeNode> childNode) {
        this.childNode = childNode;
    }

    public Boolean getParentStatus() {
        return parentStatus;
    }

    public void setParentStatus(Boolean parentStatus) {
        this.parentStatus = parentStatus;
    }
}
