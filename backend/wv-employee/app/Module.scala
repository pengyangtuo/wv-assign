import java.util.UUID

import com.google.inject.name.Names
import com.google.inject.{AbstractModule, Scopes}
import models.{Employee, PayGroup}
import play.api.libs.concurrent.AkkaGuiceSupport
import repositories.employee.{EmployeeRepository, EmployeeRepositoryImpl}
import repositories.payGroup.{PayGroupRepository, PayGroupRepositoryImpl}
import services.employee.{EmployeeService, EmployeeServiceImpl}
import services.payGroup.{PayGroupService, PayGroupServiceImpl}

/**
  * Created by yangtuopeng on 2017-03-25.
  */
class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bind(classOf[EmployeeRepository])
      .to(classOf[EmployeeRepositoryImpl])
      .asEagerSingleton

    bind(classOf[EmployeeService])
      .to(classOf[EmployeeServiceImpl])
      .asEagerSingleton

    bind(classOf[PayGroupRepository])
      .to(classOf[PayGroupRepositoryImpl])
      .asEagerSingleton

    bind(classOf[PayGroupService])
      .to(classOf[PayGroupServiceImpl])
      .asEagerSingleton
  }
}
