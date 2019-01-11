package com.ruubypay.framework.configx.web.web;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.ruubypay.framework.configx.web.business.INodeBusiness;
import com.ruubypay.framework.configx.web.entity.CommonResponse;
import com.ruubypay.framework.configx.web.entity.PropertyItemVO;
import com.ruubypay.framework.configx.web.service.INodeService;
import com.ruubypay.framework.configx.web.util.CloneVersion;
import com.ruubypay.framework.configx.web.util.FormatPeoperties;
import com.ruubypay.framework.configx.web.util.SecturyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.utils.ZKPaths;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 跳转到配置中心首页
 * @author chenhaiyang
 */
@Slf4j
@Controller
public class IndexController {

    /**
     * 注解分组前缀
     */
    private static final String COMMENT_SUFFIX = "$";
    /**
     * 导出zip格式的文件
     */
    private static final String ZIP = ".zip";
    /**
     * 导出某个组的properties配置文件
     */
    private static final String PROPERTIES = ".properties";

    @Resource
    private INodeService iNodeService;

    @Resource
    private INodeBusiness iNodeBusiness;

    /**
     * 首页
     * @return 首页
     */
    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public String index() {
        return "redirect:/version";
    }

    /**
     * 新建版本
     * @param version 版本信息
     * @param fromVersion 克隆自版本
     * @return 返回版本创建结果
     */
    @PostMapping(value = "/version/{version:.+}")
    public @ResponseBody
    CommonResponse<Object> createVersion(@PathVariable String version, String fromVersion) {
        log.debug("Create version {} from {}", version, fromVersion);

        version = StringUtils.trim(version);
        fromVersion = StringUtils.trim(fromVersion);
        if (!Strings.isNullOrEmpty(version)) {

            final String root = SecturyUtil.getRoot();
            final String versionNode = ZKPaths.makePath(root,version);
            boolean suc = iNodeService.createProperty(versionNode,null);
            if (suc) {
                iNodeService.createProperty(versionNode + COMMENT_SUFFIX,null);
                if (!Strings.isNullOrEmpty(fromVersion)) {
                    final String fromVersionNode = ZKPaths.makePath(root, fromVersion);
                    CloneVersion.cloneVersion(fromVersionNode, versionNode,iNodeService);
                    CloneVersion.cloneVersion(fromVersionNode + COMMENT_SUFFIX, versionNode + COMMENT_SUFFIX,iNodeService);
                }

                return new CommonResponse<>(true, "/version/" + version, null);
            }
        }
        return new CommonResponse<>(false, null, "参数无效");
    }

    /**
     * 查询某个版本的所有配置组
     * @param version 版本
     * @return 版本
     */
    @GetMapping(value = {"/version", "/version/{version:.+}"})
    public ModelAndView rootNode(@PathVariable(required = false) String version) {

        final String root = SecturyUtil.getRoot();
        final List<String> versions = iNodeService.listChildren(root)
                .stream()
                .filter(e -> !e.endsWith(COMMENT_SUFFIX))
                .sorted(Comparator.comparing(String::toString).reversed())
                .collect(Collectors.toList());

        final String theVersion = Optional.ofNullable(version)
                                          .orElseGet(()->Iterables.getFirst(versions, null));


        final ModelAndView mv = new ModelAndView("index");
        mv.addObject("root", root);
        mv.addObject("versions", versions);
        mv.addObject("theVersion", theVersion);

        if (Iterables.contains(versions, theVersion)) {

            final List<String> groups = iNodeService.listChildren(ZKPaths.makePath(root, theVersion))
                    .stream().sorted().collect(Collectors.toList());
            mv.addObject("groups", groups);
        }

        return mv;
    }

    /**
     * 获取具体版本的组配置
     * @param version 版本
     * @param group 组
     * @return 返回
     */
    @GetMapping(value = "/group/{version}/{group:.+}")
    public ModelAndView groupData(@PathVariable String version, @PathVariable String group) {

        final String root = SecturyUtil.getRoot();
        final List<PropertyItemVO> items = iNodeBusiness.findPropertyItems(root,version,group);

        final ModelAndView mv = new ModelAndView("data", "items", items);
        mv.addObject("version", version);
        mv.addObject("group", group);

        return mv;
    }

