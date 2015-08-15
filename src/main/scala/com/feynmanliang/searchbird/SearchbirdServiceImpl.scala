package com.feynmanliang.searchbird

import com.twitter.util.Future

import com.feynmanliang.searchbird.config.SearchbirdServiceConfig
import com.feynmanliang.searchbird.thrift.SearchbirdService

class SearchbirdServiceImpl(config: SearchbirdServiceConfig, index: Index)
    extends SearchbirdService[Future] {
  val serverName = "Searchbird"
  val thriftPort = config.thriftPort

  def get(key: String): Future[String] = index.get(key)
  def put(key: String, value: String): Future[Unit] =
    index.put(key, value) flatMap { _ => Future.Unit }
  def search(query: String): Future[Seq[String]] = index.search(query)
}

