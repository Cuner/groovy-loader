package org.cuner.groovy.loader.trigger;

import org.cuner.groovy.loader.NamespacedGroovyLoader;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by houan on 18/6/5.
 */
public class ResourceModifiedTrigger implements GroovyRefreshTrigger {
    public boolean isTriggered(Map<String, Long> resourcesLastModifiedMap, String groovyResourcesDir) {
        String path = this.getClass().getClassLoader().getResource("").getPath();
        File groovyFileDir = new File(path + groovyResourcesDir);
        List<File> groovyFileList = NamespacedGroovyLoader.getResourceListFromDir(groovyFileDir);
        for (File file : groovyFileList) {
            //新增
            if (!resourcesLastModifiedMap.containsKey(file.getName())) {
                return true;
            } else {
                //修改
                if (resourcesLastModifiedMap.get(file.getName()) != file.lastModified()) {
                    return true;
                }
            }
        }

        //删除
        if (resourcesLastModifiedMap.size() != groovyFileList.size()) {
            return true;
        }
        return false;
    }
}
