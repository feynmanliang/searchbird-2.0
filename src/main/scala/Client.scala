import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.thrift.ThriftClientFramedCodec
import org.apache.thrift.protocol.TBinaryProtocol
import java.net.InetSocketAddress

class Client {
  val service = ClientBuilder()
    .hosts(Seq(new InetSocketAddress("localhost", 9999)))
    .codec(ThriftClientFramedCodec())
    .hostConnectionLimit(1)
    .build()

  val client = new thrift.SearchbirdService.FinagledClient(service, new TBinaryProtocol.Factory)

  def get(key: String) = client.get(key)()
  def put(key: String, value: String) = client.put(key, value)()
  def search(query: String) = client.search(query)
}