    /**
     * 删除一整个组的配置
     * @param version 版本
     * @param group 组
     * @return 返回删除结果
     */
    @DeleteMapping(value = "/group/{version}/{group:.+}")
    public @ResponseBody
    CommonResponse<Object> deleteGroup(@PathVariable String version, @PathVariable String group) {
        log.debug("Delete group version: {}, group: {}", version, group);

        final String root = SecturyUtil.getRoot();
        final String versionPath = ZKPaths.makePath(root,version,group);
        iNodeService.deleteProperty(versionPath);
        return new CommonResponse<>(true, null, null);
    }

    /**
     * 新增配置组
     * @param version 版本
     * @param newGroup 组名
     * @return 重定向到页面
     */
    @PostMapping(value = "/group/{version:.+}")
    public ModelAndView createGroup(@PathVariable String version, String newGroup) {
        version = StringUtils.trim(version);
        newGroup = StringUtils.trim(newGroup.trim());

        final String root = SecturyUtil.getRoot();
        final String groupPath = ZKPaths.makePath(root,version,newGroup);

        iNodeService.createProperty(groupPath,null);

        return new ModelAndView("redirect:/version/" + version);
    }

    /**
     * 新建属性
     * @param version 版本
     * @param group 组
     * @param key key
     * @param value value
     * @param comment 注释
     * @return 创建结果
     */
    @PostMapping(value = "/create")
    public @ResponseBody
    CommonResponse<Object> createProperty(String version, String group, String key, String value, String comment) {
        log.debug("Create property version: {}, group: {}, key: {}, value: {}, comment: {}",
                version, group, key, value, comment);

        version = StringUtils.trim(version);
        group = StringUtils.trim(group);
        key = StringUtils.trim(key);
        value = StringUtils.trim(value);
        comment = StringUtils.trim(comment);

        if(StringUtils.isBlank(version)||StringUtils.isBlank(group)||StringUtils.isBlank(key)){
            return new CommonResponse<>(true, null, null);
        }

        final String root = SecturyUtil.getRoot();

        final String propPath = ZKPaths.makePath(root, version, group, key);
        final boolean suc = iNodeService.createProperty(propPath, value);

        if (suc) {
            if (!Strings.isNullOrEmpty(comment)) {
                final String commentPath = ZKPaths.makePath(root, version + COMMENT_SUFFIX, group, key);
                iNodeService.createProperty(commentPath, comment);
            }

            return new CommonResponse<>(true, null, null);
        }
        return new CommonResponse<>(false, null, "服务异常，请稍后重试");
    }

    /**
     * 新建属性
     * @param version 版本
     * @param group 组
     * @param key key
     * @param value value
     * @param comment 注释
     * @return 创建结果
     */
    @PostMapping(value = "/update")
    public @ResponseBody
    CommonResponse<Object> updateProperty(String version, String group, String key, String value, String comment) {
        log.debug("update property version: {}, group: {}, key: {}, value: {}, comment: {}",
                version, group, key, value, comment);

        version = StringUtils.trim(version);
        group = StringUtils.trim(group);
        key = StringUtils.trim(key);
        value = StringUtils.trim(value);
        comment = StringUtils.trim(comment);

        if(StringUtils.isBlank(version)||StringUtils.isBlank(group)||StringUtils.isBlank(key)){
            return new CommonResponse<>(true, null, null);
        }

        final String root = SecturyUtil.getRoot();

        final String propPath = ZKPaths.makePath(root, version, group, key);
        final boolean suc = iNodeService.updateProperty(propPath, value);

        if (suc) {
            if (!Strings.isNullOrEmpty(comment)) {
                final String commentPath = ZKPaths.makePath(root, version + COMMENT_SUFFIX, group, key);
                iNodeService.updateProperty(commentPath, comment);
            }

            return new CommonResponse<>(true, null, null);
        }
        return new CommonResponse<>(false, null, "服务异常，请稍后重试");
    }

    /**
     * 移除属性
     * @param version 版本
     * @param group 组
     * @param key key
     * @return 返回属性值
     */
    @DeleteMapping(value = "/delete/{version}/{group}/{key:.+}")
    public @ResponseBody
    CommonResponse<Object> deleteProp(@PathVariable String version, @PathVariable String group, @PathVariable String key) {
        log.debug("Delete property version: {}, group: {}, key: {}", version, group, key);
        final String root = SecturyUtil.getRoot();
        final String propPath = ZKPaths.makePath(root, version, group, key);
        iNodeService.deleteProperty(propPath);
        return new CommonResponse<>(true, null, null);
    }

