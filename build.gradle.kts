plugins {
    java
    `java-library-distribution`
    `maven-publish`
}

group = "edu.utsa.fileflow"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io")
    }
}

dependencies {
    // https://mvnrepository.com/artifact/org.antlr/antlr4-runtime
    implementation("org.antlr:antlr4-runtime:4.5")
    // https://mvnrepository.com/artifact/log4j/log4j
    implementation("log4j:log4j:1.2.14")

    // https://github.com/rodneyxr/ffa-grammar
    implementation("com.github.rodneyxr:ffa-grammar:gradle-SNAPSHOT")
    // https://github.com/rodneyxr/ffa-framework
    implementation("com.github.rodneyxr:ffa-framework:gradle-SNAPSHOT")
    // https://github.com/rodneyxr/brics-automaton
    implementation("com.github.rodneyxr:brics-automaton:gradle-SNAPSHOT")
    // https://github.com/rodneyxr/brics-jsa
    implementation("com.github.rodneyxr:brics-jsa:gradle-SNAPSHOT")

    // junit
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

distributions {
    main {
        distributionBaseName.set("examples")
    }
}

publishing {
    publications {
        create<MavenPublication>("examples") {
            artifact(tasks.jar)
        }
    }
}