/**
 * Created by ypeng on 2017-03-29.
 */
import React, {PropTypes} from 'react';
import Table from '../../../../components/Table';
const Timestamp = require('react-timestamp');

const SummaryTable = ({summary}) => {
  const headers = [
    "Employee Id",
    "Amout Paid",
    "Pay Period Start",
    "Pay Period End",
    "Last Modified",
  ];

  const data = summary.map((summaryEntry) => {
    return [
      /* eslint-disable */
      (<b>{summaryEntry.employeeId}</b>),
      (summaryEntry.amount),
      (<Timestamp time={summaryEntry.payPeriodStart / 1000} format="date"/>),
      (<Timestamp time={summaryEntry.payPeriodEnd / 1000} format="date"/>),
      (<Timestamp time={summaryEntry.lastModifiedTime / 1000}/>)/**/
      /* eslint-enable */
    ];
  });

  return (
    <Table tableHeader={headers} tableData={data} />
  );
};

SummaryTable.propTypes = {
  summary: PropTypes.array.isRequired
};

export default SummaryTable;