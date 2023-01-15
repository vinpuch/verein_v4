@file:Suppress("MissingPackageDeclaration", "SpellCheckingInspection", "GrazieInspection")

/*
* Copyright (C) 2016 - present Juergen Zimmermann, Hochschule Karlsruhe
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
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

//  Aufrufe
//  1) Microservice uebersetzen und starten
//        .\gradlew bootRun [-Dport=8081] [tls=false] [-Ddb=mysql] [-Ddb=oracle] [--args='--debug'] [--continuous]
//        .\gradlew compileJava
//        .\gradlew compileTestJava
//
//  2) Microservice als selbstausfuehrendes JAR erstellen und ausfuehren
//        .\gradlew bootJar
//        java -jar build/libs/....jar --spring.profiles.active=dev
//        .\gradlew bootBuildImage [-Dtag='2.0.0']
//
//  3) Tests und QS
//        .\gradlew test [--rerun-tasks]
//        .\gradlew allureServe
//              EINMALIG>>   .\gradlew downloadAllure
//        .\gradlew jacocoTestReport
//        .\gradlew jacocoTestCoverageVerification
//        .\gradlew checkstyleMain checkstyleTest spotbugsMain spotbugsTest
//        .\gradlew buildHealth
//        .\gradlew reason --id com.fasterxml.jackson.core:jackson-annotations:2.13.3
//
//  4) Sicherheitsueberpruefung durch OWASP Dependency Check und Snyk
//        .\gradlew dependencyCheckAnalyze --info
//        .\gradlew snyk-test
//
//  5) "Dependencies Updates"
//        .\gradlew versions
//        .\gradlew dependencyUpdates
//        .\gradlew checkNewVersions
//
//  6) API-Dokumentation erstellen
//        .\gradlew javadoc
//
//  7) Entwicklerhandbuch in "Software Engineering" erstellen
//        .\gradlew asciidoctor asciidoctorPdf
//
//  8) Projektreport erstellen
//        .\gradlew projectReport
//        .\gradlew dependencyInsight --dependency spring-security-rsa
//        .\gradlew dependencies
//        .\gradlew dependencies --configuration runtimeClasspath
//        .\gradlew buildEnvironment
//        .\gradlew htmlDependencyReport
//
//  9) Report ueber die Lizenzen der eingesetzten Fremdsoftware
//        .\gradlew generateLicenseReport
//
//  10) Daemon stoppen
//        .\gradlew --stop
//
//  11) Verfuegbare Tasks auflisten
//        .\gradlew tasks
//
//  12) "Dependency Verification"
//        .\gradlew --write-verification-metadata pgp,sha256 --export-keys
//
//  13) Initialisierung des Gradle Wrappers in der richtigen Version
//      dazu ist ggf. eine Internetverbindung erforderlich
//        gradle wrapper --gradle-version=8.0-milestone-6 --distribution-type=bin

// https://github.com/gradle/kotlin-dsl/tree/master/samples
// https://docs.gradle.org/current/userguide/kotlin_dsl.html
// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import net.ltgt.gradle.errorprone.errorprone
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun

// TODO https://youtrack.jetbrains.com/issue/KTIJ-19369
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    java
    idea
    checkstyle
    jacoco
    `project-report`

    alias(libs.plugins.springBoot)

    // https://spring.io/blog/2022/09/26/native-support-in-spring-boot-3-0-0-m5
    // Kommentar entfernen fuer Spring AOT
    //alias(libs.plugins.graalvmPlugin)

    // https://github.com/tbroyer/gradle-errorprone-plugin
    // https://errorprone.info/docs/installation
    alias(libs.plugins.errorpronePlugin)

    // https://spotbugs.readthedocs.io/en/latest/gradle.html
    alias(libs.plugins.spotbugs)

    // https://github.com/radarsh/gradle-test-logger-plugin
    alias(libs.plugins.testLogger)

    // https://github.com/allure-framework/allure-gradle
    // https://docs.qameta.io/allure/#_gradle_2
    alias(libs.plugins.allure)

    // https://github.com/boxheed/gradle-sweeney-plugin
    alias(libs.plugins.sweeney)

    // https://github.com/jeremylong/dependency-check-gradle
    alias(libs.plugins.owaspDependencycheck)

    // https://github.com/snyk/gradle-plugin
    alias(libs.plugins.snyk)

    // https://github.com/asciidoctor/asciidoctor-gradle-plugin
    // FIXME https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597
    alias(libs.plugins.asciidoctor)
    alias(libs.plugins.asciidoctorPdf)
    // Leanpub als Alternative zu PDF: https://github.com/asciidoctor/asciidoctor-leanpub-converter

    // https://github.com/nwillc/vplugin
    alias(libs.plugins.nwillc)

    // https://github.com/ben-manes/gradle-versions-plugin
    alias(libs.plugins.benManes)

    // https://github.com/markelliot/gradle-versions
    alias(libs.plugins.markelliot)

    // https://github.com/autonomousapps/dependency-analysis-android-gradle-plugin
    alias(libs.plugins.dependencyAnalysis)

    // https://github.com/jk1/Gradle-License-Report
    alias(libs.plugins.licenseReport)

    // https://github.com/gradle-dependency-analyze/gradle-dependency-analyze
    // https://github.com/jaredsburrows/gradle-license-plugin
    // https://github.com/hierynomus/license-gradle-plugin
}

defaultTasks = mutableListOf("compileTestJava")
group = "com.acme"
version = "1.0.0"

sweeney {
    enforce(mapOf("type" to "gradle", "expect" to "[8.0,8.0]"))
    // https://www.java.com/releases
    // https://devcenter.heroku.com/articles/java-support#specifying-a-java-version
    enforce(mapOf("type" to "jdk", "expect" to "[19.0.1,20]"))
    validate()
}

java {
    // https://docs.gradle.org/current/userguide/java_plugin.html#sec:java-extension
    // https://docs.gradle.org/current/dsl/org.gradle.api.plugins.JavaPluginExtension.html
    toolchain {
        // einschl. sourceCompatibility und targetCompatibility
        languageVersion.set(JavaLanguageVersion.of(libs.versions.javaVersion.get()))
    }
}

repositories {
    mavenCentral()

    // https://github.com/spring-projects/spring-framework/wiki/Spring-repository-FAQ
    // https://github.com/spring-projects/spring-framework/wiki/Release-Process
    maven("https://repo.spring.io/milestone") { mavenContent { releasesOnly() } }

    // Snapshots von Spring (auch fuer Snapshots von springdoc-openapi)
    // maven("https://repo.spring.io/snapshot") { mavenContent { snapshotsOnly() } }

    // Snapshots von springdoc-openapi
    // maven("https://s01.oss.sonatype.org/content/repositories/snapshots") { mavenContent { snapshotsOnly() } }

    // Snapshots von JaCoCo fuer Java 18
    // maven("https://oss.sonatype.org/content/repositories/snapshots") {
    //     mavenContent { snapshotsOnly() }
    //     // https://docs.gradle.org/current/userguide/jacoco_plugin.html#sec:jacoco_dependency_management
    //     content { onlyForConfigurations("jacocoAgent", "jacocoAnt") }
    // }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

/* ktlint-disable comment-spacing */
@Suppress("CommentSpacing")
// https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_separation
dependencies {
    //implementation(platform(libs.micrometerBom))
    //implementation(platform(libs.jacksonBom))
    //implementation(platform(libs.nettyBom))
    //implementation(platform(libs.reactorBom))
    //implementation(platform(libs.springBom))
    //implementation(platform(libs.springDataBom))
    //implementation(platform(libs.springSecurityBom))
    //implementation(platform(libs.zipkinReporterBom))
    implementation(platform(libs.mockitoBom))
    //implementation(platform(libs.junitBom))
    implementation(platform(libs.allureBom))
    implementation(platform(libs.springBootBom))
    // spring-boot-starter-parent als "Parent POM"
    implementation(platform(libs.springdocOpenapiBom))

    // "Starters" enthalten sinnvolle Abhaengigkeiten, die man i.a. benoetigt
    // spring-boot-starter beinhaltet Spring Boot mit Actuator sowie spring-boot-starter-logging mit Logback
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-tomcat") {
        exclude(group = "org.apache.tomcat.embed", module = "tomcat-embed-websocket")
    }
    implementation("org.springframework.boot:spring-boot-starter-json")
    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    //iplementation("org.springframework.boot:spring-boot-starter-security")
    //implementation("org.springframework.security:spring-security-crypto")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.flywaydb:flyway-core")
    // https://flywaydb.org/documentation/database/mysql#java-usage
    if (System.getProperty("db") == "mysql") {
        runtimeOnly("org.flywaydb:flyway-mysql")
    }
    implementation(libs.tomcatCoyote)

    // Tracing und Metriken durch Micrometer sowie Visualisierung durch Zipkin oder Prometheus/Grafana
    // FIXME https://github.com/spring-projects/spring-graphql/issues/547 NullPointerException, wenn Requests von Spring-basierten GraphQL-Clients empfangen werden
    //implementation("io.micrometer:micrometer-observation")
    //implementation("io.micrometer:micrometer-tracing-bridge-brave")
    //implementation("io.zipkin.reporter2:zipkin-reporter-brave")

    implementation(libs.jfiglet)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // https://docs.jboss.org/hibernate/orm/6.1/userguide/html_single/Hibernate_User_Guide.html#tooling-gradle-modelgen
    // QueryDSL com.querydsl:querydsl-jpa von Juli 2021 unterstuetzt nur JPA 2.2 und Hibernate 5.6
    annotationProcessor(libs.hibernateJpamodelgen)

    // https://springdoc.org/v2/#swagger-ui-configuration
    // https://github.com/springdoc/springdoc-openapi
    // https://github.com/springdoc/springdoc-openapi-demos/wiki/springdoc-openapi-2.x-migration-guide
    // https://www.baeldung.com/spring-rest-openapi-documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    when (System.getProperty("db")) {
        null, "postgresql" -> runtimeOnly("org.postgresql:postgresql")
        "mysql" -> runtimeOnly("mysql:mysql-connector-java")
        "oracle"-> runtimeOnly("com.oracle.database.jdbc:ojdbc11")
        "h2" -> runtimeOnly("com.h2database:h2")
        else -> throw IllegalArgumentException("Fehler bei -Ddb=mysql|oracle|h2")
    }
    runtimeOnly(libs.jansi)
    runtimeOnly(libs.bouncycastle) // Argon2

    compileOnly(libs.spotbugsAnnotations)
    // https://github.com/spring-projects/spring-framework/issues/25095
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")

    // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools
    //developmentOnly("org.springframework.boot:spring-boot-devtools:${libs.versions.springBoot.get()}")

    // einschl. JUnit und Mockito
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.hamcrest", module = "hamcrest")
        exclude(group = "org.skyscreamer", module = "jsonassert")
        exclude(group = "org.xmlunit", module = "xmlunit-core")
    }
    testImplementation(libs.junitPlatformSuiteApi)
    testRuntimeOnly(libs.junitPlatformSuiteEngine)
    //testImplementation("org.springframework.security:spring-security-test")
    // mock() fuer record
    testImplementation("org.mockito:mockito-inline")

    // https://github.com/tbroyer/gradle-errorprone-plugin
    errorprone(libs.errorprone)

    constraints {
        implementation(libs.annotations)
        //implementation(libs.springGraphQL)
        //implementation(libs.springHateoas)
        //implementation(libs.jakartaPersistence)
        //implementation(libs.hibernate)
        //runtimeOnly(libs.postgres)
        //runtimeOnly(libs.mysql)
        if (System.getProperty("db") == "oracle") {
            runtimeOnly(libs.oracle)
        }
        implementation(libs.flyway)
        if (System.getProperty("db") == "mysql") {
            runtimeOnly(libs.flywayMySQL)
        }
        //implementation(libs.hibernateValidator)
        //implementation(libs.bundles.tomcat)
        implementation(libs.tomcatCore)
        implementation(libs.tomcatEl)
        //implementation(libs.bundles.graphqlJavaBundle)
        implementation(libs.graphqlJava)
        //implementation(libs.graphqlJavaDataloader)
        //implementation(libs.bundles.slf4jBundle)
        //implementation(libs.slf4jApi)
        //implementation(libs.slf4jJul)
        //implementation(libs.logback)
        //implementation(libs.springSecurityRsa)
        //implementation(libs.bundles.log4j)

        //testImplementation(libs.assertj)
    }
}
/* ktlint-enable comment-spacing */

