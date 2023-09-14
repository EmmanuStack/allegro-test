import pl.allegro.tech.build.axion.publish.PublishingExtension

plugins {
    id("application")
    id("kotlin-spring")
    id("maven-publish")
    id("phoenix-provisioning")
}

apply(plugin = "phoenix-provisioning")

application {
    mainClass.set("pl.allegro.opbox.publisher.AppRunner")
}

dependencies {
    implementation(project(":couchbase-allegro"))
    implementation(platform("pl.allegro.tech.common:andamio-starter-dependencies:${libs.versions.andamioStarter.get()}"))
    implementation(group = "org.springframework.boot", name = "spring-boot-starter-data-couchbase")
    implementation(group = "pl.allegro.tech.common", name = "andamio-starter-webflux")

    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(group = "com.google.cloud", name = "google-cloud-bigquery", version = "2.31.2")
    implementation(group = "pl.allegro.bigdata.hadoop", name = "vault-gcp-java", version = "0.3.0")
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = libs.versions.jackson.get())
    implementation(group = "org.apache.curator", name = "curator-recipes", version = "5.5.0") {
        // curator depends on Zookeeper 3.5.x which is incompatible with Zookeeper installed in Allegro
        exclude(group = "org.apache.zookeeper", module = "zookeeper")
    }
    implementation(group = "org.apache.zookeeper", name = "zookeeper", version = "3.9.0") {
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
        exclude(group = "log4j", module = "log4j")
    }

    testImplementation(group = "org.springframework.boot", name = "spring-boot-starter-test")
    testImplementation(group = "io.kotest", name = "kotest-runner-junit5", version = libs.versions.kotest.get())
    testImplementation(group = "io.kotest.extensions", name = "kotest-extensions-spring", version = libs.versions.kotestSpringExtension.get())
}

configure<PublishingExtension> {
    applyDefaultPublication = false

    repositories.create("allegro") {
        url = "https://artifactory.allegrogroup.com/artifactory/allegro-{}s-local/"
    }
    apply()
}

val distZip by tasks
val provisioningPackage by tasks

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifact(provisioningPackage)
            artifact(distZip) { classifier = "deploy" }
        }
    }
}

