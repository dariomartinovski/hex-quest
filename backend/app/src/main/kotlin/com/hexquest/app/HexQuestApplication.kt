package com.hexquest.app

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(basePackages = ["com.hexquest"])
@EntityScan(basePackages = ["com.hexquest"])
@EnableJpaRepositories(basePackages = ["com.hexquest"])
class hexquestApplication

fun main(args: Array<String>) {
    runApplication<hexquestApplication>(*args)
}
