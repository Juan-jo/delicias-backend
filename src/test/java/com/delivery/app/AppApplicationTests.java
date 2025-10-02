package com.delivery.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppApplicationTests {

	@Test
	void contextLoads() {
	}

    @Test
    void testNumberIsEven() {
        assertTrue(NumberUtils.isEven(4), "4 debería ser par");
        assertTrue(NumberUtils.isEven(0), "0 debería ser par");
    }

    @Test
    void testNumberIsOdd() {
        assertFalse(NumberUtils.isEven(3), "3 debería ser impar");
        assertFalse(NumberUtils.isEven(7), "7 debería ser impar");
    }

    public static class NumberUtils {
        public static boolean isEven(int number) {
            return number % 2 == 0;
        }
    }

}
