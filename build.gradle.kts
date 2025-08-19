import org.jetbrains.changelog.date
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm")  version "2.2.10"
    id("org.jetbrains.changelog") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.7.2"
}
group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

changelog {
    version.set(providers.gradleProperty("version").get())
    path.set(file("CHANGELOG.md").canonicalPath)
    header.set(provider { "${version.get()} - ${
        date("yyyy-MM-dd")
    }" })
    headerParserRegex.set("""(\d\.\d\.\d)""".toRegex())
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.set(listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security"))
}
repositories {
    mavenCentral()
 
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies{
    intellijPlatform {
        intellijIdeaCommunity("2025.2")
    }
}


intellijPlatform{
    signing{
        privateKeyFile.set(File("./cert/private.pem"))
        certificateChainFile.set(File("./cert/chain.crt"))
        password = System.getenv("SIGNING_PASSWORD") ?: providers.environmentVariable("SIGNING_PASSWORD").orNull
    }
    publishing{
        token = System.getenv("PUBLISH_TOKEN") ?: providers.environmentVariable("PUBLISH_TOKEN").orNull
    }
    buildSearchableOptions.set(true)
}

tasks {
    providers.gradleProperty("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it.get()
            targetCompatibility = it.get()
        }
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(it.get()))
            }
        }
    }
    
    wrapper {
        gradleVersion = providers.gradleProperty("gradleVersion").get()
    }

    patchPluginXml {
        version = providers.gradleProperty("version").get()
        sinceBuild = providers.gradleProperty("pluginSinceBuild").get()
        untilBuild = providers.gradleProperty("pluginUntilBuild").get()

        changeNotes.set(provider {
            changelog.renderItem(changelog.getLatest(), Changelog.OutputType.HTML)
        })
    }
    
    
    
    

}