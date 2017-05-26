/**
 * Created by ypeng on 2017-03-29.
 */
import React, {PropTypes}  from 'react';
import style from './Upload.scss';

const Upload = ({uploadHandler}) => {

  function resetInput(target) {
    target.value = '';
  }

  function handleFile(event) {
    const target = event.target;
    const file = target.files[0];

    resetInput(target);

    uploadHandler(file);
  }

  return (
    <form encType="multipart/form-data" className="wv-inputfile">
      <input onChange={handleFile} type="file" name="report" id="report"/>
      <label htmlFor="report" className="btn btn-secondary">Upload</label>
    </form>
  );
};

Upload.propTypes = {
  uploadHandler: PropTypes.func.isRequired
};

export default Upload;