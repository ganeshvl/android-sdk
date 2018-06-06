package com.mapfit.android.map

import android.location.Location
import android.support.test.annotation.UiThreadTest
import android.support.test.espresso.Espresso
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.idling.CountingIdlingResource
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.rule.GrantPermissionRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import com.mapfit.android.*
import com.mapfit.android.location.LocationListener
import com.mapfit.android.location.LocationPriority
import com.mapfit.tetragon.SceneUpdate
import kotlinx.android.synthetic.main.mf_overlay_map_controls.view.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


/**
 * Instrumentation tests for [MapOptions] functionality.
 *
 * Created by dogangulcan on 1/8/18.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MapOptionsTest {

    private lateinit var mapView: MapView

    private lateinit var mapfitMap: MapfitMap

    @Mock
    private lateinit var locationListener: LocationListener

    @Mock
    private lateinit var onMapThemeLoadListener: OnMapThemeLoadListener

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

    @Rule
    @JvmField
    val grantPermissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

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
            mapfitMap.setOnMapThemeLoadListener(onMapThemeLoadListener)
            mapfitMap.setOnMapPinchListener(onMapPinchListener)
            mapfitMap.setOnMapPanListener(onMapPanListener)
        })

        activityRule.activity.init()
    }

    @After
    fun cleanup() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    @UiThreadTest
    fun testInitValuesExistence() {
        Assert.assertNotNull(mapView)
        Assert.assertNotNull(mapfitMap)
    }

    @Test
    @UiThreadTest
    fun testDefaultValues() {
        Assert.assertEquals(MapTheme.MAPFIT_DAY, mapfitMap.getMapOptions().theme)
    }

    @Test
    @UiThreadTest
    fun testStyleChanges() {
        mapfitMap.getMapOptions().theme = MapTheme.MAPFIT_NIGHT
        Assert.assertEquals(MapTheme.MAPFIT_NIGHT, mapfitMap.getMapOptions().theme)
    }

    @Test
    @UiThreadTest
    fun testZoomControlVisibility() {
        mapfitMap.getMapOptions().zoomControlsEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.zoomControlsView.visibility)

        mapfitMap.getMapOptions().zoomControlsEnabled = false
        Assert.assertEquals(View.GONE, mapView.zoomControlsView.visibility)
    }

    @Test
    @UiThreadTest
    fun testCompassVisibility() {
        mapfitMap.getMapOptions().compassButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnCompass.visibility)

        mapfitMap.getMapOptions().compassButtonEnabled = false
        Assert.assertEquals(View.GONE, mapView.btnCompass.visibility)
    }

    @Test
    @UiThreadTest
    fun testRecenterVisibility() {
        mapfitMap.getMapOptions().recenterButtonEnabled = true
        Assert.assertEquals(View.VISIBLE, mapView.btnRecenter.visibility)

        mapfitMap.getMapOptions().recenterButtonEnabled = false
        Assert.assertEquals(View.GONE, mapView.btnRecenter.visibility)
    }

    /**
     * Location update is not mocked.
     */
    @Test
    fun testOnUserLocationListener() = runBlocking(UI) {
        delay(400)
        mapfitMap.getMapOptions().setUserLocationEnabled(
            true,
            LocationPriority.HIGH_ACCURACY,
            locationListener
        )

        delay(7000)

        Mockito.verify(locationListener, Mockito.atLeastOnce())
            .onLocation(Mockito.any(Location::class.java) ?: Location(""))

        mapfitMap.getMapOptions().setUserLocationEnabled(false)
    }

    @Test
    @UiThreadTest
    fun test3dBuildings() = runBlocking {

        mapfitMap.getMapOptions().is3dBuildingsEnabled = true

        delay(500)

        Mockito.verify(onMapThemeLoadListener, only()).onLoaded()
        Mockito.verify(onMapThemeLoadListener, never()).onError()
    }

    @Test
    @UiThreadTest
    fun testSceneUpdate() = runBlocking {

        val sceneUpdate = SceneUpdate("global.building_fill", "#ffffff")
        mapfitMap.getMapOptions().updateScene(listOf(sceneUpdate))

        delay(500)

        Mockito.verify(onMapThemeLoadListener, only()).onLoaded()
        Mockito.verify(onMapThemeLoadListener, never()).onError()
    }

    @Test
    fun testGestures() = runBlocking {
        delay(500)

        Assert.assertTrue(mapfitMap.getMapOptions().panEnabled)
        Assert.assertTrue(mapfitMap.getMapOptions().rotateEnabled)
        Assert.assertTrue(mapfitMap.getMapOptions().pinchEnabled)
        Assert.assertTrue(mapfitMap.getMapOptions().tiltEnabled)

        mapfitMap.getMapOptions().gesturesEnabled = false
        Assert.assertFalse(mapfitMap.getMapOptions().panEnabled)
        Assert.assertFalse(mapfitMap.getMapOptions().rotateEnabled)
        Assert.assertFalse(mapfitMap.getMapOptions().pinchEnabled)
        Assert.assertFalse(mapfitMap.getMapOptions().tiltEnabled)

        Espresso.onView(ViewMatchers.withId(R.id.glSurface)).perform(ViewActions.swipeDown())
        Espresso.onView(ViewMatchers.withId(R.id.glSurface)).perform(pinchIn())
        delay(300)

        Mockito.verify(onMapPanListener, never()).onMapPan()
        Mockito.verify(onMapPinchListener, never()).onMapPinch()

        mapfitMap.getMapOptions().gesturesEnabled = true
        Assert.assertTrue(mapfitMap.getMapOptions().panEnabled)
        Assert.assertTrue(mapfitMap.getMapOptions().rotateEnabled)
        Assert.assertTrue(mapfitMap.getMapOptions().pinchEnabled)
        Assert.assertTrue(mapfitMap.getMapOptions().tiltEnabled)

        Espresso.onView(ViewMatchers.withId(R.id.glSurface)).perform(ViewActions.swipeDown())
        Espresso.onView(ViewMatchers.withId(R.id.glSurface)).perform(pinchIn())
        delay(300)

        Mockito.verify(onMapPanListener, atLeastOnce()).onMapPan()
        Mockito.verify(onMapPinchListener, atLeastOnce()).onMapPinch()
    }


}
