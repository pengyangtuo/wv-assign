/**
 * Created by ypeng on 2017-03-29.
 */
import React, {PropTypes} from 'react';
import style from './Loading.scss';

const Loading = ({finishMsg, isLoading}) => {

  if(isLoading){
    return (
      <div className="wv-loading">
        <div className="loader" />
        <div className="loading-msg">Loading</div>
      </div>
    );
  }else{
    return (
      <div className="wv-loading">
        {finishMsg()}
      </div>
    );
  }

};

Loading.propTypes = {
  finishMsg: PropTypes.func,
  isLoading: PropTypes.bool.isRequired
};

export default Loading;
