package me.gacl.file;

import com.alibaba.fastjson.JSONArray;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义扩展点
 * 调用拦截
 * @author yangzhenyu
 * */
public class DubboFliter implements Filter {
    private static Logger log = LoggerFactory.getLogger(DubboFliter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String method = invocation.getMethodName();
        String name = invoker.getInterface().getName();
        Object[] args = invocation.getArguments();
        String ageString = JSONArray.toJSONString(args);
        log.info("《《==========调用服务【{}】-方法【{}】==========》》",name,method);
        log.info("========== 参数:【{}】",ageString);
        return invoker.invoke(invocation);
    }

}
