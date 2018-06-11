package com.ruubypay.framework.configx.web.web;

import com.ruubypay.framework.configx.web.entity.Tip;
import com.ruubypay.framework.configx.web.service.IRootNodeRecorder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 输入配置提醒
 * @author chenhaiyang
 */
@Controller
public class RootNodeTipsController {

    @Resource
    private IRootNodeRecorder iRootNodeRecorder;
    /**
     * 首页输入提醒
     * @return tips
     */
    @RequestMapping("/tips")
    public @ResponseBody
    List<Tip> getTips(){
       List<String> tips = iRootNodeRecorder.listNode();

       return tips.stream()
               .map(tip->new Tip(tip,""))
               .collect(Collectors.toList());
    }
}
