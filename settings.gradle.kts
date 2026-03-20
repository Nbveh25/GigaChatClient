pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "GigaChat"
include(":app")

include(":core")
include(":core:network")
include(":core:designsystem")

include(":feature")
include(":feature:auth")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":core:database")
