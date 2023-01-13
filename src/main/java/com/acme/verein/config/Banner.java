/*
 * Copyright (C) 2022 - present Juergen Zimmermann, Hochschule Karlsruhe
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.acme.verein.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

import org.apache.catalina.util.ServerInfo;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.security.core.SpringSecurityCoreVersion;

/**
 * Banner als String-Konstante für den Start des Servers.
 *
 * @author <a href="mailto:Juergen.Zimmermann@h-ka.de">Jürgen Zimmermann</a>
 */
@SuppressWarnings({
    "AccessOfSystemProperties",
    "CallToSystemGetenv",
    "UtilityClassCanBeEnum",
    "UtilityClass",
    "ClassUnconnectedToPackage"
})
public final class Banner {
    private static final String JAVA = Runtime.version().toString() + " - " + System.getProperty("java.vendor");
    private static final String OS_VERSION = System.getProperty("os.name");
    private static final InetAddress LOCALHOST = getLocalhost();
    private static final long MEGABYTE = 1024L * 1024L;
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final String SERVICE_HOST = System.getenv("VEREIN_SERVICE_HOST");
    private static final String SERVICE_PORT = System.getenv("VEREIN_SERVICE_PORT");
    private static final String KUBERNETES = SERVICE_HOST == null
        ? "N/A"
        : "VEREIN_SERVICE_HOST=" + SERVICE_HOST + ", VEREIN_SERVICE_PORT=" + SERVICE_PORT;
    private static final String USERNAME = System.getProperty("user.name");

    /**
     * Banner für den Server-Start.
     */
    public static final String TEXT = """

            __                   __             ___
           / /____  ______  ____/ /__     _   _<  /
          / //_/ / / / __ \\/ __  / _ \\   | | / / /
         / ,< / /_/ / / / / /_/ /  __/   | |/ / /
        /_/|_|\\__,_/_/ /_/\\__,_/\\___/    |___/_/

        (C) Juergen Zimmermann, Hochschule Karlsruhe
        Version              1.0
        Spring Boot          %s
        Spring Security      %s
        Spring Framework     %s
        Tomcat               %s
        Java                 %s
        Betriebssystem       %s
        Rechnername          %s
        IP-Adresse           %s
        Heap: Size           %d MiB
        Heap: Free           %d MiB
        Kubernetes           %s
        Username             %s
        JVM Locale           %s
        OpenAPI              /swagger-ui.html /v3/api-docs.yaml
        """
        .formatted(
            SpringBootVersion.getVersion(),
            SpringSecurityCoreVersion.getVersion(),
            SpringVersion.getVersion(),
            ServerInfo.getServerInfo(),
            JAVA,
            OS_VERSION,
            LOCALHOST.getHostName(),
            LOCALHOST.getHostAddress(),
            RUNTIME.totalMemory() / MEGABYTE,
            RUNTIME.freeMemory() / MEGABYTE,
            KUBERNETES,
            USERNAME,
            Locale.getDefault()
        );

    @SuppressWarnings("ImplicitCallToSuper")
    private Banner() {
    }

    private static InetAddress getLocalhost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
