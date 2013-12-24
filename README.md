# felix-maven-plugin

This plugin is used to run concrete Apache Felix configuration with installed OSGi bundles. Also is capable to do assembly.
My main motivation is to have some maven plugin which I can run and debug OSGi stuff.

## Goals overview

felix:run - Start the Apache Felix instance with bundles and configuration you specified in plugin
felix:assembly - Create the Apache Felix distribution with bundles and configuration you specified.

## Example

    <project>
        [...]
        <build>
            <plugins>
                <plugin>
				    <groupId>org.zdevra</groupId>
				    <artifactId>felix-maven-plugin</artifactId>
				    <version>1.0.0</version>
                    <configuration>
                        <osgiProperties>
                            <org.osgi.framework.bootdelegation>sun.*,com.sun.*</org.osgi.framework.bootdelegation>
                        </osgiProperties>
                    </configuration>
                    <bundles>
                        <bundle>maven:org.apache.felix:org.apache.felix.bundlerepository:1.6.6</bundle>
                        <bundle>maven:org.apache.felix:org.apache.felix.shell.remote:1.1.2</bundle>
                        <bundle>maven:org.apache.felix:org.apache.felix.gogo.runtime:0.10.0</bundle>
                        <bundle>maven:org.apache.felix:org.apache.felix.gogo.shell:0.10.0</bundle>
                        <bundle>maven:org.apache.felix:org.apache.felix.gogo.command:0.12.0</bundle>
                    </bundles>
                </plugin>
            </plugins>
        </build>
        [...]
    </project>

If you wish to run the Apache felix with some system properties, then you could use the `systemPoperties` parameter.

    <plugin>
        [...]
        <configuration>
            [...]
            <systemPropeties>
                <log4j.configuration>${baseDir}/log4j.conf</log4j.configuration>
            </systemPropeties>
            [...]
        </configuration>
        [...]
    </plugin>


This is the very small configuration of Apache Felix. If you wish to run it, just type

    mvn felix:run

There is another goal `assembly` which is very usefull in modules with maven assembly plugin where is created distribution.
This goal copy all bundles you specified, create configuration you specified and copy also `felix.jar` into `target/felix`
folder. The very basic setup for assembly could be:

    <plugin>
        <groupId>org.zdevra</groupId>
        <artifactId>felix-maven-plugin</artifactId>
        <version>1.0.0</version>
        <executions>
            <execution>
                <id>distro-assembly</id>
                <phase>prepare-package</phase>
                <goals>
                    <goal>assembly</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            [...]
        </configuraton>
        [...]
    </plugin>

## Authors

* Zdenko Vrabel [@sn3d](http://github.com/sn3d)

##Licencing

Copyright (C) 2013 Zdenko Vrabel Licensed under the Apache License, Version 2.0