// aktuelle Snapshots laden
// configurations.all {
//    resolutionStrategy { cacheChangingModulesFor(0, "seconds") }
// }

tasks.compileJava {
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.JavaCompile.html
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.compile.CompileOptions.html
    // https://dzone.com/articles/gradle-goodness-enabling-preview-features-for-java
    options.isDeprecation = true
    with(options.compilerArgs) {
        add("-Xlint:unchecked")
        // fuer springdoc-openapi https://github.com/spring-projects/spring-framework/issues/29563
        add("-parameters")
        add("--enable-preview")
        //add("-Xlint:preview")

        // https://github.com/tbroyer/gradle-errorprone-plugin#jdk-16-support
        add("--add-opens")
        add("--add-exports")
    }

    // https://uber.github.io/AutoDispose/error-prone
    // https://errorprone.info/docs/flags
    // TODO https://github.com/google/error-prone/issues/2321
    with(options.errorprone.errorproneArgs) {
        add("-Xep:InvalidParam:OFF")
        add("-Xep:MissingSummary:OFF")
    }

    // ohne sourceCompatiblity und targetCompatibility:
    //options.release.set(libs.versions.javaVersion.get())
    // https://blog.gradle.org/incremental-compiler-avoidance#about-annotation-processors
}

tasks.compileTestJava {
    options.isDeprecation = true
    with(options.compilerArgs) {
        add("-Xlint:unchecked")
        add("--enable-preview")
    }
    // Spring HATEOAS: _embedded.List
    options.errorprone.errorproneArgs.add("-Xep:VariableNameSameAsType:OFF")
}

