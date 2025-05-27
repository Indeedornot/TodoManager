package com.bmisiek.todomanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@AutoConfigureMockMvc
@ComponentScan(basePackages = {"com.bmisiek.todomanager", "com.bmisiek.libraries.validation"})
class TodoManagerApplicationTests {

    @Test
    void contextLoads() {
    }

}
