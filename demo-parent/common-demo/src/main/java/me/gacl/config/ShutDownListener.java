package me.gacl.config;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.config.DubboShutdownHook;
import org.apache.dubbo.registry.support.AbstractRegistryFactory;
import org.apache.dubbo.rpc.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Iterator;

/**
 * 优雅关机
 * 注意:不建议应用自定义hook，因为java的hook执行是无序的，可能会产生无法预知的问题，
 * 建议应用将自身停机逻辑委托给spring管理
 * */
@Component
public class ShutDownListener implements SmartApplicationListener {

    private static Logger log = LoggerFactory.getLogger(ShutDownListener.class);


    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        //将当前线程的堆栈跟踪打印到标准错误流。它仅用于调试。
        //Thread.currentThread().dumpStack();
        log.info("=============================【优雅停机 start】=============================");
        //停掉dubbo框架自身关闭挂钩hook注册
        DubboShutdownHook.getDubboShutdownHook().unregister();
        //摧毁所有的register对象，并清理缓存数据
        AbstractRegistryFactory.destroyAll();
        /**
         * 避免上游consumer的服务列表未更新完成，provider这时发送当前没有进行中的调用
         * 就立即关闭服务暴露，导致上游consumer调用该服务失败
         * 原因:provider调用zk节点后，理论上consumer收到通知立即更新provider列表，
         * 但因为provider从注册中心撤销服务和consumer将其服务列表删除并不是原子操作
         * */
        try{
            Thread.sleep(5000);
            //关闭心跳与联系
            ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
            Iterator var2 = loader.getLoadedExtensions().iterator();
            while(var2.hasNext()) {
                String protocolName = (String)var2.next();
                Protocol protocol = (Protocol)loader.getLoadedExtension(protocolName);
                 if (protocol != null) {
                    protocol.destroy();
                 }
            }
        }catch (Exception e){
            log.error("DubboShutdownHook exception",e);
        }
        log.info("=============================【优雅停机 end】=============================");

    }
    @Override
    public int getOrder() {
        //order ，数字越小，优先级越高，越先执行
        return 0;
    }
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        //监听spring context close 事件
        return aClass == ContextClosedEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        //设置为true
        return true;
    }


}