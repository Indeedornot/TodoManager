package com.bmisiek.todomanager;

import com.bmisiek.libraries.seeder.RunOnStartInterface;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Comparator;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bmisiek.todomanager", "com.bmisiek.libraries.validation"})
public class TodoManagerApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(TodoManagerApplication.class, args);

        List<RunOnStartInterface> tasks = getStartupTasks(context);
        tasks.forEach(TodoManagerApplication::handleStartupTask);
    }

    private static List<RunOnStartInterface> getStartupTasks(ApplicationContext context) {
        return context
                .getBeansOfType(RunOnStartInterface.class).values().stream()
                .sorted(Comparator.comparingInt(RunOnStartInterface::getPriority))
                .toList();
    }

    private static void handleStartupTask(RunOnStartInterface task) {
        try {
            task.run();
        } catch (Exception e) {
            throw new RuntimeException("Error executing task: " + task.getClass().getName(), e);
        }
    }
}