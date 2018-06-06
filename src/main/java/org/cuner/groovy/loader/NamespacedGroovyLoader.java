package org.cuner.groovy.loader;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.cuner.groovy.loader.listener.DefaultGroovyRefreshedListener;
import org.cuner.groovy.loader.listener.GroovyRefreshedEvent;
import org.cuner.groovy.loader.listener.GroovyRefreshedListener;
import org.cuner.groovy.loader.trigger.GroovyRefreshTrigger;
import org.cuner.groovy.loader.trigger.ResourceModifiedTrigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by houan on 18/6/5.
 */
public class NamespacedGroovyLoader implements ApplicationListener<ContextRefreshedEvent> {

    private ApplicationContext parentContext;

    private Map<String, FileSystemXmlApplicationContext> namespacedContext;

    private List<FileSystemXmlApplicationContext> toDestoryContext;

    private ConcurrentHashMap<String, Long> resourcesLastModifiedMap;

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
            // do schedule
            reloadScriptAndRefresh();
        }
    }

    private void reloadScriptAndRefresh() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        if (trigger.isTriggered(resourcesLastModifiedMap, groovyResourcesDir)) {
                            // reload
                            Thread.sleep(3000);
                            initLoadResources();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void initLoadResources() {
        if (MapUtils.isNotEmpty(this.namespacedContext)) {
            toDestoryContext = new ArrayList<FileSystemXmlApplicationContext>(this.namespacedContext.values());
        }
        this.namespacedContext = new HashMap<String, FileSystemXmlApplicationContext>();
        this.resourcesLastModifiedMap = new ConcurrentHashMap<String, Long>();
        //定位资源文件路径
        if (StringUtils.isBlank(groovyResourcesDir)) {
            groovyResourcesDir = this.getClass().getClassLoader().getResource("").getPath() + "/spring/groovy";
        }
        File groovyFileDir = new File(groovyResourcesDir);
        List<File> groovyFileList = getResourceListFromDir(groovyFileDir);
        for (File file : groovyFileList) {
            FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(new String[] {file.toURI().toString()}, true, parentContext);
            this.namespacedContext.put(file.getName().replace("xml", ""), context);
            this.resourcesLastModifiedMap.put(file.getName(), file.lastModified());
        }

        //触发监听器时间
        listener.groovyRefreshed(new GroovyRefreshedEvent(parentContext, this.namespacedContext));

        if (CollectionUtils.isNotEmpty(toDestoryContext)) {
            for (FileSystemXmlApplicationContext fileSystemXmlApplicationContext : toDestoryContext) {
                fileSystemXmlApplicationContext.close();
            }
        }
    }

    public static List<File> getResourceListFromDir(File dir) {
        List<File> fileList = new ArrayList<File>();
        if (dir.isDirectory()) {
            File[] subFiles = dir.listFiles();
            if (subFiles == null || subFiles.length < 1) {
                return fileList;
            }
            for (File subFile : subFiles) {
                if (subFile.isDirectory()) {
                    fileList.addAll(getResourceListFromDir(subFile));
                } else {
                    if (subFile.getName().endsWith(".xml")) {
                        fileList.add(subFile);
                    }
                }
            }
        } else {
            if (dir.getName().endsWith(".xml")) {
                fileList.add(dir);
            }
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
