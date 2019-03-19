package global.redefine.watchdog.controller;

import com.alibaba.fastjson.JSON;
import com.redefine.redis.utils.RedefineClusterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by luqiang on 2018/5/18.
 *
 * @author QIANGLU
 */
@RestController
@RequestMapping("/depend")
@CrossOrigin
public class DependController {

    Logger LOG = LoggerFactory.getLogger(DependController.class);

    /**
     * 登陆返回token
     *
     * @return
     */
    @RequestMapping(value = "/dispose", method = RequestMethod.GET)
    public Map<String, Object> dispose() {
        Map<String, String> ids = new HashMap<>();
        List<Map<String, String>> nodes = new LinkedList<>();
        List<Map<String, String>> edges = new LinkedList<>();
        Map<String, Object> result = new HashMap<>();

        Set<byte[]> all = RedefineClusterUtils.sMembers("dependappall");
        all.forEach(s -> {
            try {
                String uid = UUID.randomUUID().toString();
                Map<String, String> data = new HashMap<>();
                String name = new String(s, "UTF-8");
                data.put("id", uid);
                data.put("label", name);
                nodes.add(data);
                ids.put(name, uid);
            } catch (Exception e) {
                LOG.error("dispose error:{}", e);
            }
        });
        Map<Object, Object> depend = RedefineClusterUtils.hgetAll("dependdata");
        depend.forEach((k, v) -> {
            Map<Object, Object> val = JSON.parseObject(String.valueOf(v), Map.class);
            Object from = val.get("from");
            Object to = val.get("to");
            if (from != null && to != null) {
                Map<String, String> rt = new HashMap<>();
                rt.put("from", ids.get(from));
                rt.put("to", ids.get(to));
                rt.put("arrows", "to");
                edges.add(rt);
            }
        });
        result.put("nodes", nodes);
        result.put("edges", edges);

        return result;
    }


}
