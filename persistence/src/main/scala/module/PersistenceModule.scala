package module

import com.google.inject.AbstractModule
import databaseComponent.MongoDB.MongoUserDAO
import databaseComponent.Slick.SlickUserDAO
import databaseComponent.UserDAO

class PersistenceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[UserDAO]).to(classOf[MongoUserDAO])
  }
}
