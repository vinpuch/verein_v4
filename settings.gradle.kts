@file:Suppress("MissingPackageDeclaration", "UnstableApiUsage")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal() // maven("https://plugins.gradle.org/m2")

        maven("https://repo.spring.io/milestone")

        // Snapshots von Spring Framework, Spring Data, Spring Security und Spring Cloud
        // maven("https://repo.spring.io/snapshot") { mavenContent { snapshotsOnly() } }
        // maven("https://repo.spring.io/plugins-release")
    }
}

// https://docs.gradle.org/8.0-milestone-3/userguide/toolchains.html#sub:download_repositories
// toolchainManagement {
//     jvm {
//         javaRepositories {
//             repository("adoptium") {
//                 resolverClass.set(AdoptiumResolver::class.java)
//             }
//         }
//     }
// }

// buildCache {
//    local {
//        directory = "C:/Z/caches"
//    }
// }

rootProject.name = "verein"
