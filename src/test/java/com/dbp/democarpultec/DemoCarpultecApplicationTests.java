package com.dbp.democarpultec;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DemoCarpultecApplicationTests extends PostgresContainerTest {

    @Test
    void contextLoads() {
    }

}
