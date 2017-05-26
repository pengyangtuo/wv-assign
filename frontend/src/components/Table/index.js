/**
 * Created by ypeng on 2017-03-29.
 */
import React, {PropTypes} from 'react';
import TableHeader from './components/TableHeader';
import TableRow from './components/TableRow';

const Table = ({tableHeader, tableData}) => {

  if (tableData.length > 0) {
    return (
      <table className="table">
        <TableHeader headers={tableHeader}/>
        <tbody>
        {
          tableData.map((data, idx) => {
            return (
              <TableRow key={idx} rowId={idx} values={data}/>
            );
          })
        }
        </tbody>
      </table>
    );
  } else {
    const constainerStyle = {
      padding: 0
    };

    return (
      <div className="col" style={constainerStyle}>
        <hr className="my-4"/>
        <h4 className="display-4 text-lightgray">This table is empty</h4>
      </div>
    );
  }

};

Table.propTypes = {
  tableHeader: PropTypes.array.isRequired,
  tableData: PropTypes.array.isRequired
};

export default Table;