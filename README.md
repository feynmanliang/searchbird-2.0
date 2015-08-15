# Searchbird 2.0
A distributed inverted-index implementation for querying documents by
terms. Inspired by Searchbird from Twitter's [Scala
School](https://twitter.github.io/scala_school/searchbird.html) with the
following changes:

 * Replaced deprecated `ostrich` dependency with `twitter-server`
 * Uses the new client/server APIs for `finagle`

## Instructions
 1. Compile

    ```
    sbt compile && sbt assembly
    ````

 2. Start some shards

    ```
    sbt "run -admin.port=:8990 -shard=0" &
    sbt "run -admin.port=:8991 -shard=1" &
    sbt "run -admin.port=:8992 -shard=2" &
    ```

 3. Add some data:
    ```
    ./console localhost 9000
    > client.put("fromShardA", "a value from SHARD_A")
    > client.put("hello", "world")

    ./console localhost 9001
    > client.put("fromShardB", "a value from SHARD_B")
    > client.put("hello", "world again")
    ```

 4. Run the master and submit queries:
    ```
    sbt run &
    ./console localhost 9999
    finagle-client> client.get("hello")
    res0: String = world

    finagle-client> client.get("fromShardA")
    res1: String = a value from SHARD_A

    finagle-client> client.get("fromShardB")
    res2: String = a value from SHARD_B

    finagle-client> client.get("fromShardC")
    com.feynmanliang.searchbird.thrift.SearchbirdException: No such key

    finagle-client> client.search("hello").get
    res4: Seq[String] = List()

    finagle-client> client.search("world")get
    res5: Seq[String] = ArrayBuffer(hello)

    finagle-client> client.search("world").get
    res6: Seq[String] = ArrayBuffer(hello)

    finagle-client> client.search("value").get
    res7: Seq[String] = ArrayBuffer(fromShardA, fromShardB)
    ```

