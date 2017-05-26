import fetch from 'isomorphic-fetch';
import Config from '../../config/';

export const actionType = {
  LOAD_SUMMARY_START: "LOAD_SUMMARY_START",
  LOAD_SUMMARY_SUCCESS: "LOAD_SUMMARY_SUCCESS",
  LOAD_SUMMARY_FAIL: "LOAD_SUMMARY_FAIL"
};

function loadSummaryStart() {
  return { type: actionType.LOAD_SUMMARY_START };
}

function loadSummarySuccess(summary) {
  return {
    type: actionType.LOAD_SUMMARY_SUCCESS,
    summary
  };
}

function loadSummaryFail(error) {
  return {
    type: actionType.LOAD_SUMMARY_FAIL,
    error
  };
}

function loadSummary() {
  return function(dispatch) {
    dispatch(loadSummaryStart());
    fetch(Config.serviceEndpoints.summary)
      .then((response) => {
        if (response.status >= 400) {
          dispatch(loadSummaryFail("Bad response from server"));
        }

        return response.json();
      })
      .then((data) => {
        dispatch(loadSummarySuccess(data));
      })
      .catch(err => {
        dispatch(loadSummaryFail("Service unavailable"));
      });
  };
}

export const actions = {
  loadSummaryStart: loadSummaryStart,
  loadSummarySuccess: loadSummarySuccess,
  loadSummaryFail: loadSummaryFail,
  loadSummary: loadSummary
};