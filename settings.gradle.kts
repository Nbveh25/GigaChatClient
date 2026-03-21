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
include(":core:auth")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:designsystem")

include(":feature")
include(":feature:auth")
include(":feature:auth:api")
include(":feature:auth:impl")

include(":feature:register")
include(":feature:register:api")
include(":feature:register:impl")

include(":feature:chat-list")
include(":feature:chat-list:api")
include(":feature:chat-list:impl")
