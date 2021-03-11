package me.gacl.gray;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.loadbalance.AbstractLoadBalance;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
/**
 * 自定义扩展点
 * 灰度发布
 * @author yangzhenyu
 * */
public class GrayLoadBalance extends AbstractLoadBalance {
    public static final String NAME = "gray";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        /**
         * Dubbo 中 invoker 其实就是一个具有调用功能的对象，在服务暴露端封装的就是真实的服务实现，把真实的服务实现封装一下变成一个 invoker
         * */
        List<Invoker<T>> list = new ArrayList<>();
        for (Invoker invoker : invokers) {
            list.add(invoker);
        }
        Map<String, String> map = invocation.getAttachments();
        String userId = map.get("indexGray");

        Iterator<Invoker<T>> iterator = list.iterator();
        String grayUserIds = url.getParameter("grayUserids", "");
        String[] arrs = grayUserIds.split(",");
        while (iterator.hasNext()) {
            Invoker<T> invoker = iterator.next();
            String providerStatus = invoker.getUrl().getParameter("status", "prod");
            if (Objects.equals(providerStatus, NAME)) {
                if (Arrays.asList(arrs).contains(userId)) {
                    return invoker;
                } else {
                    iterator.remove();
                }
            }
        }

        return this.randomSelect(list, url, invocation);
    }

    /**
     * 重写了一遍随机负载策略
     *
     * @param invokers
     * @param url
     * @param invocation
     * @param <T>
     * @return
     */
    private <T> Invoker<T> randomSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int length = invokers.size();
        boolean sameWeight = true;
        int[] weights = new int[length];
        int firstWeight = this.getWeight((Invoker) invokers.get(0), invocation);
        weights[0] = firstWeight;
        int totalWeight = firstWeight;

        int offset;
        int i;
        for (offset = 1; offset < length; ++offset) {
            i = this.getWeight((Invoker) invokers.get(offset), invocation);
            weights[offset] = i;
            totalWeight += i;
            if (sameWeight && i != firstWeight) {
                sameWeight = false;
            }
        }

        if (totalWeight > 0 && !sameWeight) {
            offset = ThreadLocalRandom.current().nextInt(totalWeight);

            for (i = 0; i < length; ++i) {
                offset -= weights[i];
                if (offset < 0) {
                    return (Invoker) invokers.get(i);
                }
            }
        }
        return (Invoker) invokers.get(ThreadLocalRandom.current().nextInt(length));
    }
}
