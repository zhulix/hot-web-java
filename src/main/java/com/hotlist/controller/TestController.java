package com.hotlist.controller;

import com.hotlist.common.R;
import com.hotlist.common.dto.HotSiteDto;
import com.hotlist.common.dto.PostTestDto;
import com.hotlist.common.dto.PostWrapper;
import com.hotlist.service.HotRdbService;
import com.hotlist.service.HotResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Deprecated
public class TestController {

    private static final String HOT = "hot";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private HotRdbService hotRdbService;

    @Resource
    private HotResourceService hotResourceService;

    @PostMapping("/postTest")
    public R addResourceTest(@RequestBody HotSiteDto hotSiteDto) {
        hotRdbService.savePost(hotSiteDto);
        return R.ok();
    }

    @GetMapping("/getTest")
    public R getResourceTest() {

//        String key = "hot:acfun:default:default:pc";
        String key = "hot:douban:default:default:pc";
        Object resourceByKey = hotResourceService.getResourceByKey(key);

        return R.ok().put("data", resourceByKey);
    }

    @GetMapping("/getTest/{key}")
    public R getResource(@PathVariable("key") String key) {
//        String key = "hot:douban:default:default:pc";
        Object resourceByKey = hotResourceService.getResourceByKey(key);

        return R.ok().put("data", resourceByKey);
    }

}
