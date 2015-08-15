package com.feynmanliang.searchbird.config

import com.feynmanliang.searchbird.{ResidentIndex, SearchbirdServiceImpl, CompositeIndex, RemoteIndex}

class SearchbirdServiceConfig extends {
  var thriftPort: Int = 9999
  var shards: Seq[String] = Seq(
    "localhost:9000",
    "localhost:9001",
    "localhost:9002"
  )

  def apply(shard: Option[Int]) = {
    val index = shard match {
      case Some(arg) =>
        val which = arg.toInt
        if (which >= shards.size || which < 0)
          throw new Exception("invalid shard number %d".format(which))

        // override with the shard port
        val Array(_, port) = shards(which).split(":")
        thriftPort = port.toInt

        new ResidentIndex

      case None =>
        require(!shards.isEmpty)
        val remotes = shards map { new RemoteIndex(_) }
        new CompositeIndex(remotes)
    }

    new SearchbirdServiceImpl(this, index)
  }
}
