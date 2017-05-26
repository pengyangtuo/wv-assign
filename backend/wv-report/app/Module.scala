import com.google.inject.AbstractModule
import models.ReportSummary
import repositories.recordSummary.{ReportSummaryRepository, ReportSummaryRepositoryImpl}
import repositories.report.{ReportRepository, ReportRepositoryImpl}
import repositories.reportRecord.{ReportRecordRepository, ReportRecordRepositoryImpl}
import services.report.{ReportService, ReportServiceImpl}
import services.reportRecord.{ReportRecordService, ReportRecordServiceImpl}
import services.reportSummary.{ReportSummaryService, ReportSummaryServiceImpl}

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.

 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
class Module extends AbstractModule {

  override def configure() = {
    /* Report bindings */
    bind(classOf[ReportService])
      .to(classOf[ReportServiceImpl])
      .asEagerSingleton

    bind(classOf[ReportRepository])
      .to(classOf[ReportRepositoryImpl])
      .asEagerSingleton

    /* Report Record bindings */
    bind(classOf[ReportRecordService])
      .to(classOf[ReportRecordServiceImpl])
      .asEagerSingleton

    bind(classOf[ReportRecordRepository])
      .to(classOf[ReportRecordRepositoryImpl])
      .asEagerSingleton

    /* Report Summary Record bindings */
    bind(classOf[ReportSummaryService])
      .to(classOf[ReportSummaryServiceImpl])
      .asEagerSingleton

    bind(classOf[ReportSummaryRepository])
      .to(classOf[ReportSummaryRepositoryImpl])
      .asEagerSingleton
  }

}
