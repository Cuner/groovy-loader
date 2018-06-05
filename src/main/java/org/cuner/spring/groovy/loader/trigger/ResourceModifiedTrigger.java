package org.cuner.spring.groovy.loader.trigger;

import java.util.Map;

/**
 * Created by houan on 18/6/5.
 */
public class ResourceModifiedTrigger implements GroovyRefreshTrigger {
    public boolean isTriggered(Map<String, Long> scriptLastModifiedMap) {
        return false;
    }
}
