package org.cuner.groovy.loader.listener;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.util.Map;

/**
 * Created by houan on 18/6/5.
 */
public class GroovyRefreshedEvent {

    private ApplicationContext ancestorContext;

    private Map<String, FileSystemXmlApplicationContext> namespacedContextMap;

    public GroovyRefreshedEvent(ApplicationContext ancestorContext, Map<String, FileSystemXmlApplicationContext> contextMap) {
        this.ancestorContext = ancestorContext;
        this.namespacedContextMap = contextMap;
    }

    public Map<String, FileSystemXmlApplicationContext> getNamespacedContextMap() {
        return namespacedContextMap;
    }

    public void setNamespacedContextMap(Map<String, FileSystemXmlApplicationContext> namespacedContextMap) {
        this.namespacedContextMap = namespacedContextMap;
    }

    public ApplicationContext getAncestorContext() {
        return ancestorContext;
    }

    public void setAncestorContext(ApplicationContext ancestorContext) {
        this.ancestorContext = ancestorContext;
    }
}
