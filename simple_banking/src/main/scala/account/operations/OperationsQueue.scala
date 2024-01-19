package account.operations

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OperationsQueue {
  trait Operation {
    def execute(): Unit
  }

  private val queue = mutable.Queue[Operation]()

  def enqueue(operation: Operation): Unit = {
    queue.synchronized {
      queue.enqueue(operation)
    }
  }

  def startProcessing(): Unit = {
    Future {
      while (true) {
        queue.synchronized {
          if (queue.nonEmpty) {
            val operation = queue.dequeue()
            operation.execute()
          }
        }
      }
    }
  }
}
