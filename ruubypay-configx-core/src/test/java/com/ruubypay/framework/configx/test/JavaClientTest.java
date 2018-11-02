package com.ruubypay.framework.configx.test;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.framework.configx.Encrypt;
import com.ruubypay.framework.configx.encrypt.impl.EncryptByAes;
import com.ruubypay.framework.configx.proxy.ConfigBeanProxy;
import com.ruubypay.framework.configx.zookeeper.ZookeeperConfigGroup;
import com.ruubypay.framework.configx.zookeeper.ZookeeperConfigProfile;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import org.junit.Test;

public class JavaClientTest{


    /**
     * 基本测试
     */
    @Test
    public void testGroup() throws Exception {

        System.setProperty("testByLocal","true");

        ZookeeperConfigProfile zookeeperConfigProfile
                = new ZookeeperConfigProfile("localhost:2181",
                "/config/MISS.PushCenter","1.0.0");

        Encrypt encrypt = new EncryptByAes("a0f574ec51441f3e98011491f28b584d");

        AbstractGeneralConfigGroup generalConfigGroup = new ZookeeperConfigGroup(zookeeperConfigProfile,"datasource-group",encrypt);
        for(int i=0;i<1;i++){
            try {
                Thread.sleep(10);
                generalConfigGroup.forEach((k,v)->System.out.println(k+":"+v));
                System.out.println("============================");
                generalConfigGroup.forEach((k,v)->System.out.println(k+":"+ generalConfigGroup.get(k)));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println(generalConfigGroup.get("mysqlpassword"));
        String password = generalConfigGroup.encryptValue("马大哈");
        generalConfigGroup.set("mysqlpassword",password);
        Thread.sleep(1000);
        System.out.println(generalConfigGroup.get("mysqlpassword"));
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

    /**
     * 测试代理
     */
    @Test
    public void testProxy(){
        DemoBean demoBean = new DemoBean();
        demoBean.setName("测试代理");
        demoBean.setaDouble(22.2);

        ConfigBeanProxy proxy =  new ConfigBeanProxy<DemoBean>(demoBean, null);
        DemoBean b2= (DemoBean) proxy.getProxy();
        System.out.println(demoBean.getName()+"!!!!"+b2.getName());
        b2.setName("修改代理");
        System.out.println(demoBean.getName()+"!!!!"+b2.getName());


    }
}
