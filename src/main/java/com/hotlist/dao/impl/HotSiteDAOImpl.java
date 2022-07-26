package com.hotlist.dao.impl;

import com.alibaba.fastjson2.JSON;
import com.hotlist.dao.HotSiteDAO;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.entity.UserEntity;
import com.hotlist.utils.HotUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class HotSiteDAOImpl implements HotSiteDAO {

    public static class RedisKey {

        public static String resourceKey(UserEntity user) {
            return HotUtil.stringJoin("hot", "site", user.getUserName(), "res");
        }

        public static String siteHashKey(UserEntity user) {
            return HotUtil.stringJoin("hot", "site", user.getUserName());
        }
        private static String resourceListKeyPostfix(HotSiteEntity hotSite) {
            return HotUtil.stringJoin(hotSite.getAlias(), hotSite.getHotRankList(),
                    HotUtil.categoryJoin(hotSite.getHotRankListCategory()), "pc");
        }

        public static String resourceListKey(HotSiteEntity hotSite, UserEntity user) {
            return HotUtil.stringJoin(resourceKey(user), resourceListKeyPostfix(hotSite));
        }

        public static String resourceRefreshKey(HotSiteEntity hotSite, UserEntity user) {
            return HotUtil.stringJoin("refreshKey", resourceListKey(hotSite, user));
        }
//        public static String siteHashKey() {
//            return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName());

//        }

//        public static String resourceKey() {
//            return HotUtil.stringJoin("hot", "site", HotContext.getCurrentUser().getUserName(), "res");

//        }
//        public static String resourceListKey(HotSiteEntity hotSite) {
//            return HotUtil.stringJoin(resourceKey(), resourceListKeyPostfix(hotSite));

//        }
//        public static String resourceRefreshKey(HotSiteEntity hotSite) {
//            return HotUtil.stringJoin("refreshKey", resourceListKey(hotSite));
//        }
    }


    public HotSiteDAOImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<HotSiteEntity> findSiteByUser(UserEntity user) {
        String siteHashKey = RedisKey.siteHashKey(user);
//        ScanOptions options = ScanOptions.scanOptions().count(50).match(siteHashKey).build();
//        RedisConnection redisConnection = redisConnectionFactory.getConnection();
//        Cursor<byte[]> scan = redisConnection.scan(options);
//        List<String> siteKey = new ArrayList<>();
//        while (scan.hasNext()) {
//            String key = StringUtils.replace(new String(scan.next()), "USERS:", "");
//            siteKey.add(key);
//        }
//        redisTemplate.opsForHash().multiGet(siteHashKey, )
        // TODO O(N)
        Set<Object> keys = redisTemplate.opsForHash().keys(siteHashKey);
        List<Object> siteList = redisTemplate.opsForHash().multiGet(siteHashKey, keys);
        if (!CollectionUtils.isEmpty(siteList))
            return siteList.stream().map(k -> JSON.to(HotSiteEntity.class, k)).collect(Collectors.toList());
        return null;
    }

    @Override
    public void saveSite(HotSiteEntity site, UserEntity user) {
        String siteHashKey = RedisKey.siteHashKey(user);
        String siteKey = RedisKey.resourceListKeyPostfix(site);
        redisTemplate.opsForHash().put(siteHashKey, siteKey, site);
    }

    @Override
    public void delSite(HotSiteEntity site, UserEntity user) {
        String siteHashKey = RedisKey.siteHashKey(user);
        String siteKey = RedisKey.resourceListKeyPostfix(site);
        redisTemplate.opsForHash().delete(siteHashKey, siteKey);
    }
}
