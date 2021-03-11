package me.gacl.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 测试Vo
 * @author yangzhenyu
 * */
public class TestVo implements Serializable {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -5023112818896544461L;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("年龄")
    private int age;
    @ApiModelProperty("多任务并行处理的任务数")
    private int index;
    @ApiModelProperty("灰度索引")
    private long indexGray;

    public long getIndexGray() {
        return indexGray;
    }

    public void setIndexGray(long indexGray) {
        this.indexGray = indexGray;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
