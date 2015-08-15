package com.feynmanliang.searchbird

import com.feynmanliang.searchbird.config.SearchbirdServiceConfig
import com.twitter.finagle.Thrift
import com.twitter.server.TwitterServer
import com.twitter.util.Await

object Main extends TwitterServer {
  def main(): Unit = {
    val config = new SearchbirdServiceConfig()
    val service = new SearchbirdServiceImpl(config)
    val server = Thrift.serveIface("localhost:" + config.thriftPort, service)

    Await.ready(server)
    Await.ready(adminHttpServer)
  }
}
