package cn.edu.zut.collect_school;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApp {
    public static void main(String[] args) {
        SpringApplication.run(MainApp.class, args);
    }

//    @Bean
//    public CommandLineRunner runStudentDataGenerator() {
//        return args -> {
//            // 在这里调用你的StudentDataGenerator
//            cn.edu.zut.collect_school.mock.StudentDataGenerator.main(args);
//        };
//    }
}