package com.example.googlebooks;

import android.content.Context;

import com.example.googlebooks.utils.CacheManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/*
Test for CacheManager since I added quite a bit of logic to it.  I set the cache so that it will
retain the 3 most recent search query results.  I also added a timeout (set at 20 seconds so it
was easier to test without waiting too long), so that if enough time passes the app will make a
remote call to refresh potentially stale data in the local DB.
 */

@Config(manifest= Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class CacheManagerTest {

    private CacheManager cacheManager;

    @Before
    public void setUp() throws Exception {
        Context context = RuntimeEnvironment.application.getApplicationContext();

        cacheManager = new CacheManager(context);
    }

    @Test
    public void isCacheValid_queryInPrefsAtCurrentTime_returnsTrue() {
        //GIVEN
        cacheManager.updateCache("test", System.currentTimeMillis());
        //WHEN
        boolean result = cacheManager.isCacheValid("test");
        //THEN
        assertTrue(result);
    }

    @Test
    public void isCacheValid_queryNotInPrefs_returnsFalse() {
        //GIVEN
        cacheManager.updateCache("a", System.currentTimeMillis());
        cacheManager.updateCache("b", System.currentTimeMillis());
        cacheManager.updateCache("c", System.currentTimeMillis());
        //WHEN
        boolean result = cacheManager.isCacheValid("d");
        //THEN
        assertFalse(result);
    }

    @Test
    public void isCacheValid_queryInPrefsPastTimeout_returnsFalse() {
        //GIVEN
        cacheManager.updateCache("test", System.currentTimeMillis() - 30000);
        //WHEN
        boolean result = cacheManager.isCacheValid("test");
        //THEN
        assertFalse(result);
    }
}