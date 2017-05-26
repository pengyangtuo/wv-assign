import fetch from 'isomorphic-fetch';
import Config from '../../config/';

export const actionType = {
  LOAD_EMPLOYEE_START: "LOAD_EMPLOYEE_START",
  LOAD_EMPLOYEE_SUCCESS: "LOAD_EMPLOYEE_SUCCESS",
  LOAD_EMPLOYEE_FAIL: "LOAD_EMPLOYEE_FAIL"
};

function loadEmployeeStart() {
  return { type: actionType.LOAD_EMPLOYEE_START };
}

function loadEmployeeSuccess(employees) {
  return {
    type: actionType.LOAD_EMPLOYEE_SUCCESS,
    employees
  };
}

function loadEmployeeFail(error) {
  return {
    type: actionType.LOAD_EMPLOYEE_FAIL,
    error
  };
}

function loadEmployee() {
  return function(dispatch) {
    dispatch(loadEmployeeStart());
    fetch(Config.serviceEndpoints.employee)
      .then((response) => {
        if (response.status >= 400) {
          dispatch(loadEmployeeFail("Bad response from server"));
        }

        return response.json();
      })
      .then((data) => {
        dispatch(loadEmployeeSuccess(data));
      })
      .catch(err => {
        dispatch(loadEmployeeFail("Service unavailable"));
      });
  };
}

export const actions = {
  loadEmployeeStart: loadEmployeeStart,
  loadEmployeeSuccess: loadEmployeeSuccess,
  loadEmployeeFail: loadEmployeeFail,
  loadEmployee: loadEmployee
};