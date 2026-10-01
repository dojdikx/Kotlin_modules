plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

application {
    mainClass.set("console.ParallelLoadKt")
}

tasks.register<JavaExec>("task2") {
    group = "application"
    description = "Поиск дубликатов JSON"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("console.DuplicateSearchKt")
    workingDir = rootProject.projectDir
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}
