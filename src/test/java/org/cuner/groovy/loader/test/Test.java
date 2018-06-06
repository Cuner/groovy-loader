package org.cuner.groovy.loader.test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by houan on 18/6/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:loader.xml"})
public class Test {

    @org.junit.Test
    public void test() throws Exception{
        System.out.println("loading..");
        Thread.sleep(5 * 60 * 1000);
    }

}
