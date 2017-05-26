import fetch from 'isomorphic-fetch';
import Config from '../../config/';

export const actionType = {
  LOAD_REPORT_START: "LOAD_REPORT_START",
  LOAD_REPORT_SUCCESS: "LOAD_REPORT_SUCCESS",
  LOAD_REPORT_FAIL: "LOAD_REPORT_FAIL",
  UPLOAD_REPORT_START: "UPLOAD_REPORT_START",
  UPLOAD_REPORT_SUCCESS: "UPLOAD_REPORT_SUCCESS",
  UPLOAD_REPORT_FAIL: "UPLOAD_REPORT_FAIL"
};

function loadReportStart() {
  return { type: actionType.LOAD_REPORT_START };
}

function loadReportSuccess(reports) {
  return {
    type: actionType.LOAD_REPORT_SUCCESS,
    reports
  };
}

function loadReportFail(error) {
  return {
    type: actionType.LOAD_REPORT_FAIL,
    error
  };
}

function loadReport() {
  return function(dispatch) {
    dispatch(loadReportStart());
    fetch(Config.serviceEndpoints.report)
      .then((response) => {
        if (response.status >= 400) {
          dispatch(loadReportFail("Bad response from server"));
        }

        return response.json();
      })
      .then((data) => {
        dispatch(loadReportSuccess(data));
      })
      .catch(err => {
        dispatch(loadReportFail("Service unavailable"));
      });
  };
}

function uploadReportStart() {
  return { type: actionType.UPLOAD_REPORT_START };
}

function uploadReportSuccess(report) {
  return {
    type: actionType.UPLOAD_REPORT_SUCCESS,
    report
  };
}

function uploadReportFail(error) {
  return {
    type: actionType.UPLOAD_REPORT_FAIL,
    error
  };
}

function uploadReport(file) {
  const formData = new FormData();
  formData.append('report', file, file.name);

  const headers = {
    'Accept': 'application/json, */*'
  };

  const options = {
    headers,
    method: 'POST',
    body: formData
  };

  return function(dispatch) {
    dispatch(uploadReportStart());
    fetch(Config.serviceEndpoints.report, options)
      .then((response) => {
        if(response.status == 409){
          dispatch(uploadReportFail("Uploaded duplicate report"));
          return;
        }else if(response.status > 400){
          dispatch(uploadReportFail("Bad response from server"));
          return;
        }
        return response.json();
      })
      .then((data) => {
        if(data){
          dispatch(uploadReportSuccess(data));
        }
      })
      .catch(err => {
        dispatch(uploadReportFail("Service unavailable"));
      });
  };
}

export const actions = {
  loadReportStart: loadReportStart,
  loadReportSuccess: loadReportSuccess,
  loadReportFail: loadReportFail,
  loadReport: loadReport,
  uploadReportStart: uploadReportStart,
  uploadReportSuccess: uploadReportSuccess,
  uploadReportFail: uploadReportFail,
  uploadReport: uploadReport
};