package com.izual;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static junit.framework.TestCase.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class IntegrationTestApplicationTests {

    @Test
    public void contextLoads() {
        assertFalse(true);
        System.out.println("test jenkins!");
    }

}
