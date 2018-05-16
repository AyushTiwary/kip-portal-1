package model

import akka.actor.ActorSystem
import com.outworkers.phantom.dsl.{DatabaseProvider, context}
import com.typesafe.config.ConfigFactory
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll}

import scala.concurrent.duration.DurationInt

trait AppDbProvider extends DatabaseProvider[AppDatabase] {
  val config = ConfigFactory.load()
  var appDatabase: AppDatabase  = null

  def prepareDatabase: AppDatabase = {
    appDatabase = new PortalDataBase(config).getClusterDB
    appDatabase
  }
  override def database: AppDatabase = appDatabase
}


trait TestSuite extends AsyncFlatSpec with AppDbProvider with BeforeAndAfterAll {
  val system = ActorSystem("kip-portal")
  override def beforeAll(): Unit = {
    try {
      prepareDatabase
      database.create(100 seconds)
    } catch {
      case _: Exception =>
        EmbeddedCassandraServerHelper.startEmbeddedCassandra("test-cassandra.yaml", 1000000L)
        prepareDatabase
        database.create(100 seconds)
    }
  }

  override def afterAll(): Unit = {
    database.truncate(100 seconds)
  }
}
