package com.androidatc.android_8_lab_10

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by HELMI on 11/6/2019.
 */
class Place {
    var id: String?= null
    var icon: String?= null
    var name: String?= null
    var vicinity: String?= null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    companion object {
        internal fun jsonToPointReference(pointRef: JSONObject): Place? {
            try {
                val result = Place()
                val geometry = pointRef.get("geometry") as JSONObject
                val location = geometry.get("location") as JSONObject

                result.latitude = location.get("lat") as Double
                result.longitude = location.get("lng") as Double
                result.icon = pointRef.getString("icon")
                result.name = pointRef.getString("name")
                result.vicinity = pointRef.getString("vicinity")
                result.id = pointRef.getString("id")

                return result
            } catch (ex: JSONException) {
            }
            return null
        }
    }
}