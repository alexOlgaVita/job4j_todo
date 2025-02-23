package ru.job4j;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.sql2o.Sql2o;
import org.sql2o.converters.Converter;
import org.sql2o.converters.ConverterException;
import org.sql2o.quirks.NoQuirks;
import org.sql2o.quirks.Quirks;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootApplication
public class Main {
    @Bean(destroyMethod = "close")
    public SessionFactory sf() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Bean
    public DataSource connectionPool(@Value("${datasource.url}") String url,
                                     @Value("${datasource.username}") String username,
                                     @Value("${datasource.password}") String password) {
        return new BasicDataSource() {
            {
                setUrl(url);
                setUsername(username);
                setPassword(password);
            }
        };
    }

    @Bean
    public Sql2o databaseClient(DataSource dataSource) {
        return new Sql2o(dataSource, createConverters());
    }

    private Quirks createConverters() {
        return new NoQuirks() {
            {
                converters.put(LocalDateTime.class, new Converter<LocalDateTime>() {

                    @Override
                    public LocalDateTime convert(Object value) throws ConverterException {
                        if (value == null) {
                            return null;
                        }
                        if (!(value instanceof Timestamp)) {
                            throw new ConverterException("Invalid value to convert");
                        }
                        return ((Timestamp) value).toLocalDateTime();
                    }

                    @Override
                    public Object toDatabaseParam(LocalDateTime value) {
                        return value == null ? null : Timestamp.valueOf(value);
                    }
                });
            }
        };
    }

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