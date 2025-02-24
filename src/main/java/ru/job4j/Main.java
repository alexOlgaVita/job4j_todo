package ru.job4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        /* временное решение возникающей проблемы пришлось обойти таким способом, пока нет другого.
         * Проблема возникала при выполнении запросов: "java.lang.ClassCastException: class ru.job4j.model.Task cannot
         *  be cast to class ru.job4j.model.Task (ru.job4j.model.Task is in unnamed module of loader 'app';
         * ru.job4j.model.Task is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @853c3ea)"
         * источкник: "https://forum.jmix.ru/t/podklyuchenie-spring-boot-devtools/112?ysclid=m7dm9o7xh6819232596"
         * */
        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication.run(ru.job4j.Main.class, args);
    }
}