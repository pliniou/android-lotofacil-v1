package com.cebolao.lotofacil.ui.icons

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector

object CebolaoIcons {
    val Error: ImageVector
        get() {
            if (_error != null) {
                return _error!!
            }
            _error = materialIcon(name = "CebolaoIcons.Error") {
                materialPath {
                    moveTo(12.0f, 2.0f)
                    curveTo(6.48f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                    reflectiveCurveToRelative(4.48f, 10.0f, 10.0f, 10.0f)
                    reflectiveCurveToRelative(10.0f, -4.48f, 10.0f, -10.0f)
                    reflectiveCurveTo(17.52f, 2.0f, 12.0f, 2.0f)
                    close()
                    moveTo(11.0f, 17.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(2.0f)
                    horizontalLineToRelative(-2.0f)
                    verticalLineToRelative(-2.0f)
                    close()
                    moveTo(11.0f, 7.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(7.0f)
                    horizontalLineToRelative(-2.0f)
                    verticalLineToRelative(-7.0f)
                    close()
                }
            }
            return _error!!
        }

    val Empty: ImageVector
        get() {
            if (_empty != null) {
                return _empty!!
            }
            _empty = materialIcon(name = "CebolaoIcons.Empty") {
                materialPath {
                    moveTo(19.0f, 3.0f)
                    lineTo(5.0f, 3.0f)
                    curveTo(3.9f, 3.0f, 3.0f, 3.9f, 3.0f, 5.0f)
                    verticalLineToRelative(14.0f)
                    curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                    horizontalLineToRelative(14.0f)
                    curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                    lineTo(21.0f, 5.0f)
                    curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                    close()
                    moveTo(9.0f, 17.0f)
                    lineTo(7.0f, 17.0f)
                    verticalLineToRelative(-7.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(7.0f)
                    close()
                    moveTo(13.0f, 17.0f)
                    horizontalLineToRelative(-2.0f)
                    lineTo(11.0f, 7.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(10.0f)
                    close()
                    moveTo(17.0f, 17.0f)
                    horizontalLineToRelative(-2.0f)
                    verticalLineToRelative(-4.0f)
                    horizontalLineToRelative(2.0f)
                    verticalLineToRelative(4.0f)
                    close()
                }
            }
            return _empty!!
        }

    private var _error: ImageVector? = null
    private var _empty: ImageVector? = null
}
