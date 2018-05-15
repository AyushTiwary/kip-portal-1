package model

import com.outworkers.phantom.connectors.{CassandraConnection, ContactPoints}
import com.typesafe.config.Config

class PortalDataBase(config: Config) extends PortalConfig(config) {
  private val connector: CassandraConnection = createClusterDBConnector
  private val database: AppDatabase = createClusterDB

  private def createClusterDBConnector: CassandraConnection =
    ContactPoints(List(cassandraHosts)).keySpace(cassandraKeyspace)

  private def createClusterDB: AppDatabase =
    new AppDatabase(connector)

  def getClusterDB: AppDatabase =
    database
}
