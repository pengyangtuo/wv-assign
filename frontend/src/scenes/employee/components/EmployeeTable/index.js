/**
 * Created by ypeng on 2017-03-29.
 */
import React, {PropTypes} from 'react';
import Table from '../../../../components/Table';
const Timestamp = require('react-timestamp');

const EmployeeTable = ({employees}) => {
  const headers = [
    "Employee Id",
    "Name",
    "Pay Group"
  ];

  const data = employees.map((employee) => {
    return [
      /* eslint-disable */
      (<b>{employee.id}</b>),
      (employee.firstName+" "+employee.lastName),
      (employee.group)
      /* eslint-enable */
    ];
  });

  return (
    <Table tableHeader={headers} tableData={data} />
  );
};

EmployeeTable.propTypes = {
  employees: PropTypes.array.isRequired
};

export default EmployeeTable;