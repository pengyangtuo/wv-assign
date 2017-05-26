package repositories.employee

import models.Employee
import wv.service.repository.Repository

/**
  * Created by yangtuopeng on 2017-03-26.
  */
trait EmployeeRepository extends Repository[Employee, Int]
