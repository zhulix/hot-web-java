package com.hotlist.controller;

import com.hotlist.common.R;
import com.hotlist.service.HotResourceService;
import com.hotlist.utils.HttpAccessUtil;
import lombok.extern.slf4j.Slf4j;
import org.seimicrawler.xpath.JXDocument;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Deprecated
public class IndexController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private HotResourceService hotResourceService;

    @GetMapping("/fetchResource")
    public R fetchResource() {
        return R.ok();
    }


    @GetMapping("/test/{key}")
    public R test(@PathVariable("key") String key) {
        Object resourceByKey = hotResourceService.getResourceByKey(key);

        return R.ok().put("data", resourceByKey);
    }

    @GetMapping("/customerTest")
    public R customerTest() {

        String url = stringRedisTemplate.opsForValue().get("hot:baidu:realtime:hotSearch:pc:url");

        BoundHashOperations<String, Object, Object> hashOperations = stringRedisTemplate.boundHashOps("hot:baidu:realtime:hotSearch:parseContent");
        Map<Object, Object> entries = hashOperations.entries();

        String htmlBody = HttpAccessUtil.get(url);
        JXDocument jxDocument = JXDocument.create(htmlBody);
        assert entries != null;
        List<String> res = new ArrayList<>(30);

        Collection<Object> values = entries.values();
        for (int i = 2; i < 30; i++) {
            int finalI = i;
            values.forEach(v -> {
                String s = jxDocument.selNOne(String.format(String.valueOf(v), finalI)).asString();
                res.add(s);
            });
        }

        return R.ok().put("data", res);
    }

}
