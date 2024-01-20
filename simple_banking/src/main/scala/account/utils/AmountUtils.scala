package account.utils

import eu.timepit.refined.api.{Refined, Validate}
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.refineV


object AmountUtils {
  trait RefinedNewType[A] {
    type Valid
    type Type = A Refined Valid

    final def apply(value: A)(implicit v: Validate[A, Valid]): Either[String, Type] =
      refineV[Valid](value)
  }

  object Amount extends RefinedNewType[Long] {
    type Valid = Positive
  }
  type Amount = Amount.Type

  object DebitAmount extends RefinedNewType[Long] {
    type Valid = Positive
  }
  type DebitAmount = DebitAmount.Type

  object CreditAmount extends RefinedNewType[Long] {
    case class Valid()

    implicit val validateCreditAmount: Validate.Plain[Long, Valid] = Validate.fromPredicate(
      value => value >= Long.MinValue && value <= Long.MaxValue,
      value => s"$value is not a valid CreditAmount",
      CreditAmount.Valid()
    )
  }
  type CreditAmount = CreditAmount.Type

  object CreditLimit extends RefinedNewType[Long] {
    type Valid = Positive
  }
  type CreditLimit = CreditLimit.Type

  object AccumulativeAmount extends RefinedNewType[Long] {
    type Valid = Positive
  }
  type AccumulativeAmount = AccumulativeAmount.Type
}

