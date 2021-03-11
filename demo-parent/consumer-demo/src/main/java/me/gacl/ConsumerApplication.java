
package me.gacl;


import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 微服务应用服务启动类
 * @author yangzhenyu
 * @EnableDubbo 注解是 @EnableDubboConfig 和 @DubboComponentScan两者组合的便捷表达方式
 */
@EnableDubbo(scanBasePackages="me.gacl")
@SpringBootApplication(scanBasePackages={"me.gacl"},exclude = DruidDataSourceAutoConfigure.class)
public class ConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}
            