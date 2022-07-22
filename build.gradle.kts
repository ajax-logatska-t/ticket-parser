import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.protobuf.gradle.*

plugins {
    idea
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("com.google.protobuf") version "0.8.19"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
}

group = "local"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.grpc:grpc-protobuf:1.47.0")
    implementation("io.grpc:grpc-stub:1.47.0")
    implementation("io.grpc:grpc-netty-shaded:1.47.0")
    implementation("com.google.protobuf:protobuf-java:3.21.1")
    implementation("net.devh:grpc-server-spring-boot-starter:2.13.1.RELEASE")
    implementation("com.salesforce.servicelibs:reactor-grpc:1.2.3")
    implementation("com.salesforce.servicelibs:reactive-grpc-common:1.2.3")
    implementation("io.reactivex.rxjava2:rxjava:2.2.21")
    implementation("io.nats:nats-spring-boot-starter:0.5.6")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("com.salesforce.servicelibs:reactor-grpc-stub:1.2.3")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.17.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.40.1"
        }
        id("reactor") {
            artifact = "com.salesforce.servicelibs:reactor-grpc:1.2.3"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("reactor")
            }
        }
    }
}

