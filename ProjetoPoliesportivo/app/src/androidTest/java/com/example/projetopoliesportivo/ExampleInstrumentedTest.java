package com.example.projetopoliesportivo;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.projetopoliesportivo", appContext.getPackageName());
    }

    @Test
    public void clickingExistingAccountLinkOpensLoginScreen() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.textoLinkLogin)).perform(
                    androidx.test.espresso.action.ViewActions.click()
            );

            Espresso.onView(ViewMatchers.withId(R.id.textoTituloLogin)).check(
                    androidx.test.espresso.assertion.ViewAssertions.matches(
                            ViewMatchers.isDisplayed()
                    )
            );
        }
    }
}
