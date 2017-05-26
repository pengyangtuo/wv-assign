/**
 * Created by yangtuopeng on 2017-03-28.
 */
import React, {PropTypes} from 'react';
import {connect} from 'react-redux';
import {bindActionCreators} from 'redux';
import {employeeActions} from '../../services/employee';
import Timestamp from 'react-timestamp';
import EmployeeTable from './components/EmployeeTable';
import Loading from '../../components/Loading';
import Header from '../../components/Header';

class EmployeePage extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.lastModifedMessage = this.lastModifedMessage.bind(this);
  }

  componentDidMount() {
    this.props.actions.loadEmployee();
  }

  lastModifedMessage() {
    return (
      <small className="text-gray">
        Last loaded: <Timestamp time={this.props.employee.lastModifiedTime / 1000}/>
      </small>
    );
  }

  render() {
    return (
      <div className="container">
        <div className="row">
          <Header title="Employee"/>
        </div>

        <div className="row">
          <Loading isLoading={this.props.employee.loading == 1} finishMsg={this.lastModifedMessage}
          />
        </div>

        <div className="row">

          { this.props.employee.error != null &&
            <div className="alert alert-danger" role="alert">
              <strong>Something is wrong: </strong> {this.props.employee.error}.
            </div>
          }

          <EmployeeTable employees={this.props.employee.data}/>

        </div>
      </div>
    );
  }
}

EmployeePage.propTypes = {
  employee: PropTypes.object.isRequired,
  actions: PropTypes.object.isRequired
};

function mapStateToProps(state, ownProps) {
  return {
    employee: state.employee
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(employeeActions, dispatch)
  };
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EmployeePage);
