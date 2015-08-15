package com.feynmanliang.searchbird

import com.feynmanliang.searchbird.config.SearchbirdServiceConfig
import com.feynmanliang.searchbird.thrift.SearchbirdService
import com.feynmanliang.searchbird.thrift.SearchbirdService.FinagledService
import com.twitter.finagle.Thrift
import com.twitter.server.TwitterServer
import com.twitter.util.Await

object Main extends TwitterServer {
  val shard = flag[Int]("shard", "shard identifier")

  def main(): Unit = {
    val config = new SearchbirdServiceConfig()
    val service = config(shard.get)
    val server = Thrift.serveIface("localhost:" + config.thriftPort, service)

    onExit {
      server.close()
      adminHttpServer.close()
    }
    Await.ready(server)
    Await.ready(adminHttpServer)
  }
}
