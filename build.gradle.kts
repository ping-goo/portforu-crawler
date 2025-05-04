plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlin.plugin.jpa") version "1.9.25"
	id("org.jetbrains.kotlin.plugin.allopen") version "1.9.25"
	id("org.jetbrains.kotlin.plugin.noarg") version "1.9.25"
}

group = "pingu"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
}
noArg {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.4")


	// 웹 크롤링
	implementation("org.jsoup:jsoup:1.15.3")
	implementation("org.seleniumhq.selenium:selenium-java:4.6.0")
	implementation("io.github.bonigarcia:webdrivermanager:5.8.0")

	implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

	implementation("com.squareup.okhttp3:okhttp:4.10.0")
	implementation("org.json:json:20230227")

	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.boot:spring-boot-starter-quartz")

	runtimeOnly("mysql:mysql-connector-java:8.0.33")

	implementation("org.springframework.boot:spring-boot-starter-aop")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// RabbitMQ
	implementation("org.springframework.boot:spring-boot-starter-amqp")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

configurations.all {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.seleniumhq.selenium" && requested.name.contains("selenium")) {
			useVersion("4.6.0")
		}
	}
}
