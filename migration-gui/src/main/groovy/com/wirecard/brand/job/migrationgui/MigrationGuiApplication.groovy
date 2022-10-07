package com.wirecard.brand.job.migrationgui.migrationgui

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication(scanBasePackages = ["com.wirecard.brand.job.migrationgui.mvc", "com.wirecard.brand.job.migrationgui.service"])
class MigrationGuiApplication {

	static void main(String[] args) {
		SpringApplication.run MigrationGuiApplication, args
	}

}
