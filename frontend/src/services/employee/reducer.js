/**
 * Created by yangtuopeng on 2017-03-29.
 */
import {actionType} from './action';

const init = {
  loading: 0,
  data: [],
  error: null,
  lastModifiedTime: 0
};

export default function employeeReducer(state = init, action) {
  switch (action.type) {
    case actionType.LOAD_EMPLOYEE_START:
      return Object.assign({}, state, {
        loading: 1
      });

    case actionType.LOAD_EMPLOYEE_SUCCESS:
      return Object.assign({}, state, {
        loading: 0,
        error: null,
        data: action.employees,
        lastModifiedTime: Date.now()
      });

    case actionType.LOAD_EMPLOYEE_FAIL:
      return Object.assign({}, state, {
        loading: 0,
        error: action.error
      });
    default:
      return state;
  }
}
