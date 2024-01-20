import account.DebitAccount
import account.utils.AmountUtils.DebitAmount
import org.scalatest.concurrent.Futures.timeout
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

class DebitAccountSpec extends AnyFunSpec with Matchers {

  describe("DebitAccount") {
    def testReplenishment(initialBalance: Long, amount: Long, expectedBalance: Long): Unit = {
      it(s"should replenish $amount to $initialBalance resulting in $expectedBalance") {
        val account = DebitAmount(initialBalance) match {
          case Right(validAmount) => DebitAccount(validAmount)
          case Left(error) => fail(s"Invalid initial balance: $error")
        }
        val replenishAmount = DebitAmount(amount).getOrElse(fail("Invalid replenish amount"))
        val updatedAccount = account.replenishment(replenishAmount)
        updatedAccount.balance.value shouldBe expectedBalance
      }
    }


    def testWithdrawal(initialBalance: Long, amount: Long, isAllowed: Boolean): Unit = {
      it(s"should ${if (!isAllowed) "not " else ""}allow withdrawal of $amount from $initialBalance") {
        val initialAmount = DebitAmount(initialBalance).getOrElse(fail("Invalid initial balance"))
        val withdrawalAmount = DebitAmount(amount).getOrElse(fail("Invalid withdrawal amount"))
        val account = DebitAccount(initialAmount)

        val futureResult = account.withdrawal(withdrawalAmount).map(Some(_)).recover {
          case NonFatal(_) => None
        }

        ScalaFutures.whenReady(futureResult, timeout(Span(5, Seconds))) { result =>
          if (isAllowed) {
            result shouldBe defined
            result.get.balance.value should be(initialBalance - amount)
          } else {
            result should not be defined
          }
        }
      }
    }


    describe("DebitAccount Replenishment") {
      testReplenishment(100, 50, 150)
      testReplenishment(100, 1, 101)
      testReplenishment(100, 10000000000L, 10000000100L)
    }

    describe("DebitAccount Withdrawal") {
      testWithdrawal(100, 50, isAllowed = true)
      testWithdrawal(100, 99, isAllowed = true)
      testWithdrawal(100, 150, isAllowed = false)
      testWithdrawal(100, 1, isAllowed = true)
    }
  }
}