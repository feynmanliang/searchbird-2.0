package com.feynmanliang.searchbird

import java.util.concurrent.ConcurrentHashMap

import com.twitter.conversions.time._
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.thrift.ThriftClientFramedCodec
import com.twitter.util._
import com.twitter.logging.Logger

import com.feynmanliang.searchbird.thrift.{SearchbirdService, SearchbirdException}

trait Index {
  def get(key: String): Future[String]
  def put(key: String, value: String): Future[Unit]
  def search(key: String): Future[Seq[String]]
}

class RemoteIndex(hosts: String) extends Index {
  val transport = ClientBuilder()
    .name("remoteIndex")
    .hosts(hosts)
    .codec(ThriftClientFramedCodec())
    .hostConnectionLimit(1)
    .timeout(500.milliseconds)
    .build()
  val client = new SearchbirdService.FinagledClient(transport)

  def get(key: String) = client.get(key)
  def put(key: String, value: String) = client.put(key, value) map { _ => () }
  def search(query: String) = client.search(query) map { _.toList }
}

class CompositeIndex(indices: Seq[Index]) extends Index {
  require(!indices.isEmpty)

  def get(key: String) = {
    val queries = indices.map { idx =>
      idx.get(key) map { r => Some(r) } handle { case e => None }
    }

    Future.collect(queries) flatMap { results =>
      results.find { _.isDefined } map { _.get } match {
        case Some(v) => Future.value(v)
        case None => Future.exception(SearchbirdException("No such key"))
      }
    }
  }

  def put(key: String, value: String) =
    Future.exception(SearchbirdException("put() not supported by CompositeIndex"))

  def search(query: String) = {
    val queries = indices.map { _.search(query) rescue { case _=> Future.value(Nil) } }
    Future.collect(queries) map { results => (Set() ++ results.flatten) toList }
  }
}

class ResidentIndex extends Index {
  val log = Logger.get(getClass)

  val forward = new ConcurrentHashMap[String, String]()
  val reverse = new ConcurrentHashMap[String, Set[String]]

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
          val current = Option(reverse.get(token)) getOrElse Set()
          reverse.put(token, current - key)
        }
      }
    }

    forward.put(key, value)

    // admit only one updater.
    synchronized {
      (Set() ++ value.split(" ")) foreach { token =>
        val current = Option(reverse.get(token)) getOrElse Set()
        reverse.put(token, current + key)
      }
    }

    Future.Unit
  }

  def search(query: String) = Future.value {
    val tokens = query.split(" ")
    val hits = tokens map { token => Option(reverse.get(token)) getOrElse Set() }
    val intersected = hits reduceLeftOption { _ & _ } getOrElse Set()
    intersected.toList
  }
}