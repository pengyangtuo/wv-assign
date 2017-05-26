/**
 * Created by yangtuopeng on 2017-03-28.
 */
import React, {PropTypes} from 'react';
import {NavLink} from 'react-router-dom';
import style from './Nav.scss';

const Nav = () => {
  return (
    <div className="wv-vertical-nav">

      <ul className="nav flex-column">
        <li className="nav-item">
          <a className="nav-link">
            <h1 className="display-4">W</h1>
          </a>
        </li>
        <li className="nav-item">
          <a className="nav-link">
            <span className="label">DASHBOARD</span>
          </a>
        </li>
        <li className="nav-item">
          <NavLink to="/summary" className="nav-link" activeClassName="active">Summary</NavLink>
        </li>
        <li className="nav-item">
          <NavLink to="/employee" className="nav-link" activeClassName="active">Employee</NavLink>
        </li>
        <li className="nav-item">
          <NavLink to="/report" className="nav-link" activeClassName="active">Report</NavLink>
        </li>
      </ul>
    </div>
  );
};

Nav.propTypes = {};

export default Nav;