    /**
     * 导出配置
     * @param version 版本
     * @param group 组
     * @return 返回信息
     */
    @GetMapping(value = {"/export/{version:.+}", "/export/{version}/{group:.+}"})
    public @ResponseBody
    HttpEntity<byte[]> exportData(@PathVariable String version, @PathVariable(required = false) String group) {

        final String root = SecturyUtil.getRoot();
        if (!Strings.isNullOrEmpty(group)) {
            //export group
            final List<PropertyItemVO> items = iNodeBusiness.findPropertyItems(root, version, group);
            final List<String> lines = FormatPeoperties.formatPropertyLines(root, version, group, items);

            byte[] document = Joiner.on("\r\n").join(lines).getBytes();
            HttpHeaders header = new HttpHeaders();
            header.setContentType(new MediaType("application", "properties"));
            header.set("Content-Disposition", "inline; filename=" + group + PROPERTIES);
            header.setContentLength(document.length);
            return new HttpEntity<>(document, header);
        } else {
            //按照版本导出
            final String versionPath = ZKPaths.makePath(root, version);
            List<String> groups = iNodeService.listChildren(versionPath);
            if (groups != null && !groups.isEmpty()) {
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    try(ZipOutputStream zipOutputStream = new ZipOutputStream(out)) {
                        for (String groupName : groups) {
                            String groupPath =  ZKPaths.makePath(versionPath, groupName);
                            String fileName = ZKPaths.getNodeFromPath(groupPath) + PROPERTIES;

                            List<PropertyItemVO> items = iNodeBusiness.findPropertyItems(root, version, groupName);
                            List<String> lines = FormatPeoperties.formatPropertyLines(root, version, groupName, items);
                            if (!lines.isEmpty()) {
                                ZipEntry zipEntry = new ZipEntry(fileName);
                                zipOutputStream.putNextEntry(zipEntry);
                                IOUtils.writeLines(lines, "\r\n", zipOutputStream, Charsets.UTF_8.displayName());
                                zipOutputStream.closeEntry();
                            }
                        }
                    }
                    byte[] document = out.toByteArray();
                    HttpHeaders header = new HttpHeaders();
                    header.setContentType(new MediaType("application", "zip"));
                    header.set("Content-Disposition", "inline; filename=" + StringUtils.replace(root, "/", "-") + ZIP);
                    header.setContentLength(document.length);
                    return new HttpEntity<>(document, header);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            return null;
        }
    }

    /**
     * 导入配置
     * @param version 版本
     * @param file 文件
     * @return 返回导入结果
     */
    @PostMapping("/import/{version:.+}")
    public ModelAndView importData(@PathVariable String version, MultipartFile file){
        final String fileName = file.getOriginalFilename();
        log.info("Upload file : {}", fileName);

        try (InputStream in = file.getInputStream()) {
            if (fileName!=null && fileName.endsWith(PROPERTIES)) {
                saveGroup(version, fileName, in);

            } else if (fileName!=null && fileName.endsWith(ZIP)) {
                try (ZipArchiveInputStream input = new ZipArchiveInputStream(in)) {
                    ArchiveEntry nextEntry;
                    while ((nextEntry = input.getNextEntry()) != null) {
                        String entryName = nextEntry.getName();
                        saveGroup(version, entryName, input);
                    }
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return new ModelAndView("redirect:/version/" + version);
    }

    /**
     * 将每一个组的配置导入配置中心
     * @param version 版本
     * @param fileName 文件名
     * @param in 输入流
     * @throws IOException IOException
     */
    private void saveGroup(String version, String fileName, InputStream in) throws IOException {

        final String root= SecturyUtil.getRoot();
        List<PropertyItemVO> items = FormatPeoperties.parseInputFile(in);
        if(!items.isEmpty()) {
            final String group = Files.getNameWithoutExtension(fileName);
            final String dataPath = ZKPaths.makePath(root, version, group);
            final String commentPath = ZKPaths.makePath(root, version + COMMENT_SUFFIX, group);

            items.forEach(item -> {
                iNodeService.createProperty(ZKPaths.makePath(dataPath, StringUtils.trim(item.getName())), StringUtils.trim(item.getValue()));
                iNodeService.createProperty(ZKPaths.makePath(commentPath, StringUtils.trim(item.getName())), StringUtils.trim(item.getComment()));
            });
        }
    }
}
