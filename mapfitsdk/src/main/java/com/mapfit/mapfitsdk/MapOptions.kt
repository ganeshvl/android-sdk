package com.mapfit.mapfitsdk

import android.support.annotation.FloatRange
import android.support.v4.content.ContextCompat
import android.view.View
import com.mapfit.mapfitsdk.utils.isValidZoomLevel
import kotlinx.android.synthetic.main.overlay_map_controls.view.*

/**
 * Created by dogangulcan on 12/21/17.
 */
class MapOptions internal constructor(
        private val mapView: com.mapfit.mapfitsdk.MapView,
        private val mapController: MapController
) {

    companion object {
        const val MAP_MIN_ZOOM = 1.0
        const val MAP_MAX_ZOOM = 20.5
    }

    private var maxZoom: Float = 20.5f

    private var minZoom: Float = 1f

    private var theme: MapTheme? = null


    fun setTheme(theme: MapTheme) {
        if (this.theme == null || this.theme != theme) {
            updateScene(theme)
            this.theme = theme
        }
    }

    private fun updateScene(value: MapTheme) {
        mapController.loadSceneFile(value.toString())
        val attributionImage = when (value) {
            MapTheme.MAPFIT_DAY -> {
                mapView.btnLegal?.setTextColor(ContextCompat.getColor(mapView.context, R.color.dark_text))
                mapView.btnBuildYourMap?.setTextColor(ContextCompat.getColor(mapView.context, R.color.dark_text))
                R.drawable.ic_watermark_light
            }
            MapTheme.MAPFIT_NIGHT -> {
                mapView.btnLegal?.setTextColor(ContextCompat.getColor(mapView.context, R.color.light_text))
                mapView.btnBuildYourMap?.setTextColor(ContextCompat.getColor(mapView.context, R.color.light_text))
                R.drawable.ic_watermark_dark
            }
        }
        mapView.getAttributionImage().setImageResource(attributionImage)
    }

    var compassButtonEnabled = false
        set(value) {
            mapView.btnCompass.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var recenterButtonEnabled = false
        set(value) {
            mapView.btnRecenter.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    var zoomControlsEnabled = true
        set(value) {
            mapView.zoomControlsView.visibility = if (value) View.VISIBLE else View.GONE
            field = value
        }

    private var cameraType: CameraType = CameraType.PERSPECTIVE
        set(value) {
            mapController.cameraType = MapController.CameraType.valueOf(value.name)
            field = value
        }

    var panEnabled = true
        set(value) {
            mapController.touchInput.panEnabled = value
            field = value
        }

    var pinchEnabled = true
        set(value) {
            mapController.touchInput.pinchEnabled = value
            field = value
        }

    var rotateEnabled = true
        set(value) {
            mapController.touchInput.rotationEnabled = value
            field = value
        }

    var tiltEnabled = true
        set(value) {
            mapController.touchInput.tiltEnabled = value
            field = value
        }

    private var is3dBuildingsEnabled = true
        set(value) {
            TODO()
        }

    /**
     * @param zoomLevel desired maximum zoom level
     */
    internal fun setMaxZoom(@FloatRange(from = MAP_MIN_ZOOM, to = MAP_MAX_ZOOM) zoomLevel: Float) {
        if (isValidZoomLevel(zoomLevel)) {
            maxZoom = zoomLevel
        }
    }

    internal fun getMaxZoom() = maxZoom

    internal fun setMinZoom(@FloatRange(from = MAP_MIN_ZOOM, to = MAP_MAX_ZOOM) zoomLevel: Float) {
        if (isValidZoomLevel(zoomLevel)) {
            minZoom = zoomLevel
        }
    }

    private enum class CameraType {
        PERSPECTIVE,
        ISOMETRIC,
        FLAT
    }

    internal fun loadDefaultTheme() {
        updateScene(MapTheme.MAPFIT_DAY)
    }

}