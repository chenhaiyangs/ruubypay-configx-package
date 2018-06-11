package com.ruubypay.framework.configx.test;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.framework.configx.Encrypt;
import com.ruubypay.framework.configx.encrypt.helper.GenerateKeyUtil;
import com.ruubypay.framework.configx.encrypt.impl.EncryptByAes;
import com.ruubypay.framework.configx.zookeeper.ZookeeperConfigGroup;
import com.ruubypay.framework.configx.zookeeper.ZookeeperConfigProfile;
import org.junit.Test;

public class JavaClientTest{


    /**
     * 基本测试
     */
    @Test
    public void testGroup(){
        ZookeeperConfigProfile zookeeperConfigProfile
                = new ZookeeperConfigProfile("localhost:2181",
                "/config/demoproject","1.0.0");

        AbstractGeneralConfigGroup generalConfigGroup = new ZookeeperConfigGroup(zookeeperConfigProfile,"demo-group");
        for(int i=0;i<100;i++){
            try {
                Thread.sleep(10);
                generalConfigGroup.forEach((k,v)->System.out.println(k+":"+v));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试和外部Bean绑定
     */
    @Test
    public void testOuterBean(){
        ZookeeperConfigProfile zookeeperConfigProfile
                = new ZookeeperConfigProfile("localhost:2181",
                "/config/demoproject","1.0.0");

//        DemoBean demoBean = new DemoBean();
//        new ConfigReflectWithOuterBean<>(demoBean,new ZookeeperConfigGroup(zookeeperConfigProfile,"demo-group"));
//
//        for(int i=0;i<100;i++){
//            try {
//                Thread.sleep(10);
//                System.out.println(demoBean);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 测试和内部Bean绑定
     * @throws Exception
     */
    @Test
    public void testInnerBean() throws Exception {
        ZookeeperConfigProfile zookeeperConfigProfile
                = new ZookeeperConfigProfile("localhost:2181",
                "/config/demoproject","1.0.0");

//        ConfigReflectWithInnerBean<DemoBean> innerBean =
//                new ConfigReflectWithInnerBean<>(DemoBean.class,new ZookeeperConfigGroup(zookeeperConfigProfile,"demo-group"));
//
//        for(int i=0;i<100;i++){
//            try {
//                Thread.sleep(10);
//                System.out.println(innerBean.getConfigBean());
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * 测试加解密
     * @throws Exception
     */
    @Test
    public void testEncrypt() throws Exception {
        ZookeeperConfigProfile zookeeperConfigProfile
                = new ZookeeperConfigProfile("localhost:2181",
                "/config/demoproject","1.0.0");

        String src = "22";
        System.out.println("需要被加密的:"+src);
        Encrypt encrypt = new EncryptByAes("1870577f29b17d6787782f35998c4a79");
        String decrypts = encrypt.encrypt(src);
        System.out.println("配置中心加密的:"+decrypts);
        AbstractGeneralConfigGroup generalConfigGroup = new ZookeeperConfigGroup(zookeeperConfigProfile,"demo-group",encrypt);
        String result = generalConfigGroup.get("bFloat");
        System.out.println("框架自动解密的："+result);
    }

}
