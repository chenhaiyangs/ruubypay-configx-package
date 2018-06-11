package com.ruubypay.framework.configx.web.util;

import com.ruubypay.framework.configx.web.entity.PropertyItem;
import com.ruubypay.framework.configx.web.service.INodeService;
import org.apache.curator.utils.ZKPaths;

import java.util.List;

/**
 * 克隆版本
 * @author chenhaiyang
 */
public class CloneVersion {

    public static void cloneVersion(String sourceVersionPath, String destinationVersionPath, INodeService nodeService){
        List<String> sourceGroups = nodeService.listChildren(sourceVersionPath);
        if (sourceGroups != null) {
            for (String sourceGroup : sourceGroups) {
                String sourceGroupFullPath = ZKPaths.makePath(sourceVersionPath, sourceGroup);
                String destinationGroupFullPath = ZKPaths.makePath(destinationVersionPath, sourceGroup);

                nodeService.createProperty(destinationGroupFullPath, null);
                List<PropertyItem> sourceProperties = nodeService.findProperties(sourceGroupFullPath);
                if (sourceProperties != null) {
                    for (PropertyItem sourceProperty : sourceProperties) {
                        nodeService.createProperty(ZKPaths.makePath(destinationGroupFullPath, sourceProperty.getName()), sourceProperty.getValue());
                    }
                }
            }
        }
    }
}
