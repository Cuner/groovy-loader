package org.cuner.spring.groovy.loader.test;

import org.cuner.spring.groovy.loader.listener.GroovyRefreshedEvent;
import org.cuner.spring.groovy.loader.listener.GroovyRefreshedListener;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.lang.reflect.Method;

/**
 * Created by houan on 18/6/5.
 */
public class TestListener implements GroovyRefreshedListener {

    public void groovyRefreshed(GroovyRefreshedEvent event) {
        for (String namespace : event.getNamespacedContextMap().keySet()) {
            System.out.println("nameSpace:" + namespace);
            FileSystemXmlApplicationContext context = event.getNamespacedContextMap().get(namespace);
            Object groovyObject = context.getBean("test");
            for (Method method : groovyObject.getClass().getMethods()) {
                try {
                    if (method.getName().contains("test")) {
                        method.invoke(groovyObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
