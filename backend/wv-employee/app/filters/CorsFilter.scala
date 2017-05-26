package filters

import javax.inject.Inject

import play.api.http.DefaultHttpFilters
import play.filters.cors.CORSFilter

/**
  * Created by ypeng on 2017-03-29.
  */
class CorsFilter @Inject()(corsFilter: CORSFilter) extends DefaultHttpFilters(corsFilter){

}