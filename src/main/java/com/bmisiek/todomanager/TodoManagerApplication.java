package com.bmisiek.todomanager;

import com.bmisiek.libraries.seeder.RunOnStartInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class TodoManagerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TodoManagerApplication.class, args);

        List<RunOnStartInterface> tasks = context
                .getBeansOfType(RunOnStartInterface.class).values().stream()
                .sorted(Comparator.comparingInt(RunOnStartInterface::getPriority))
                .toList();

        tasks.forEach(task -> {
            try {
                task.run();
            } catch (Exception e) {
                throw new RuntimeException("Error executing task: " + task.getClass().getName(), e);
            }
        });
    }
}