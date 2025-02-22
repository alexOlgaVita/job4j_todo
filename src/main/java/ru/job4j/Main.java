package ru.job4j;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {
    @Bean(destroyMethod = "close")
    public SessionFactory sf() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        /* временное решение возникающей проблемы пришлось обойти таким способом, пока нет другого.
         * Проблема возникала при выполнении запросов: "java.lang.ClassCastException: class ru.job4j.model.Task cannot
         *  be cast to class ru.job4j.model.Task (ru.job4j.model.Task is in unnamed module of loader 'app';
         * ru.job4j.model.Task is in unnamed module of loader org.springframework.boot.devtools.restart.classloader.RestartClassLoader @853c3ea)"
         * источкник: https://forum.jmix.ru/t/podklyuchenie-spring-boot-devtools/112?ysclid=m7dm9o7xh6819232596
         * */
        System.setProperty("spring.devtools.restart.enabled", "false");

        SpringApplication.run(ru.job4j.Main.class, args);
    }
}