package com.bmisiek.todomanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan(basePackages = {"com.bmisiek.todomanager", "com.bmisiek.libraries.validation"})
class TodoManagerApplicationTests {

    @Test
    void contextLoads() {
    }

}
