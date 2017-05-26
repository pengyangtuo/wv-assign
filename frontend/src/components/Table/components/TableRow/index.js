import React, {PropTypes} from 'react';

const TableRow = ({values, rowId}) => {
  return (
    <tr>{values.map((value, idx) => {
      return (<td key={rowId+"-"+idx}>{value}</td>);
    })}</tr>
  );
};

TableRow.propTypes = {
  values: PropTypes.array.isRequired,
  rowId: PropTypes.number.isRequired
};

export default TableRow;