tasks.named<BootJar>("bootJar") {
    doLast {
        println("")
        println("Aufruf der ausfuehrbaren JAR-Datei:")
        @Suppress("MaxLineLength")
        println(
            "java -D'LOG_PATH=./build/log' -D'javax.net.ssl.trustStore=./src/main/resources/truststore.p12' -D'javax.net.ssl.trustStorePassword=zimmermann' -jar build/libs/${archiveFileName.get()} --spring.profiles.default=dev --spring.profiles.active=dev [--debug]", // ktlint-disable max-line-length
        )
        println("")
    }
}

// https://github.com/paketo-buildpacks/spring-boot
tasks.named<BootBuildImage>("bootBuildImage") {
    // paketobuildpacks/builder:tiny als Builder fuer "Native Image"
    // https://docs.spring.io/spring-boot/docs/current/reference/html/native-image.html
    // https://github.com/spring-projects/spring-framework/blob/main/framework-docs/src/docs/asciidoc/core/core-aot.adoc

    // "created 42 years ago" wegen Reproducability: https://medium.com/buildpacks/time-travel-with-pack-e0efd8bf05db

    // default:   imageName = "docker.io/${project.name}:${project.version}"
    val path = "juergenzimmermann"
    imageName.set("$path/${project.name}")
    val tag = System.getProperty("tag") ?: project.version.toString()
    tags.set(mutableListOf("$path/${project.name}:$tag"))

    // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.builder-configuration
    // https://github.com/bell-sw/Liberica/releases
    @Suppress("StringLiteralDuplication")
    environment.set(
        mapOf(
            // https://github.com/paketo-buildpacks/bellsoft-liberica/releases
            "BP_JVM_VERSION" to "19.0.1",
            // Kommentar entfernen fuer Spring AOT
            //"BP_NATIVE_IMAGE" to "true",
            //"BP_BOOT_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-H:+ReportExceptionStackTraces",
            // https://github.com/paketo-buildpacks/bellsoft-liberica#configuration
            // https://github.com/paketo-buildpacks/spring-boot
            // "BPL_JVM_THREAD_COUNT" to "250", // default
            "BPL_JVM_THREAD_COUNT" to "20",
            // https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#build-image.examples.runtime-jvm-configuration
            "BPE_DELIM_JAVA_TOOL_OPTIONS" to " ",
            "BPE_APPEND_JAVA_TOOL_OPTIONS" to "--enable-preview",
            // https://github.com/paketo-buildpacks/spring-boot/blob/main/buildpack.toml
            //"BP_SPRING_CLOUD_BINDINGS_DISABLED" to "true",
            //"BPL_SPRING_CLOUD_BINDINGS_DISABLED" to "true",
            //"BPL_SPRING_CLOUD_BINDINGS_ENABLED" to "false", // deprecated
            // https://paketo.io/docs/howto/configuration/#enabling-debug-logging
            //"BP_LOG_LEVEL" to "DEBUG",
        )
    )

    // Adoptium (Eclipse Temurin) statt Bellsoft Liberica (= default), einschl. Gradle, Spring Boot, ...
    // ABER: JRE nur fuer LTS (long term support) 8, 11, 17
    // https://paketo.io/docs/howto/java/#use-an-alternative-jvm
    // buildpacks = listOf(
    //     // https://github.com/paketo-buildpacks/ca-certificates
    //     "paketo-buildpacks/ca-certificates",
    //     // https://github.com/paketo-buildpacks/adoptium
    //     "gcr.io/paketo-buildpacks/adoptium",
    //     // https://github.com/paketo-buildpacks/java
    //     "paketo-buildpacks/java",
    // )

    // Podman statt Docker
    // docker {
    //    host = "unix:///run/user/1000/podman/podman.sock"
    //    isBindHostToBuilder = true
    // }
}

