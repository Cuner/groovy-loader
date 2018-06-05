package org.cuner.spring.groovy.loader;

import org.apache.commons.collections4.MapUtils;
import org.cuner.spring.groovy.loader.listener.DefaultGroovyRefreshedListener;
import org.cuner.spring.groovy.loader.listener.GroovyRefreshedEvent;
import org.cuner.spring.groovy.loader.listener.GroovyRefreshedListener;
import org.cuner.spring.groovy.loader.trigger.GroovyRefreshTrigger;
import org.cuner.spring.groovy.loader.trigger.ResourceModifiedTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by houan on 18/6/5.
 */
public class NamespacedGroovyLoader implements ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext parentContext;

    private Map<String, FileSystemXmlApplicationContext> namespacedContext;

    private Map<String, Long> resourcesLastModifiedMap;

    private GroovyRefreshTrigger trigger;

    private GroovyRefreshedListener listener;

    private AtomicBoolean loaded = new AtomicBoolean(false);

    private String groovyResourcesDir;


    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        this.parentContext = contextRefreshedEvent.getApplicationContext();
        if (loaded.compareAndSet(false, true)) {
            if (trigger == null) {
                trigger = new ResourceModifiedTrigger();
            }
            if (listener == null) {
                listener = new DefaultGroovyRefreshedListener();
            }
            initLoadResources();
        }
        // do schedule
    }

    private void initLoadResources() {
        if (MapUtils.isNotEmpty(this.namespacedContext)) {
            for (FileSystemXmlApplicationContext fileSystemXmlApplicationContext : this.namespacedContext.values()) {
                fileSystemXmlApplicationContext.close();
            }
        }
        this.namespacedContext = new HashMap<String, FileSystemXmlApplicationContext>();
        this.resourcesLastModifiedMap = new HashMap<String, Long>();
        //定位资源文件路径
        String path = this.getClass().getClassLoader().getResource("").getPath();
        File groovyFileDir = new File(path + groovyResourcesDir);
        List<File> groovyFileList = getFileListFromDir(groovyFileDir);
        for (File file : groovyFileList) {
            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(new String[] {file.toURI().toString()}, true, parentContext);
            this.namespacedContext.put(file.getName(), context);
            this.resourcesLastModifiedMap.put(file.getName(), file.lastModified());
        }

        //触发监听器时间
        listener.groovyRefreshed(new GroovyRefreshedEvent(parentContext, this.namespacedContext));
    }

    private List<File> getFileListFromDir(File dir) {
        List<File> fileList = new ArrayList<File>();
        if (dir.isDirectory()) {
            File[] subFiles = dir.listFiles();
            if (subFiles == null || subFiles.length < 1) {
                return fileList;
            }
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) {
                    fileList.addAll(getFileListFromDir(subFile));
                } else {
                    fileList.add(subFile);
                }
            }
        } else {
            fileList.add(dir);
        }
        return fileList;
    }

    public GroovyRefreshTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(GroovyRefreshTrigger trigger) {
        this.trigger = trigger;
    }

    public GroovyRefreshedListener getListener() {
        return listener;
    }

    public void setListener(GroovyRefreshedListener listener) {
        this.listener = listener;
    }

    public String getGroovyResourcesDir() {
        return groovyResourcesDir;
    }

    public void setGroovyResourcesDir(String groovyResourcesDir) {
        this.groovyResourcesDir = groovyResourcesDir;
    }
}
