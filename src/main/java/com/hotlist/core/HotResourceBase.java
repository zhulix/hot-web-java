package com.hotlist.core;

import com.hotlist.core.filter.FilterFactory;
import com.hotlist.core.filter.HotResultWrapper;
import com.hotlist.entity.HotSiteEntity;
import com.hotlist.utils.HttpAccessUtil;

import java.util.List;

public abstract class HotResourceBase implements HotResource {

    /**
     * job、手动、其他方式传对象过来
     *      需要：redisKey（前提是库里有存好的所有资源）、对象（库里没资源，传过来使用并保存）
     *
     *
     */

    /**
     * 获取网络资源
     *
     * @param hotSiteInfoWrapper 1
     * @return 1
     */
    @Override
    public HotResultWrapper fetch(HotSiteInfoWrapper hotSiteInfoWrapper) {

        String fetchResource = fetchResource(hotSiteInfoWrapper.hotSite);

        // 资源解析操作应该都有这个er完成
        HotResourceParser resourceParser = getParser(fetchResource, hotSiteInfoWrapper.hotSite);

        HotResultWrapper hotResultWrapper = parseResource(resourceParser);

        resolve(hotResultWrapper, resourceParser);
        hotResultWrapper.setHotSite(hotSiteInfoWrapper.hotSite);
        return hotResultWrapper;
    }

    /**
     * http拿资源
     */
    public String fetchResource(HotSiteEntity hotSite) {
        return HttpAccessUtil.get(hotSite.getUrl());
    }

    /**
     * 初步解析
     */
    public abstract HotResultWrapper parseResource(HotResourceParser hotResourceParser);

    /**
     * 得到自己需要的资源
     */
    public abstract void resolve(HotResultWrapper resultWrapper, HotResourceParser hotResourceParser);


    private HotResourceParser getParser(String fetchResource, HotSiteEntity hotSite) {
        HotResourceParser hotResourceParser = new HotResourceParser(fetchResource, hotSite);
        String parseType = hotSite.getParseType();
        if (parseType.equals("json")) {
            hotResourceParser.context.configFilter(FilterFactory.FILTER_MAP.get("json"));
        } else if (parseType.equals("xPath")) {
            hotResourceParser.context.configFilter(FilterFactory.FILTER_MAP.get("document"));
        }
        return hotResourceParser;
    }

}
