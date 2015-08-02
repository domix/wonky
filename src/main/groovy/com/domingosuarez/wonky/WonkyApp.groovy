package com.domingosuarez.wonky

import static org.springframework.boot.SpringApplication.run

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching

/**
 * Created by domix on 19/07/15.
 */
@SpringBootApplication
@EnableCaching
class WonkyApp {
  static void main(String... args) {
    run WonkyApp, args
  }
}
