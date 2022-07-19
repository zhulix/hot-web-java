package com.hotlist.core;

import com.hotlist.core.filter.HotResultWrapper;
import com.hotlist.entity.HotSiteEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class DocumentHotResource extends HotResourceBase {

    @Override
    public void save(HotResultWrapper hotResultWrapper) {
        if (Objects.nonNull(hotResultWrapper.getParsedResource()))
            hotResultWrapper.getHotSite().saveByResource(hotResultWrapper.getParsedResourceAsList());
    }

    @Override
    public HotResultWrapper parseResource(HotResourceParser hotResourceParser) {
        String arrayKey = hotResourceParser.getArrayKey();

        Document parse = Jsoup.parse(hotResourceParser.resource);
        JXNode jxNode = JXDocument.create(parse).selNOne(arrayKey);

        HotResultWrapper hotResultWrapper = new HotResultWrapper(jxNode);
        if (hotResourceParser.context.hasFilter()) {
            hotResourceParser.context.getFilter().doFilter(hotResultWrapper, hotResourceParser.hotSite.getSerializeFilterRuler());
        }

        return hotResultWrapper;
    }

    @Override
    public void resolve(HotResultWrapper resultWrapper, HotResourceParser hotResourceParser) {
        JXNode jxNode = (JXNode) resultWrapper.getParsedResource();
        List<Object> objects = childrenResolve(jxNode, hotResourceParser);
        resultWrapper.setParsedResource(objects);
    }

    private List<Object> childrenResolve(JXNode jxNode, HotResourceParser hotResourceParser) {
        Map<String, String> parseContentMap = hotResourceParser.getParseContent();
        String arrayKey = hotResourceParser.getArrayKey();
        HotSiteEntity.ShowConfig showConfig = hotResourceParser.hotSite.getShowConfig();
        Map<String, String> configMap = showConfig.getConfig();
        parseContentMap.forEach((k, v) -> {
            // 这是arrayList
            // *[@id=\"sanRoot\"]/main/div[2]/div/div[2]
            // *[@id=\"sanRoot\"]/main/div[2]/div/div[2]/div[%s]/div[2]/a/div[1]/text()
            //                                          上面这里就是子节点，直接用这一段xpath解析
            // 解析内容必须是arrayKey中的子节点才能被解析，所以不需要直接从总的document中解析。
            if (v.startsWith(arrayKey)) parseContentMap.put(k, v.substring(arrayKey.length()));
        });


        Set<String> contentKeys = parseContentMap.keySet();
        int childrenSize;
        if (Objects.nonNull(jxNode) && jxNode.isElement()) {
            childrenSize = jxNode.asElement().childrenSize() + 1;
        } else throw new RuntimeException("解析失败");

        List<Object> ans = new ArrayList<>(childrenSize);
        label:
        for (int i = 1; i < childrenSize; i++) {
            Map<String, String> content = new HashMap<>();
            for (String k : contentKeys) {
                try {
                    content.put("timeStamp", String.valueOf(System.currentTimeMillis()));
                    String v = parseContentMap.get(k);
                    JXNode node = jxNode.selOne(String.format(v, i));
                    String val;
                    if (node.isElement() && node.asElement().tag().getName().equals("a")) {
                        val = node.asElement().attributes().get("href");
                    } else {
                        val = jxNode.selOne(String.format(v, i)).asString();
                    }
                    // 替换[]表达式
                    if (configMap.containsKey(k)) val = showConfig.format(k, val, configMap.get(k));
                    content.put(k, val);
                } catch (Exception e) {
//                    log.info(e.getMessage());
                    continue label;
                }
            }
            ans.add(content);
        }
        return ans;
    }
}
