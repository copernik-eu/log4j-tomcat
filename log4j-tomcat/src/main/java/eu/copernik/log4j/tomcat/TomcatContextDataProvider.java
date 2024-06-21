/*
 * Copyright © 2023 Piotr P. Karwasz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eu.copernik.log4j.tomcat;

import aQute.bnd.annotation.Resolution;
import aQute.bnd.annotation.spi.ServiceProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.juli.WebappProperties;
import org.apache.logging.log4j.core.util.ContextDataProvider;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

@ServiceProvider(value = ContextDataProvider.class, resolution = Resolution.OPTIONAL)
public class TomcatContextDataProvider implements ContextDataProvider {

    private static final String CONTEXT_DATA_ENABLED = "log4j2.tomcatContextDataEnabled";
    private static final StringMap EMPTY_STRING_MAP = new SortedArrayStringMap();

    static {
        EMPTY_STRING_MAP.freeze();
    }

    private final ConcurrentMap<Integer, Map<String, String>> mapCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, StringMap> stringMapCache = new ConcurrentHashMap<>();
    private final boolean enabled;

    public TomcatContextDataProvider() {
        enabled = PropertiesUtil.getProperties().getBooleanProperty(CONTEXT_DATA_ENABLED);
    }

    @Override
    public Map<String, String> supplyContextData() {
        if (enabled) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final int hashCode = System.identityHashCode(tccl);
            return mapCache.computeIfAbsent(hashCode, __ -> {
                final Map<String, String> map = new HashMap<>(3);
                if (tccl instanceof WebappProperties) {
                    final WebappProperties props = (WebappProperties) tccl;
                    map.put(TomcatLookup.CONTEXT_NAME, props.getWebappName());
                    map.put(TomcatLookup.ENGINE_NAME, props.getServiceName());
                    map.put(TomcatLookup.HOST_NAME, props.getHostName());
                }
                return Collections.unmodifiableMap(map);
            });
        }
        return Collections.emptyMap();
    }

    @Override
    public StringMap supplyStringMap() {
        if (enabled) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final int hashCode = System.identityHashCode(tccl);
            return stringMapCache.computeIfAbsent(hashCode, __ -> {
                final StringMap map = new SortedArrayStringMap(3);
                if (tccl instanceof WebappProperties) {
                    final WebappProperties props = (WebappProperties) tccl;
                    map.putValue(TomcatLookup.CONTEXT_NAME, props.getWebappName());
                    map.putValue(TomcatLookup.ENGINE_NAME, props.getServiceName());
                    map.putValue(TomcatLookup.HOST_NAME, props.getHostName());
                }
                map.freeze();
                return map;
            });
        }
        return EMPTY_STRING_MAP;
    }
}
