import {combineReducers, createStore, applyMiddleware} from 'redux';
import {summaryReducer} from './services/summary';
import {employeeReducer} from './services/employee';
import {reportReducer} from './services/report';
import thunk from 'redux-thunk';

// combine reducer
const rootReducer = combineReducers({
  summary: summaryReducer,
  employee: employeeReducer,
  report: reportReducer
});

export default function configStore(initialState) {
  return createStore(
    rootReducer,
    initialState,
    applyMiddleware(thunk)
  );
}

export const initialState = {
  summary: {
    loading: 0,
    data: [],
    error: null,
    lastModifiedTime: Date.now()
  },
  employee: {
    loading: 0,
    data: [],
    error: null,
    lastModifiedTime: Date.now()
  },
  report: {
    loading: 0,
    uploading: 0,
    data: [],
    error: null,
    lastModifiedTime: Date.now()
  }
};
