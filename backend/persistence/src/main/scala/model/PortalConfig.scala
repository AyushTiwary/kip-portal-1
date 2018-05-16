package model

import com.typesafe.config.Config

class PortalConfig(config: Config) {

  val cassandraHosts = config.getString("cassandra.host")
  val cassandraKeyspace = config.getString("cassandra.keyspace")
}
