package com.ruubypay.framework.configx.web.util;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.ruubypay.framework.configx.web.entity.PropertyItemVO;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 格式化配置到文本配置文件中，用于导出功能
 * @author chenhaiyang
 */
public class FormatPeoperties {


    private static final Splitter PROPERTY_SPLITTER = Splitter.on('=').limit(2);

    /**
     * 格式化配置文件
     * @param root 跟节点
     * @param version 版本
     * @param group 组
     * @param items 配置信息
     * @return 返回格式化结果
     */
    public static List<String> formatPropertyLines(String root, String version, String group, List<PropertyItemVO> items) {
        List<String> lines = Lists.newArrayList();
        lines.add(String.format("# Export from zookeeper configuration group: [%s] - [%s] - [%s].", root,
                version, group));
        lines.add("");
        for (PropertyItemVO item : items) {
            if (!Strings.isNullOrEmpty(item.getComment())) {
                lines.add("# " + item.getComment());
            }
            lines.add(item.getName() + "=" + item.getValue());
        }
        return lines;
    }

    /**
     * 解析输入文件
     * @param inputstream 输入流
     * @return 返回配置信息
     * @throws IOException IOException
     */
    @SuppressWarnings("unchecked")
    public static List<PropertyItemVO> parseInputFile(InputStream inputstream) throws IOException {
        List<String> lines = IOUtils.readLines(inputstream, Charsets.UTF_8.name());
        List<PropertyItemVO> items = Lists.newArrayList();
        String previousLine = null;
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if(i == 0 && line.startsWith("# Export from zookeeper")) {
                continue;
            }
            if (!line.startsWith("#")) {
                Iterable<String> parts = PROPERTY_SPLITTER.split(line);
                if (Iterables.size(parts) == 2) {
                    String key=Iterables.getFirst(parts,null);
                    if(key!=null){
                        key=key.trim();
                    }
                    String value=Iterables.getLast(parts).trim();
                    PropertyItemVO item = new PropertyItemVO(key,value);
                    if (previousLine != null && previousLine.startsWith("#")) {
                        item.setComment(org.springframework.util.StringUtils.trimLeadingCharacter(previousLine, '#').trim());
                    }
                    items.add(item);
                }
            }
            previousLine = line;
        }
        return items;
    }
}
