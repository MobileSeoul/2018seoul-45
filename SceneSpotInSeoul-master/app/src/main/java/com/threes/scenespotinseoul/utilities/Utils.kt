@file:JvmName("Utils")
package com.threes.scenespotinseoul.utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.threes.scenespotinseoul.data.model.Location

fun launchExternalMap(context: Context, location: Location) {
    val intent = Intent(Intent.ACTION_VIEW)
    val mapUri = Uri.parse("geo:${location.lat},${location.lon}?z=20")
    intent.data = mapUri
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}