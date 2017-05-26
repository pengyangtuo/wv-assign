import React, {PropTypes} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import Header from '../../components/Header';
import {summaryActions} from '../../services/summary';
import SummaryTable from './components/SummaryTable';
import Timestamp from 'react-timestamp';
import Loading from '../../components/Loading';

class SummaryPage extends React.Component {

  constructor(props, context) {
    super(props, context);

    this.lastModifedMessage = this.lastModifedMessage.bind(this);
  }

  lastModifedMessage() {
    return (
      <small className="text-gray">
        Last loaded: <Timestamp time={this.props.summary.lastModifiedTime / 1000}/>
      </small>
    );
  }

  render() {
    return (
      <div className="container">
        <div className="row">
          <Header title="Summary"/>
        </div>

        <div className="row">
          <Loading isLoading={this.props.summary.loading == 1} finishMsg={this.lastModifedMessage}
          />
        </div>
        <div className="row">

          { this.props.summary.error != null &&
          <div className="alert alert-danger" role="alert">
            <strong>Something is wrong: </strong> {this.props.summary.error}.
          </div>
          }

          <SummaryTable summary={this.props.summary.data}/>
        </div>
      </div>
    );
  }
}

SummaryPage.propTypes = {
  summary: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired
};

function mapStateToProps(state, ownProps) {
  return {
    summary: state.summary
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(summaryActions, dispatch)
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SummaryPage);
