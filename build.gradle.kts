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
        privateKeyFile.set(File(providers.gradleProperty("signingPrivateKeyFile").get()))
        certificateChainFile.set(File(providers.gradleProperty("signingCertificateChainFile").get()))
        password = providers.gradleProperty("signingPassword").get()
        // TODO: Uncomment when you have the signing keys
//        privateKey = providers.gradleProperty("signingPrivateKey").get()
//        certificateChain = providers.gradleProperty("signingCertificateChain").get()
    }
    publishing{
        token = providers.gradleProperty("publishToken").get()
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