// https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#core.aot
// https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#native-image
// Kommentar entfernen fuer Spring AOT
//tasks.named<org.springframework.boot.gradle.tasks.aot.ProcessAot>("processAot") {
//    jvmArgs = listOf("--enable-preview")
//}
//tasks.named<JavaCompile>("compileAotJava") {
//    options.compilerArgs.add("--enable-preview")
//}
//tasks.named<org.graalvm.buildtools.gradle.tasks.BuildNativeImageTask>("nativeCompile") {
//    options.get().jvmArgs("--enable-preview")
//}

//tasks.??? {
//    removeXmlSupport.set(false)
//}

tasks.named<BootRun>("bootRun") {
    // https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools
    //classpath(configurations.developmentOnly)

    jvmArgs("--enable-preview")
    // Hibernate 6.2: zzgl. Configuration mit @EnableLoadTimeWeaving
    //jvmArgs("--enable-preview", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "-javaagent:spring-instrument-VERSION.jar")


    // "System Properties", z.B. fuer Spring Properties oder fuer logback
    // https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#appendix.application-properties
    val port = System.getProperty("port")
    if (port != null) {
        systemProperty("server.port", port)
    }

    if (System.getProperty("tls") == "false") {
        @Suppress("StringLiteralDuplication")
        systemProperty("server.ssl.enabled", "false")
        @Suppress("StringLiteralDuplication")
        systemProperty("server.http2.enabled", "false")
    }

    systemProperty("spring.profiles.default", "dev")
    systemProperty("spring.profiles.active", "dev")
    systemProperty("spring.config.location", "classpath:/application.yml")
    systemProperty("spring.datasource.password", "p")
    systemProperty("spring.output.ansi.enabled", "ALWAYS")
    systemProperty("server.tomcat.basedir", "./build/tomcat")
    systemProperty("LOG_PATH", "./build/log")
    systemProperty("APPLICATION_LOGLEVEL", "TRACE")
    // Logging der Header-Daten
    systemProperty("REQUEST_RESPONSE_LOGLEVEL", "TRACE")
    systemProperty("HIBERNATE_LOGLEVEL", "DEBUG")
    systemProperty("FLYWAY_LOGLEVEL", "DEBUG")

    // $env:TEMP\tomcat-docbase.* -> src\main\webapp (urspruengl. fuer WAR)
    // Document Base = Context Root, siehe https://tomcat.apache.org/tomcat-10.1-doc/config/context.html
    // $env:TEMP\hsperfdata_<USERNAME>\<PID> Java HotSpot Performance data log: bei jedem Start der JVM neu angelegt.
    // https://support.oracle.com/knowledge/Middleware/2325910_1.html
    // https://blog.mygraphql.com/zh/notes/java/diagnostic/hsperfdata/hsperfdata

    when (System.getProperty("db")) {
        // Tablespace fuer flyway_schema_history nur bei PostgreSQL
        // MySQL: Flyway generiert bei "CREATE TABLE flyway_schema_history ..." nicht "ROW_FORMAT=COMPACT"
        // Oracle: CREATE TABLESPACE ist sehr kompliziert https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-TABLESPACE.html
        "postgres", null -> systemProperty("spring.flyway.tablespace", "${project.name}space")
        "mysql" -> systemProperty("spring.datasource.url", "jdbc:mysql://localhost/${project.name}")
        "oracle" -> systemProperty("spring.datasource.url", "jdbc:oracle:thin:@localhost/XEPDB1")
        "h2" -> {
            systemProperty("spring.datasource.url", "jdbc:h2:mem:testdb")
            systemProperty("spring.datasource.username", "sa")
            systemProperty("spring.datasource.password", "")
            systemProperty("spring.h2.console.enabled", true)
        }
        else -> throw IllegalArgumentException("Fehler bei -Ddb=mysql|oracle|h2")
    }
}

