package com.ruubypay.framework.configx.web.business;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ruubypay.framework.configx.web.entity.PropertyItem;
import com.ruubypay.framework.configx.web.entity.PropertyItemVO;
import com.ruubypay.framework.configx.web.service.INodeService;
import org.apache.curator.utils.ZKPaths;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 属性节点业务实现
 * @author chenhaiyang
 */
@Service
public class NodeBusiness implements INodeBusiness {

	@Resource
	private INodeService nodeService;

    /**
     * 获取属性列表
     * @param rootNode 根结点
     * @param version 版本
     * @param group 组
     * @return 返回一个组的配置列表
     */
	@Override
	public List<PropertyItemVO> findPropertyItems(String rootNode, String version, String group) {
		List<PropertyItemVO> items = null;
		if (!Strings.isNullOrEmpty(rootNode) && !Strings.isNullOrEmpty(version) && !Strings.isNullOrEmpty(group)) {
			List<PropertyItem> propertyItems = nodeService.findProperties(getGroupFullPath(rootNode, version, group));
			List<PropertyItem> propertyComments = nodeService.findProperties(getGroupCommentFullPath(rootNode, version, group));
			if (propertyItems != null) {
				Map<String, String> comments = Maps.newHashMap();
				if (propertyComments != null) {
					for (PropertyItem comment : propertyComments) {
						comments.put(comment.getName(), comment.getValue());
					}
				}
				items = Lists.newArrayList();

				for (PropertyItem propertyItem : propertyItems) {
					PropertyItemVO vo = new PropertyItemVO(propertyItem);
					vo.setComment(comments.get(propertyItem.getName()));
					items.add(vo);
				}
				Collections.sort(items);
			}
		}
		return items;
	}

	private String getGroupFullPath(String rootNode, String version, String group) {
		String authedNode = ZKPaths.makePath(rootNode, version);
		return ZKPaths.makePath(authedNode, group);
	}

	private String getGroupCommentFullPath(String rootNode, String version, String group) {
		String authedNode = ZKPaths.makePath(rootNode, version + "$");
		return ZKPaths.makePath(authedNode, group);
	}

}
