/**
 * Created by ypeng on 2017-03-29.
 */
import React, {PropTypes} from 'react';
import Table from '../../../../components/Table';
import Timestamp from 'react-timestamp';
import Config from '../../../../config';

const ReportTable = ({reports}) => {
  const headers = [
    "Report Id",
    "Upload Time",
    "File"
  ];

  const data = reports.map((report) => {
    return [
      /* eslint-disable */
      (<b>{report.id}</b>),
      (<Timestamp time={report.uploadedTime / 1000}/>),
      (<a href={Config.serviceEndpoints.assets + report.location} download>Download</a>)
      /* eslint-enable */
    ];
  });

  return (
    <Table tableHeader={headers} tableData={data} />
  );
};

ReportTable.propTypes = {
  reports: PropTypes.array.isRequired
};

export default ReportTable;