tasks.test {
    useJUnitPlatform {
        includeTags = setOf("integration", "unit")

        // includeTags = setOf("integration")
        // includeTags = setOf("rest")
        // includeTags = setOf("rest_get")
        // includeTags = setOf("rest_write")
        // includeTags = setOf("graphql")
        // includeTags = setOf("query")
        // includeTags = setOf("mutation")

        // includeTags = setOf("unit")
        // includeTags = setOf("service_read")
        // includeTags = setOf("service_write")
    }

    systemProperty("javax.net.ssl.trustStore", "./src/main/resources/truststore.p12")
    systemProperty("javax.net.ssl.trustStorePassword", "zimmermann")
    systemProperty("junit.platform.output.capture.stdout", true)
    systemProperty("junit.platform.output.capture.stderr", true)
    systemProperty("spring.config.location", "classpath:/application.yml")
    systemProperty("spring.datasource.password", "p")
    // Tests ohne TLS und ohne HTTP2
    systemProperty("server.ssl.enabled", false)
    systemProperty("server.http2.enabled", false)
    systemProperty("server.ssl.client-auth", "NONE")
    systemProperty("server.tomcat.basedir", "./build/tomcat")

    systemProperty("LOG_PATH", "./build/log")
    systemProperty("APPLICATION_LOGLEVEL", "TRACE")
    systemProperty("HIBERNATE_LOGLEVEL", "DEBUG")
    // systemProperty("HIBERNATE_LOGLEVEL", "TRACE")
    systemProperty("FLYWAY_LOGLEVEL", "DEBUG")

    when (System.getProperty("db")) {
        // Tablespace fuer flyway_schema_history nur bei PostgreSQL
        // MySQL: Flyway generiert bei "CREATE TABLE flyway_schema_history ..." nicht "ROW_FORMAT=COMPACT"
        // Oracle: CREATE TABLESPACE ist sehr kompliziert https://docs.oracle.com/en/database/oracle/oracle-database/21/sqlrf/CREATE-TABLESPACE.html
        "postgres", null -> systemProperty("spring.flyway.tablespace", "${project.name}space")
        "mysql" -> systemProperty("spring.datasource.url", "jdbc:mysql://localhost/${project.name}")
        "oracle" -> systemProperty("spring.datasource.url", "jdbc:oracle:thin:@localhost/XEPDB1")
        "h2" -> {
            systemProperty("spring.datasource.url", "jdbc:h2:mem:testdb")
            systemProperty("spring.datasource.username", "sa")
            systemProperty("spring.datasource.password", "")
            systemProperty("spring.h2.console.enabled", true)
        }
        else -> throw IllegalArgumentException("Fehler bei -Ddb=mysql|oracle|h2")
    }

    jvmArgs("--enable-preview")

    // https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
    // https://www.jetbrains.com/help/idea/run-debug-configuration-junit.html
    // https://docs.gradle.org/current/userguide/java_testing.html#sec:debugging_java_tests
    // debug = true

    // finalizedBy("jacocoTestReport")
}

