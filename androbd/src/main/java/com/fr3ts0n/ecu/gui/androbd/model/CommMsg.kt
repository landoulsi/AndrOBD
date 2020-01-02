package com.fr3ts0n.ecu.gui.androbd.model

import android.os.Bundle

data class CommMsg @JvmOverloads constructor(
    val what: Int,
    val obj: Any?,
    val data: Bundle = Bundle()
)