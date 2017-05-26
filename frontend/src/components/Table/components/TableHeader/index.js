import React, {PropTypes} from 'react';

const TableHeader = ({headers}) => {
  return (
    <thead>
      <tr>
        {headers.map((header, idx) => {
          return (<th key={idx}>{header}</th>);
        })}
      </tr>
    </thead>
  );
};

TableHeader.propTypes = {
  headers: PropTypes.array.isRequired
};

export default TableHeader;


