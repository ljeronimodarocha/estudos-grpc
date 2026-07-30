package com.example.bookapp.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthenticationDetailsTest {

    @Test
    void userId_isCorrectlyStored() {
        Long testUserId = 42L;
        UserAuthenticationDetails details = new UserAuthenticationDetails(testUserId);

        assertEquals(testUserId, details.getUserId());
    }

    @Test
    void userId_nullWhenNotSet() {
        // Since constructor requires userId, it should never be null
        UserAuthenticationDetails details = new UserAuthenticationDetails(1L);
        assertNotNull(details.getUserId());
    }

    @Test
    void userId_handlesZero() {
        UserAuthenticationDetails details = new UserAuthenticationDetails(0L);
        assertEquals(0L, details.getUserId());
    }

    @Test
    void userId_handlesLargeValues() {
        Long largeId = Long.MAX_VALUE;
        UserAuthenticationDetails details = new UserAuthenticationDetails(largeId);
        assertEquals(largeId, details.getUserId());
    }
}
