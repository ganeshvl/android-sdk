package com.mapfit.android.map

import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.mapfit.android.*
import com.mapfit.android.geometry.LatLng
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [MapView] functionality.
 *
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapListeners {

    private lateinit var mapView: MapView

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var onMapClickListener: OnMapClickListener

    @Mock
    private lateinit var onMapDoubleClickListener: OnMapDoubleClickListener

    @Mock
    private lateinit var onMapLongClickListener: OnMapLongClickListener

    @Mock
    private lateinit var onMapPanListener: OnMapPanListener

    @Mock
    private lateinit var onMapPinchListener: OnMapPinchListener

    @Rule
    @JvmField
    val activityRule: ActivityTestRule<MapViewTestActivity> = ActivityTestRule(
        MapViewTestActivity::class.java,
        true,
        true
    )

    private lateinit var idlingResource: CountingIdlingResource

    @Before
    @UiThreadTest
    fun init() {
        MockitoAnnotations.initMocks(this)

        idlingResource = activityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)

        idlingResource.registerIdleTransitionCallback({
            mapfitMap = activityRule.activity.mapfitMap
            mapView = activityRule.activity.mapView
        })

        activityRule.activity.init()
    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testMapClickListener() = runBlocking {
        delay(400)
        mapfitMap.setOnMapClickListener(onMapClickListener)

        onView(withId(R.id.glSurface)).perform(click())
        delay(600)

        verify(onMapClickListener, times(1))
            .onMapClicked(Mockito.any(LatLng::class.java) ?: LatLng())
    }

    @Test
    fun testOnMapDoubleClickListener() = runBlocking {
        delay(400)
        mapfitMap.setOnMapDoubleClickListener(onMapDoubleClickListener)

        onView(withId(R.id.glSurface)).perform(doubleClick())
        delay(600)

        verify(onMapDoubleClickListener, times(1))
            .onMapDoubleClicked(Mockito.any(LatLng::class.java) ?: LatLng())
    }

    @Test
    fun testOnMapLongClickListener() = runBlocking {
        delay(400)
        mapfitMap.setOnMapLongClickListener(onMapLongClickListener)

        onView(withId(R.id.glSurface)).perform(longClick())
        delay(600)

        verify(onMapLongClickListener, times(1))
            .onMapLongClicked(Mockito.any(LatLng::class.java) ?: LatLng())
    }

    @Test
    fun testOnMapPanListener() = runBlocking {
        delay(400)
        mapfitMap.setOnMapPanListener(onMapPanListener)

        onView(withId(R.id.glSurface)).perform(swipeDown())
        delay(600)

        verify(onMapPanListener, atLeastOnce()).onMapPan()
    }

    @Test
    fun testOnMapPinchListener() = runBlocking {
        delay(400)
        mapfitMap.setOnMapPinchListener(onMapPinchListener)

        onView(withId(R.id.glSurface)).perform(pinchIn())
        delay(600)

        verify(onMapPinchListener, atLeastOnce()).onMapPinch()

        onView(withId(R.id.glSurface)).perform(pinchOut())
        delay(600)

        verify(onMapPinchListener, atLeastOnce()).onMapPinch()
    }

}