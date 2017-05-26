package services.report

import models.{Report, ReportRecord}
import wv.service.services.{ApiService, FileService}

/**
  * Created by yangtuopeng on 2017-03-26.
  */
trait ReportService extends ApiService[Report, Int] with FileService[List[ReportRecord]]