// https://docs.qameta.io/allure/#_gradle_2
allure {
    version.set(libs.versions.allure.get())
    adapter {
        frameworks {
            junit5 {
                adapterVersion.set(libs.versions.allureJunit.get())
                autoconfigureListeners.set(true)
                enabled.set(true)
            }
        }
        autoconfigure.set(true)
        aspectjWeaver.set(false)
        aspectjVersion.set(libs.versions.aspectjweaver.get())
    }

    // https://github.com/allure-framework/allure-gradle#customizing-allure-commandline-download
    // commandline {
    //     group.set("io.qameta.allure")
    //     module.set("allure-commandline")
    //     extension.set("zip")
    // }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

// https://docs.gradle.org/current/userguide/task_configuration_avoidance.html
// https://guides.gradle.org/migrating-build-logic-from-groovy-to-kotlin/#configuring-tasks
tasks.getByName<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    // afterEvaluate gibt es nur bei getByName<> ("eager"), nicht bei named<> ("lazy")
    // https://docs.gradle.org/5.0/release-notes.html#configuration-avoidance-api-disallows-common-configuration-errors
    afterEvaluate {
        classDirectories.setFrom(
            files(
                classDirectories.files.map {
                    fileTree(it) { exclude("**/config/**", "**/entity/**") }
                },
            ),
        )
    }

    // https://github.com/gradle/gradle/pull/12626
    dependsOn(tasks.test)
}

