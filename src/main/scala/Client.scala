package com.feynmanliang.searchbird

import com.feynmanliang.searchbird.thrift.SearchbirdService
import com.twitter.finagle.Thrift
import com.twitter.util.Future

class Client {
  val client = Thrift.newIface[SearchbirdService[Future]]("localhost:9999")

  def get(key: String) = client.get(key)()
  def put(key: String, value: String) = client.put(key, value)()
  def search(query: String) = client.search(query)
}
