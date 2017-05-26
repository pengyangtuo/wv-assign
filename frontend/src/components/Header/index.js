import React, {PropTypes} from 'react';
import style from './Header.scss';

const Header = ({title}) => {
  return (
    <div className="wv-header">
      <small>DASHBOARD</small><br/>
      <h1 className="display-4">{title}</h1>
    </div>
  );
};

Header.propTypes = {
  title: PropTypes.string.isRequired
};

export default Header;
