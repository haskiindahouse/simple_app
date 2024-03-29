package account

import scala.concurrent.Future

trait Account[A] {
  val id: String
  def balance: A
  def replenishment(amount: A): Account[A]
  def withdrawal(amount: A): Future[Account[A]]
}