package com.github.pshirshov.izumi.distage.model.exceptions

import com.github.pshirshov.izumi.fundamentals.reflection._

class UnsupportedWiringException(message: String, val tpe: RuntimeUniverse.TypeFull) extends DIException(message, null)