tasks.getByName<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    violationRules {
        rule {
            limit {
                minimum = BigDecimal("0.7")
            }
        }
    }
}

checkstyle {
    toolVersion = libs.versions.checkstyle.get()
    // https://docs.gradle.org/current/dsl/org.gradle.api.plugins.quality.CheckstyleExtension.html
    configFile = file("extras/checkstyle.xml")
    setConfigProperties(
        "configDir" to "$projectDir/extras",
        // "checkstyleSuppressionsPath" to file("checkstyle-suppressions.xml").absolutePath,
    )
    isIgnoreFailures = false
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

spotbugs {
    // https://github.com/spotbugs/spotbugs/releases
    toolVersion.set(libs.versions.spotbugs.get())
}
tasks.spotbugsMain {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugs.html"))
    }
    excludeFilter.set(file("extras/spotbugs-exclude.xml"))
}

// https://github.com/jeremylong/DependencyCheck/blob/master/src/site/markdown/dependency-check-gradle/configuration.md
dependencyCheck {
    scanConfigurations = listOf("runtimeClasspath")
    suppressionFile = "$projectDir/extras/owasp.xml"
    data(
        closureOf<org.owasp.dependencycheck.gradle.extension.DataExtension> {
            directory = "C:/Zimmermann/owasp-dependency-check"
            username = "dc"
            password = "p"
        },
    )

    analyzedTypes = listOf("jar")
    analyzers(
        closureOf<org.owasp.dependencycheck.gradle.extension.AnalyzerExtension> {
            // nicht benutzte Analyzer
            assemblyEnabled = false
            autoconfEnabled = false
            bundleAuditEnabled = false
            cmakeEnabled = false
            cocoapodsEnabled = false
            composerEnabled = false
            golangDepEnabled = false
            golangModEnabled = false
            nodeEnabled = false
            nugetconfEnabled = false
            nuspecEnabled = false
            pyDistributionEnabled = false
            pyPackageEnabled = false
            rubygemsEnabled = false
            swiftEnabled = false

            nodeAudit(closureOf<org.owasp.dependencycheck.gradle.extension.NodeAuditExtension> { enabled = true })
            retirejs(closureOf<org.owasp.dependencycheck.gradle.extension.RetireJSExtension> { enabled = true })
            // ossIndex(closureOf<org.owasp.dependencycheck.gradle.extension.OssIndexExtension> { enabled = true })
        },
    )

    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL
}

