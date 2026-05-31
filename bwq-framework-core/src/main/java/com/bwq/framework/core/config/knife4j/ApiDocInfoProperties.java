package com.bwq.framework.core.config.knife4j;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author bwq
 */
@Getter
@Setter
@ConditionalOnProperty(name = "knife4j.enabled" , havingValue = "true")
@ConfigurationProperties(prefix ="springdoc.info")
public class ApiDocInfoProperties {

    /**
     * API文档标题
     */
    private String title = "请及时完善文档标题";

    /**
     * API文档版本
     */
    private String version = "v1.0.0";

    /**
     * API文档描述
     */
    private String description = "请及时完善文档简介";


    /**
     * 服务条款
     */
    private String termsOfService = "暂无服务条款";

    /**
     * 联系人信息
     */
    private Contact contact = new Contact();

    /**
     * 许可证信息
     */
    private License license = new License();

    @Data
    public static class Contact {
        /**
         * 联系人姓名
         */
        private String name = "毕先生";
        /**
         * 联系人主页
         */
        private String url = "暂无主页";
        /**
         * 联系人邮箱
         */
        private String email = "2450790846@qq.com";

    }

    /**
     * 许可证信息
     */
    @Data
    public static  class  License{
        /**
         * 许可证名称
         */
        private String name = "暂无许可证";
        /**
         * 许可证URL
         */
        private String url = "";
    }

}
