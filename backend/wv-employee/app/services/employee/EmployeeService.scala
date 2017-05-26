package services.employee

import models.Employee
import wv.service.services.ApiService

/**
  * Created by yangtuopeng on 2017-03-26.
  */
trait EmployeeService extends ApiService[Employee, Int]
