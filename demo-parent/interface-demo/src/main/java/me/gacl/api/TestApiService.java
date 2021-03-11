package me.gacl.api;

import me.gacl.vo.TestVo;

import java.util.concurrent.CompletableFuture;

/**
 * 测试
 * @author yangzhenyu
 * */
public interface TestApiService {
    /**
     * 测试接口
     * */
     String testApi(TestVo vo);
     /**
      * 声明异步接口
      * */
    default CompletableFuture<String> testApiAsync(TestVo vo) {
        return CompletableFuture.completedFuture(testApi(vo));
    }
}
