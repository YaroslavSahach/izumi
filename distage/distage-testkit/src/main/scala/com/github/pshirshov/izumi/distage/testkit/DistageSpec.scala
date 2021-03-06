package com.github.pshirshov.izumi.distage.testkit

import com.github.pshirshov.izumi.distage.model.reflection.universe.RuntimeDIUniverse.TagK
import org.scalatest.{ScalatestSuite, WordSpecLike}

abstract class DistageSpec[F[_] : TagK] extends DistageTestSupport[F] with WordSpecLike {
  override def toString: String = ScalatestSuite.suiteToString(None, this)
}



