package com.feynmanliang.searchbird

import com.twitter.logging.Logger
import com.twitter.util.Future
import config.SearchbirdServiceConfig
import com.feynmanliang.searchbird.thrift.{SearchbirdException, SearchbirdService}

import scala.collection.mutable

class SearchbirdServiceImpl(config: SearchbirdServiceConfig) extends SearchbirdService.FutureIface {
  val serverName = "Searchbird"
  val thriftPort = config.thriftPort
  val tracerFactory = config.tracerFactory
  private val log = Logger.get(getClass)

  val database = new mutable.HashMap[String, String]()

  def get(key: String) = {
    database.get(key) match {
      case None =>
        log.debug("get %s: miss", key)
        Future.exception(SearchbirdException("No such key"))
      case Some(value) =>
        log.debug("get %s: hit", key)
        Future(value)
    }
  }

  def put(key: String, value: String) = {
    log.debug("put %s", key)
    database(key) = value
    Future.Unit
  }

  def search(key: String) = ???
}

