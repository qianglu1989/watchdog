package global.redefine.watchdog.utils;

import global.redefine.watchdog.po.TreeNode;

import java.util.List;

/**
 * Created by chenfeng(michaellele) on 2018/11/5.
 *
 * @author chenfeng
 * @email chenfeng@redefine.global
 * @description xxxxxxxx
 */
public class TreeUtils {

    public TreeNode buildTree(TreeNode root, List<TreeNode> treeNodes) {

        treeNodes.forEach(treeNode ->{
            traverse(root, treeNode);
        });
        treeNodes.forEach(treeNode ->{
            if(!treeNode.getParentStatus()) {
                traverse(root, treeNode);
            }
        });
        return root;
    }

    public boolean traverse(TreeNode root, TreeNode node) {
        if(root.node.getSpanId().equals(node.node.getPraentId())) {
            root.getChildNode().add(node);
            node.setParentStatus(true);
            return true;
        }
        if(null != root.getChildNode() && root.getChildNode().size() > 0) {
            for(int i=0; i<root.getChildNode().size(); i++) {
                if(traverse(root.getChildNode().get(i), node)) {
                    return true;
                }
            }
        }
        return false;
    }

//    public static void main(String[] args) {
//        String spans = "[{\"id\":\"c3a17f71-0fb8-40da-a326-5d50d7bd2914\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"parentId\":\"9889b48272fc1f1f\",\"spanId\":\"894dd1948579fd55\",\"kind\":\"CLIENT\",\"name\":\"Node1http:/user/144833\",\"timestamp\":\"1540805923409000\",\"duration\":\"115000\",\"date\":1540805923409,\"localEndpoint\":{\"ipv4\":\"172.23.2.152\",\"port\":\"8703\",\"serviceName\":\"redefine-search\"},\"tags\":{\"spring+instanceid\":\"aliyun-mb-welike-pre-app-02:redefine-search:8703\",\"http+path\":\"/user/144833\",\"http+url\":\"http://aliyun-mb-welike-pre-app-02:8701/user/144833\",\"http+method\":\"GET\",\"http+host\":\"aliyun-mb-welike-pre-app-02\"}},{\"id\":\"5da4b36c-19f8-4f01-b3f1-73244090fd1e\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"spanId\":\"9889b48272fc1f1f\",\"name\":\"Node2hystrix\",\"timestamp\":\"1540805923409000\",\"duration\":\"117216\",\"date\":1540805923409,\"localEndpoint\":{\"ipv4\":\"172.23.2.152\",\"port\":\"8703\",\"serviceName\":\"redefine-search\"},\"tags\":{\"lc\":\"hystrix\",\"thread\":\"hystrix-redefine-feed-22\"}},{\"id\":\"22774af4-61a7-4c5f-85a7-f4f59f1d0988\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"parentId\":\"9889b48272fc1f1f\",\"spanId\":\"894dd1948579fd55\",\"kind\":\"SERVER\",\"name\":\"Node3http:/user/144833\",\"timestamp\":\"1540805923410000\",\"duration\":\"113904\",\"date\":1540805923410,\"localEndpoint\":{\"ipv4\":\"172.23.2.152\",\"port\":\"8701\",\"serviceName\":\"redefine-feed\"},\"tags\":{\"mvc+controller+class\":\"UserController\",\"mvc+controller+method\":\"getUser\",\"spring+instanceid\":\"aliyun-mb-welike-pre-app-02:redefine-feed:8701\",\"REDEFINE_FEED\":\"REQUEST|31d0e553-a10b-4ea1-8185-13af04af6672|http://aliyun-mb-welike-pre-app-02:8701/user/144833|GET|172.23.2.152|global.redefine.welike.feed.controllers.UserController.getUser|{\\\"arg1\\\":{\\\"detail\\\":{},\\\"id\\\":\\\"144833\\\"}}\"}},{\"id\":\"991b6111-c435-4341-9665-08c6a928a9af\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"parentId\":\"894dd1948579fd55\",\"spanId\":\"5214b2a9b6121ceb\",\"name\":\"Node4load-follow-state-async\",\"timestamp\":\"1540805923411000\",\"duration\":\"28\",\"date\":1540805923411,\"localEndpoint\":{\"ipv4\":\"172.23.2.152\",\"port\":\"8701\",\"serviceName\":\"redefine-feed\"},\"tags\":{\"method\":\"loadFollowStateAsync\",\"lc\":\"async\",\"class\":\"ThirdPartyService\"}},{\"id\":\"63e8bd3b-6fd9-47aa-86aa-98b4ac54646f\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"parentId\":\"3b65bfbeb1aec671\",\"spanId\":\"57a571a21e659a05\",\"kind\":\"CLIENT\",\"name\":\"Node5http:/user/batch/follow-states\",\"timestamp\":\"1540805923411000\",\"duration\":\"112000\",\"date\":1540805923411,\"localEndpoint\":{\"ipv4\":\"172.23.2.152\",\"port\":\"8701\",\"serviceName\":\"redefine-feed\"},\"tags\":{\"spring+instanceid\":\"aliyun-mb-welike-pre-app-02:redefine-feed:8701\",\"http+path\":\"/user/batch/follow-states\",\"http+url\":\"http://aliyun-mb-welike-pre-app-07:8701/user/batch/follow-states\",\"http+method\":\"POST\",\"http+host\":\"aliyun-mb-welike-pre-app-07\"}},{\"id\":\"af9d2ae3-1764-4b3c-a2a2-cd3f8de619da\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"parentId\":\"894dd1948579fd55\",\"spanId\":\"3b65bfbeb1aec671\",\"name\":\"Node6get-all-follow-users-count-async\",\"timestamp\":\"1540805923411000\",\"duration\":\"112163\",\"date\":1540805923411,\"localEndpoint\":{\"ipv4\":\"172.23.2.152\",\"port\":\"8701\",\"serviceName\":\"redefine-feed\"},\"tags\":{\"method\":\"getAllFollowUsersCountAsync\",\"lc\":\"async\",\"thread\":\"hystrix-redefine-relationship-48\",\"class\":\"ThirdPartyService\"}},{\"id\":\"2a066f04-9048-4bb3-8f92-170084ac494f\",\"class\":\"com.redefine.monitor.model.UltronSpan\",\"traceId\":\"9889b48272fc1f1f\",\"parentId\":\"3b65bfbeb1aec671\",\"spanId\":\"57a571a21e659a05\",\"kind\":\"SERVER\",\"name\":\"Node7http:/user/batch/follow-states\",\"timestamp\":\"1540805923525000\",\"duration\":\"3271\",\"date\":1540805923525,\"localEndpoint\":{\"ipv4\":\"172.23.2.155\",\"port\":\"8701\",\"serviceName\":\"redefine-relationship\"},\"tags\":{\"mvc+controller+class\":\"UserController\",\"mvc+controller+method\":\"getFollowUserCount\",\"spring+instanceid\":\"aliyun-mb-welike-pre-app-07:redefine-relationship:8701\"}}]";
//        List<UltronSpan> ultronSpans = JSON.parseArray(spans, UltronSpan.class);
//
//        List<Node> nodes = new LinkedList<>();
//        List<TreeNode> treeNodes  = new LinkedList<>();
//
//        TreeNode sourceNode =new TreeNode();
//
//        boolean isSource = false;
//        for(UltronSpan span : ultronSpans){
//            Node node = Node.builder().name(span.getName()).duration(span.getDuration()).localEndpoint(span.getLocalEndpoint()).path(span.getName()).
//                    praentId(span.getParentId()).tarceId(span.getTraceId()).spanId(span.getSpanId()).tags(span.getTags()).build();
//            nodes.add(node);
//            TreeNode treeNode = new TreeNode();
//            treeNode.setNode(node);
//            treeNodes.add(treeNode);
//            //System.out.println(node.getName());
//        }
//        TreeUtils treeUtils = new TreeUtils();
//
//        if(null != treeNodes && treeNodes.size() > 0) {
//            for(int i=0; i<treeNodes.size(); i++) {
//                if(null == treeNodes.get(i).getNode().getPraentId())
//                    sourceNode = treeNodes.get(i);
//            }
//        }
//        treeNodes.remove(sourceNode);
//        System.out.println(treeNodes.size());
//        System.out.println(JSON.toJSONString(treeUtils.buildTree(sourceNode, treeNodes), SerializerFeature.DisableCircularReferenceDetect));
//
//    }
}
