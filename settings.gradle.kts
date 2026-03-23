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

include(":core:auth")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:designsystem")

include(":feature:auth:api")
include(":feature:auth:impl")

include(":feature:register:api")
include(":feature:register:impl")

include(":feature:chat-list:api")
include(":feature:chat-list:impl")

include(":feature:chat-detail:api")
include(":feature:chat-detail:impl")

include(":feature:profile:api")
include(":feature:profile:impl")