snyk {
    setArguments("--configuration-matching=implementation|runtimeOnly")
    setSeverity("low")
    setApi("40df2078-e1a3-4f28-b913-e2babbe427fd")
}

tasks.javadoc {
    options {
        showFromPackage()
        // outputLevel = org.gradle.external.javadoc.JavadocOutputLevel.VERBOSE

        this as CoreJavadocOptions
        // Keine bzw. nur elementare Warnings anzeigen wegen Lombok
        // https://stackoverflow.com/questions/52205209/configure-gradle-build-to-suppress-javadoc-console-warnings
        addStringOption("Xdoclint:none", "-quiet")
        // https://stackoverflow.com/questions/59485464/javadoc-and-enable-preview
        addBooleanOption("-enable-preview", true)
        addStringOption("-release", "19")

        this as StandardJavadocDocletOptions
        author(true)
    }
}

tasks.getByName<AsciidoctorTask>("asciidoctor") {
    asciidoctorj {
        setVersion(libs.versions.asciidoctorj.get())
        // requires("asciidoctor-diagram")

        modules {
            diagram.use()
            diagram.setVersion(libs.versions.asciidoctorjDiagram.get())
        }
    }

    val separator = System.getProperty("file.separator")
    @Suppress("StringLiteralDuplication")
    setBaseDir(file("extras${separator}doc"))
    setSourceDir(file("extras${separator}doc"))
    // setOutputDir(file("$buildDir/docs/asciidoc"))
    logDocuments = true

    // https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597#issuecomment-844352804
    inProcess = org.asciidoctor.gradle.base.process.ProcessMode.JAVA_EXEC
    forkOptions {
        @Suppress("StringLiteralDuplication")
        jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.io=ALL-UNNAMED")
    }

    doLast {
        @Suppress("MaxLineLength")
        println(
            "Das Entwicklerhandbuch ist in $buildDir${separator}docs${separator}asciidoc${separator}entwicklerhandbuch.html", // ktlint-disable max-line-length
        )
    }
}

tasks.getByName<AsciidoctorPdfTask>("asciidoctorPdf") {
    asciidoctorj {
        setVersion(libs.versions.asciidoctorj.get())

        modules {
            diagram.use()
            diagram.setVersion(libs.versions.asciidoctorjDiagram.get())
            pdf.setVersion(libs.versions.asciidoctorjPdf.get())
        }
    }

    val separator = System.getProperty("file.separator")
    setBaseDir(file("extras${separator}doc"))
    setSourceDir(file("extras${separator}doc"))
    attributes(mapOf("pdf-page-size" to "A4"))
    logDocuments = true

    // https://github.com/asciidoctor/asciidoctor-gradle-plugin/issues/597#issuecomment-844352804
    inProcess = org.asciidoctor.gradle.base.process.ProcessMode.JAVA_EXEC
    forkOptions {
        jvmArgs("--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.io=ALL-UNNAMED")
    }

    doLast {
        @Suppress("MaxLineLength")
        println(
            "Das Entwicklerhandbuch ist in $buildDir${separator}docs${separator}asciidocPdf${separator}entwicklerhandbuch.pdf", // ktlint-disable max-line-length
        )
    }
}

licenseReport {
    configurations = arrayOf("runtimeClasspath")
}

tasks.getByName<DependencyUpdatesTask>("dependencyUpdates") {
    checkConstraints = true
}

idea {
    module {
        isDownloadJavadoc = true
        // https://stackoverflow.com/questions/59950657/querydsl-annotation-processor-and-gradle-plugin
        sourceDirs.add(file("generated/"))
        generatedSourceDirs.add(file("generated/"))
    }
}
