/**
 * Created by yangtuopeng on 2017-03-29.
 */
import {actionType} from './action';

const init = {
  loading: 0,
  uploading: 0,
  data: [],
  error: null,
  lastModifiedTime: 0
};

export default function reportReducer(state = init, action) {
  switch (action.type) {
    case actionType.LOAD_REPORT_START:
      return Object.assign({}, state, {
        loading: 1
      });

    case actionType.LOAD_REPORT_SUCCESS:
      return Object.assign({}, state, {
        loading: 0,
        error: null,
        data: action.reports,
        lastModifiedTime: Date.now()
      });

    case actionType.LOAD_REPORT_FAIL:
      return Object.assign({}, state, {
        loading: 0,
        error: action.error
      });
    case actionType.UPLOAD_REPORT_START:
      return Object.assign({}, state, {
        uploading: 1
      });
    case actionType.UPLOAD_REPORT_SUCCESS:
      return Object.assign({}, state, {
        uploading: 0,
        data: [
          ...state.data,
          action.report
        ]
      });
    case actionType.UPLOAD_REPORT_FAIL:
      return Object.assign({}, state, {
        uploading: 0,
        error: action.error
      });
    default:
      return state;
  }
}
