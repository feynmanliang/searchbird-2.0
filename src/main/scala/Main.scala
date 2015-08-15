import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}

object Main {
  def main(args: Array[String]): Unit = {
    val index = {
      val remotes = List("a", "b", "c") map { new RemoteIndex(_) }
      new CompositeIndex(remotes)
    }
    val service = new SearchbirdServiceImpl(index)
    val server = Thrift.serveIface("localhost:9999", service)
    Await.ready(server)
  }
}
