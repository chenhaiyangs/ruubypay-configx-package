package com.ruubypay.framework.configx.web.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 常用的配置保存
 * @author chenhaiyang
 *
 */
@Service
@Slf4j
public class RootNodeRecorder implements IRootNodeRecorder, Serializable {

	private Set<Object> nodes = Sets.newHashSet();

	private static final String ROOT_NODE_CACHE_FILE="cache/root-node-cache.list";

	@PostConstruct
	private void init() {
		File cacheFile = new File(ROOT_NODE_CACHE_FILE);
		if (cacheFile.exists()) {
			log.info("Loading node caches from file: {}", cacheFile.getAbsoluteFile());
			try {
				List lines = FileUtils.readLines(cacheFile);
                log.info("Load cache data: {}", lines);
				nodes.addAll(lines);
			} catch (IOException e) {
                log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void saveNode(String node) {
		nodes.add(node);
		try {
			FileUtils.writeLines(new File(ROOT_NODE_CACHE_FILE), nodes);
		} catch (IOException e) {
            log.error(e.getMessage(), e);
		}
	}

	@Override
	public List<String> listNode() {
        List<String> resuls= nodes.stream()
                .map(Object::toString)
                .collect(Collectors.toList());

		return Lists.newArrayList(resuls);
	}

}
