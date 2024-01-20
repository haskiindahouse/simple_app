import account.CreditAccount
import account.utils.AmountUtils.{CreditAmount, CreditLimit}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal
import org.scalatest.time.{Millis, Seconds, Span}

class CreditAccountSpec extends AnyFunSpec with Matchers with ScalaFutures {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  describe("CreditAccount") {
    def testReplenishment(initialBalance: Long, amount: Long, limit: Long, expectedBalance: Long): Unit = {
      it(s"should replenish $amount to an account with initial balance $initialBalance and limit $limit resulting in $expectedBalance") {
        val account = for {
          initialAmount <- CreditAmount(initialBalance)
          creditLimit <- CreditLimit(limit)
        } yield CreditAccount(initialAmount, creditLimit)

        account match {
          case Right(acc) =>
            val replenishedAccount = acc.replenishment(CreditAmount(amount).getOrElse(fail("Invalid replenish amount")))
            replenishedAccount.balance.value shouldBe expectedBalance
          case Left(error) => fail(s"Failed to create account: $error")
        }
      }
    }

    def testWithdrawal(initialBalance: Long, amount: Long, limit: Long, isAllowed: Boolean): Unit = {
      it(s"should ${if (!isAllowed) "not " else ""}allow withdrawal of $amount from an account with initial balance $initialBalance and limit $limit") {
        val account = for {
          initialAmount <- CreditAmount(initialBalance).right
          creditLimit <- CreditLimit(limit).right
        } yield CreditAccount(initialAmount, creditLimit)

        account match {
          case Right(acc) =>
            val withdrawalAmount = CreditAmount(amount).getOrElse(fail("Invalid withdrawal amount"))
            val futureResult = acc.withdrawal(withdrawalAmount).map(Some(_)).recover {
              case NonFatal(_) => None
            }

            whenReady(futureResult) { result =>
              if (isAllowed) {
                result shouldBe defined
                result.get.balance.value should be(initialBalance - amount)
              } else {
                result should not be defined
              }
            }
          case Left(error) => fail(s"Failed to create account: $error")
        }
      }
    }

    testReplenishment(-50, 100, 200, 50)
    testReplenishment(-100, 0, 200, -100)

    testWithdrawal(-50, 50, 200, isAllowed = true)
    testWithdrawal(-50, 300, 200, isAllowed = false)
  }
}
