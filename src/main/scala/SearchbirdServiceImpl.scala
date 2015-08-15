package com.feynmanliang.searchbird

import java.util.concurrent.ConcurrentHashMap

import com.twitter.logging.Logger
import com.twitter.util.Future
import config.SearchbirdServiceConfig
import com.feynmanliang.searchbird.thrift.{SearchbirdException, SearchbirdService}

import scala.collection.mutable

class SearchbirdServiceImpl(config: SearchbirdServiceConfig) extends SearchbirdService[Future] {
  val serverName = "Searchbird"
  val thriftPort = config.thriftPort
  private val log = Logger.get(getClass)

  val forward = new ConcurrentHashMap[String, String]()
  val reverse = new ConcurrentHashMap[String, Set[String]]()

  def get(key: String) = {
    forward.get(key) match {
      case null =>
        log.debug("get %s: miss", key)
        Future.exception(SearchbirdException("No such key"))
      case value =>
        log.debug("get %s: hit", key)
        Future(value)
    }
  }

  def put(key: String, value: String) = {
    log.debug("put %s", key)

    // remove any references to the old document in the reverse index.
    if (forward.containsKey(key)) {
      synchronized {
        forward.get(key).split(" ").toSet foreach { (token: String) =>
          val current = reverse.get(token) match {
            case null => Set[String]()
            case value => value
          }
          reverse.put(token, current - key)
        }
      }
    }

    // add the document to the forward index.
    forward.put(key, value)

    // add the tokens of document to inverted index.
    synchronized {
      value.split(" ").toSet foreach { (token: String) =>
        val current = reverse.get(token) match {
          case null => Set[String]()
          case value => value
        }
        reverse.put(token, current + key)
      }
    }

    Future.Unit
  }

  def search(query: String) = Future.value {
    val tokens = query.split(" ")
    val hits = tokens map { token =>
      reverse.get(token) match {
        case null => Set[String]()
        case value => value
      }
    }
    val intersected = hits reduceLeftOption { _ intersect _ } getOrElse Set()
    intersected.toList
  }
}

