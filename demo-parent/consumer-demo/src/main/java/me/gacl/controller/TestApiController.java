package me.gacl.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.gacl.api.TestApiService;
import me.gacl.common.ResponseBo;
import me.gacl.vo.TestVo;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.rpc.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/***
 * dubbo 测试
 * @author yangzhenyu
 */
@Api(value = "dubbo 消费者", tags = {"dubbo 消费者"})
@RestController
@RequestMapping(value="/api/commonFile")
public class TestApiController {

    private static Logger log = LoggerFactory.getLogger(TestApiController.class);

    @Reference(version = "${dubbo.consumer.parameters.version}",check=true,loadbalance="${dubbo.provider.loadbalance}")
    private TestApiService service;
    /**
     * dubbo 消费者 调用测试
     * @author yangzhenyu
     * */
    @ApiOperation(value="消费者 调用测试", notes="消费者 调用测试")
    @PostMapping(value = "/test")
    public ResponseBo test(@RequestBody TestVo vo){
        //dubbo隐式传参
        RpcContext.getContext().setAttachment("indexGray", Objects.nonNull(vo.getIndexGray()) ? String.valueOf(vo.getIndexGray()) : "1");
        return ResponseBo.ok(service.testApi(vo));
    }

    /**
     * dubbo 消费者 异步化接口调用测试
     * @author yangzhenyu
     * */
    @ApiOperation(value="消费者 异步化接口调用测试", notes="消费者 异步化接口调用测试")
    @PostMapping(value = "/testAsync")
    public ResponseBo testAsync(@RequestBody TestVo vo){
        //dubbo隐式传参
        RpcContext.getContext().setAttachment("indexGray", Objects.nonNull(vo.getIndexGray()) ? String.valueOf(vo.getIndexGray()) : "1");
        //提供了一个可以原子读写的对象引用变量
        AtomicReference<String> value = new AtomicReference<>("");
        CompletableFuture<String> future = service.testApiAsync(vo);
        boolean falg = true;
        future.whenComplete((retValue, exception) -> {
            if (exception == null) {
                value.set(retValue);
            } else {
                exception.printStackTrace();
            }
        });
        while(falg){
            if (value.get() !=""){
                falg = false;
            }
        }
        return ResponseBo.ok(value.get());
    }

    /**
     * dubbo【2.7】 多任务的并行处理 测试
     * @author yangzhenyu
     * */
    @ApiOperation(value=" dubbo【2.7】 多任务的并行处理 测试", notes=" dubbo【2.7】 多任务的并行处理 测试")
    @PostMapping(value = "/testPool")
    public ResponseBo testPool(@RequestBody TestVo vo){
        //dubbo隐式传参
        RpcContext.getContext().setAttachment("indexGray", Objects.nonNull(vo.getIndexGray()) ? String.valueOf(vo.getIndexGray()) : "1");
        AtomicReference<List<String>> valueList = new AtomicReference<List<String>>(new ArrayList<>());
        boolean falg = true;
        CompletableFuture<List<String>> finalResults = null;
        List<String> accountIdList = new ArrayList<>();
        for (int i=0;i<vo.getIndex();i++){
            accountIdList.add("1");
            falg = false;
        }
        if (!falg){
            List<CompletableFuture<String>> accountFindingFutureList =
                    accountIdList.stream().map(accountId -> service.testApiAsync(vo)).collect(Collectors.toList());
            // 使用allOf方法来表示所有的并行任务
            CompletableFuture<Void> allFutures =
                    CompletableFuture
                            .allOf(accountFindingFutureList.toArray(new CompletableFuture[accountFindingFutureList.size()]));
            // 下面的方法可以帮助我们获得所有子任务的处理结果
            finalResults = allFutures.thenApply(v -> {
                return accountFindingFutureList.stream().map(accountFindingFuture -> accountFindingFuture.join())
                        .collect(Collectors.toList());
            });
            finalResults.whenComplete((retValue, exception) -> {
                if (exception == null) {
                    valueList.set(retValue);
                } else {
                    exception.printStackTrace();
                }
            });
        }

        while(!falg){
            if (valueList.get().size() != 0){
                falg = true;
            }
        }
        /***
         *如果后续逻辑没有必要等待所有子任务全部结束，而是只要任一一个任务成功结束就可以继续执行，我们可以使用CompletableFuture.anyOf方法：
         * CompletableFuture<Object> anyOfFutures = CompletableFuture.anyOf(taskFutureA, taskFutureB, taskFutureC);
         */
        return finalResults == null?ResponseBo.error("子任务返回集合为空"):ResponseBo.ok(valueList.get());
    }
}
