package com.ruubypay.framework.configx.local;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

/**
 * 本地配置的实现 properties配置文件
 * @author chenhaiyang
 */
public class LocalPropertiesConfigGroup extends AbstractGeneralConfigGroup {
    /**
     * 配置文件的后缀
     */
    private  static final String SUFFIX=".properties";

    /**
     * 配置文件名称
     * @param fileName 文件名
     */
    public LocalPropertiesConfigGroup(String fileName){
        if(fileName==null || fileName.length()==0){
            throw new UnsupportedOperationException("fileNam=null");
        }
        FileInputStream is = null;
        try {
            Properties configProperties = new Properties();
            URL url= Thread.currentThread().getContextClassLoader().getResource(fileName + SUFFIX);
            if(url==null){
                throw new IllegalArgumentException(fileName + SUFFIX+"file not found");
            }
            String propertiesFilePath =url.getPath();
            propertiesFilePath = URLDecoder.decode(propertiesFilePath, "UTF-8");
            is = new FileInputStream(propertiesFilePath);

            configProperties.load(new InputStreamReader(is,"UTF-8"));

            configProperties.forEach((k,v)->super.put(k.toString(),configProperties.getProperty(k.toString())));
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    //ignore it
                }
            }
        }
    }

    @Override
    public void close() throws IOException {

    }
}
