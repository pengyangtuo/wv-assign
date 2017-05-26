import React, {PropTypes} from 'react';
import Nav from './components/Nav/';
import {Route, Redirect} from 'react-router-dom';
import Summary from './scenes/summary/SummaryPage';
import Employee from './scenes/employee/EmployeePage';
import Report from './scenes/report/ReportPage';
import Websocket from 'react-websocket';
import {summaryActions} from './services/summary';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import Config from './config';

class App extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.handleNotifierData = this.handleNotifierData.bind(this);
  }

  defaultRoute () {
    return <Redirect to="/summary"/>;
  }

  handleNotifierData(data) {
    const jsonData = JSON.parse(data);
    if (jsonData.service == "summary"
      && jsonData.lastUpdateTime > this.props.summary.lastModifiedTime) {
      this.props.summaryActions.loadSummary();
    }
  }

  render () {
    return (
      <div className="container">

        <Websocket url={Config.serviceEndpoints.notifier}
                   onMessage={this.handleNotifierData}
                   debug={1==1}/>

        <div className="row">
          <Nav />
          <div className="col-8">
            <Route exact path="/" component={this.defaultRoute} />

            <Route path="/summary" component={Summary} />
            <Route path="/employee" component={Employee} />
            <Route path="/report" component={Report} />

          </div>
        </div>
      </div>
    );
  }
}

App.propTypes = {
  location: PropTypes.object.isRequired,
  summaryActions: PropTypes.object.isRequired,
  summary: PropTypes.object.isRequired
};

function mapStateToProps(state, ownProps) {
  return {
    summary: state.summary
  };
}

function mapDispatchToProps(dispatch) {
  return {
    summaryActions: bindActionCreators(summaryActions, dispatch)
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(App);
