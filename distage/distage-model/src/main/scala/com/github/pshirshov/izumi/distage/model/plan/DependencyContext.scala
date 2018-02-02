package com.github.pshirshov.izumi.distage.model.plan

import com.github.pshirshov.izumi.fundamentals.reflection._

sealed trait DependencyContext {

}

object DependencyContext {

  case class MethodContext(definingClass: RuntimeUniverse.TypeFull) extends DependencyContext

  sealed trait ParameterContext extends DependencyContext

  case class ConstructorParameterContext(definingClass: RuntimeUniverse.TypeFull, constructor: RuntimeUniverse.SelectedConstructor) extends ParameterContext

  case class MethodParameterContext(factoryClass: RuntimeUniverse.TypeFull, factoryMethod: RuntimeUniverse.MethodSymb) extends ParameterContext

  case class CallableParameterContext(definingCallable: RuntimeUniverse.Callable) extends ParameterContext

}