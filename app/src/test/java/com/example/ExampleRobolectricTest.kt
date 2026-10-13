package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AbcCaseData
import com.example.data.repository.WeatherRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Weather ABC", appName)
    }

    @Test
    fun `abc case phases contains all 5 required steps`() {
        assertEquals(5, AbcCaseData.phases.size)
        assertEquals("LECTURA", AbcCaseData.phases[0].title)
        assertEquals("INFORMACIÓN", AbcCaseData.phases[1].title)
        assertEquals("ANÁLISIS", AbcCaseData.phases[2].title)
        assertEquals("SOLUCIÓN", AbcCaseData.phases[3].title)
        assertEquals("CONCLUSIONES", AbcCaseData.phases[4].title)
    }

    @Test
    fun `hardcoded weather exposes the fixed 22 degrees problem`() {
        val repo = WeatherRepository()
        val reportMadrid = repo.getHardcodedWeather("Madrid")
        val reportLima = repo.getHardcodedWeather("Lima")

        assertEquals(22.0, reportMadrid.temperature, 0.01)
        assertEquals(22.0, reportLima.temperature, 0.01)
        assertFalse(reportMadrid.isRealApi)
        assertFalse(reportLima.isRealApi)
    }
}
