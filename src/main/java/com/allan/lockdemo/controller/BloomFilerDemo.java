package com.allan.lockdemo.controller;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.*;

/**
 * 布隆过滤器 demo
 */
public class BloomFilerDemo {

    //数据量大小
    private static int size = 1000000;
    //误判率（布隆默认为0.03）
    private static double fdd = 0.03;
    //定义布隆过滤器
    private static BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), size, fdd);
    //不可重复集合（用于测试布隆过滤器的误判率）
    private static Set<String> set = new HashSet<>(size);//set 集合
    private static List<String> list = new ArrayList<>(size);


    /**
     * 测试方法
     */
    @Test
    public void BloomFilerTest() {
        //把原始添加到布隆过滤器中(三容器中存放100万个随机值，并且是唯一字符串)
        for (int i = 0; i < size; i++) {
            String uuid = UUID.randomUUID().toString();//生产随机值
            bloomFilter.put(uuid);
            set.add(uuid);
            list.add(uuid);
        }

        int right = 0;
        int wrong = 0;

        for (int i = 0; i < 10000; i++) {
            String test = i % 100 == 0 ? list.get(i / 100) : UUID.randomUUID().toString();
            if (bloomFilter.mightContain(test)) {
                if (set.contains(test)) {
                    right++;
                } else {
                    wrong++;
                }
            }
        }

        System.out.println("正确：" + right);
        System.out.println("误判：" + wrong);

    }

}
