[![codecov](https://codecov.io/gh/copernik-eu/log4j-plugins/graph/badge.svg?token=CTUJTRHWXR)](https://codecov.io/gh/copernik-eu/log4j-plugins)

# Copernik.eu Log4j2 plugins

Copernik.eu Log4j2 plugins is a loose set of plugins for the Log4j2 Core logging system.

## `log4j-tomcat`

The `log4j-tomcat` artifact contains an alternative `WebappClassloader` that allows a web application to use a common
version of the Log4j2 API if it is available on the Tomcat server. To use it:

 * add `log4j-tomcat` to your application,
 * add a `META-INF/context.xml` file (cf. [defining a context](https://tomcat.apache.org/tomcat-10.0-doc/config/context.html#Defining_a_context) with content:

```lang-xml
   <Context>
       <Loader loaderClass="eu.copernik.log4j.tomcat.Log4jWebappClassLoader"/>
   </Context>
```

The artifact contains also a "${tomcat:...}" lookup that resolves all the `classloader.*` properties available in
[Tomcat JULI](https://tomcat.apache.org/tomcat-10.0-doc/logging.html#Using_java.util.logging_(default)).


| Placeholder | Value |
|-|-|
| ${tomcat:catalina.engine.name}<br/>${tomcat:classloader.serviceName} | The name of the Tomcat's `Engine` container (e.g. "Catalina") |
| ${tomcat:catalina.engine.logger} | The logger name used by Tomcat's engine |
| ${tomcat:catalina.host.name}<br/>${tomcat:classloader.hostName} | The name of the Tomcat's `Host` container (e.g. "localhost") |
| ${tomcat:catalina.host.logger} | The logger name used by Tomcat's host |
| ${tomcat:catalina.context.name}<br/>${tomcat:classloader.webappName} | The name of the Tomcat's `Context` container |
| ${tomcat:catalina.context.logger} | The logger name used by Tomcat's context (e.g. by `ServletContext#log`) |

This allows to write a single Log4j2 configuration for all deployed application, e.g.:

```lang-xml
<Configuration name="Default">
    <Appenders>
        <File name="FILE"
              fileName="${sys:catalina.base}/logs/${tomcat:catalina.engine.name}/${tomcat:catalina.host.name}/${tomcat:catalina.context.name}.log"/>
    </Appenders>
    <Loggers>
        <Root level="INFO">
            <AppenderRef ref="FILE"/>
        </Root>
    </Loggers>
</Configuration>
```

## License

Copernik.eu Log4j2 plugins is distributed under the
[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
