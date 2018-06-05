package org.cuner.spring.groovy.loader.trigger;

import java.util.Map;

/**
 * Created by houan on 18/6/5.
 */
public interface GroovyRefreshTrigger {

    boolean isTriggered(Map<String, Long> scriptLastModifiedMap);
}
