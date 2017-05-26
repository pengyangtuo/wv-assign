/**
 * Created by yangtuopeng on 2017-03-28.
 */
import React, {PropTypes} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {reportActions} from '../../services/report';
import Timestamp from 'react-timestamp';
import ReportTable from './components/ReportTable';
import Loading from '../../components/Loading';
import Header from '../../components/Header';
import Upload from '../../components/Upload';
import style from './ReportPage.scss';

class ReportPage extends React.Component {

  constructor(props, context) {
    super(props, context);

    this.lastModifedMessage = this.lastModifedMessage.bind(this);
    this.handleUpload = this.handleUpload.bind(this);
  }

  componentDidMount() {
    this.props.actions.loadReport();
  }

  lastModifedMessage() {
    return (
      <small className="text-gray">
        Last loaded: <Timestamp time={this.props.report.lastModifiedTime / 1000}/>
      </small>
    );
  }

  handleUpload(file) {
    this.props.actions.uploadReport(file);
  }

  render() {
    return (
      <div className="container">
        <div className="row">
          <Header title="Report"/>
          <Upload uploadHandler={this.handleUpload}/>
        </div>

        <div className="row">
          <Loading isLoading={this.props.report.loading == 1} finishMsg={this.lastModifedMessage}/>
        </div>

        <div className="row">

          { this.props.report.error != null &&
          <div className="alert alert-danger" role="alert">
            <strong>Something is wrong: </strong> {this.props.report.error}.
          </div>
          }

          <ReportTable reports={this.props.report.data}/>
        </div>
      </div>
    );
  }
}

ReportPage.propTypes = {
  report: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired
};

function mapStateToProps(state, ownProps) {
  return {
    report: state.report
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(reportActions, dispatch)
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(ReportPage);
