package com.github.pshirshov.izumi.idealingua.model.il.ast.raw.defns

import com.github.pshirshov.izumi.idealingua.model.il.ast.raw.typeid.RawRef

sealed trait RawMethod {
  def meta: RawNodeMeta
}

object RawMethod {

  sealed trait Output

  object Output {

    sealed trait NonAlternativeOutput extends Output

    final case class Struct(input: RawSimpleStructure) extends NonAlternativeOutput

    final case class Algebraic(alternatives: List[RawAdt.Member], contract: Option[RawStructure]) extends NonAlternativeOutput

    final case class Singular(typeId: RawRef) extends NonAlternativeOutput

    final case class Void() extends NonAlternativeOutput

    final case class Alternative(success: NonAlternativeOutput, failure: NonAlternativeOutput) extends Output

  }

  final case class Signature(input: RawSimpleStructure, output: Output)

  final case class RPCMethod(name: String, signature: Signature, meta: RawNodeMeta) extends RawMethod

}




