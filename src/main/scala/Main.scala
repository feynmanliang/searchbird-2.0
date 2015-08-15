package com.feynmanliang.searchbird

import com.feynmanliang.searchbird.config.SearchbirdServiceConfig
import com.twitter.finagle.Thrift
import com.twitter.util.{Await, Future}

object Main {
  def main(args: Array[String]): Unit = {
    val service = new SearchbirdServiceImpl(new SearchbirdServiceConfig())
    val server = Thrift.serveIface("localhost:9999", service)
    Await.ready(server)
  }
}
