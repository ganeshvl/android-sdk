package com.mapfit.mapfitsdk

import com.mapfit.mapfitsdk.geometry.LatLng

/**
 * Created by dogangulcan on 1/4/18.
 */
interface OnMapDoubleClickListener{

    fun onMapDoubleClicked(latLng: LatLng)

}