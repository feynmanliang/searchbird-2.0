package com.feynmanliang.searchbird

import com.feynmanliang.searchbird.config.SearchbirdServiceConfig
import com.twitter.finagle.Thrift
import com.twitter.util.Await

object Main {
  def main(args: Array[String]): Unit = {
    val config = new SearchbirdServiceConfig()
    val service = new SearchbirdServiceImpl(config)
    val server = Thrift.serveIface("localhost:" + config.thriftPort, service)
    Await.ready(server)
  }
}
