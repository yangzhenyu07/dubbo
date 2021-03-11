package me.gacl.api.text;

import me.gacl.api.TestApiService;
import me.gacl.vo.TestVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 负载均衡:
 *
 * */

@Service(version = "${dubbo.provider.parameters.version}",timeout = 10000 ,export=true,loadbalance="${dubbo.provider.loadbalance}")
public class TestApiServiceImpl implements TestApiService {
    /**
     * 测试
     * */
    @Transactional(rollbackFor = {Exception.class})
    public String testApi(TestVo vo) {
        return "【"+vo.getName()+"】今年【"+vo.getAge()+"】岁啦！！！";
    }
}
