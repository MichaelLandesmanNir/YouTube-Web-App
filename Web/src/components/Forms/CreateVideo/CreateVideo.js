import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import "./CreateVideo.css";
import axios from 'axios';

function CreateVideo() {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [details, setDetails] = useState("");
  const [videoFile, setVideoFile] = useState(null);
  const [imageFile, setImageFile] = useState(null);

  useEffect(() => {
    const isLoggedIn = sessionStorage.getItem('isAuthenticated');
    if (!isLoggedIn) {
      navigate('/login');
    }
  }, [navigate]);

  const author = sessionStorage.getItem('name');

  const handleVideoChange = (e) => {
    const file = e.target.files[0];
    setVideoFile(file);
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    setImageFile(file);
  };

  const handleUpload = async (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append("title", title);
    formData.append("channel", author);
    formData.append("details", details);
    formData.append("path", videoFile);
    formData.append("image", imageFile);
    formData.append("likes", 0);
    formData.append("views", 0);
    formData.append("dislikes", 0);
    formData.append("comments", JSON.stringify([]));
    formData.append("date", new Date().toISOString());

    try {
      const response = await axios.post('http://localhost:5002/api/videos', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      console.log('Response:', response.data);
      alert("Video created successfully");
      navigate("/");
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div className="container-video-form">
      <button className="button"  onClick={() => { navigate("/") }} type="button" >
        Go Back
      </button>
   
      <form className="form" onSubmit={handleUpload}>
   
        <div className="form_front">
        <div className="youtube-image-container">
        <img className="youtube-image" src={`${process.env.PUBLIC_URL}/youtube-logo.png`} alt="YouTube Logo" />
        </div>
          <div className="form_details">Create Video</div>
          <div className="form-group">
            <h3>Title: </h3>
            <input className="input"
              type="text"
              name="title"
              onChange={(e) => setTitle(e.target.value)}
            />
            <h3>Description: </h3>
            <input className="input"
              type="text"
              name="description"
              onChange={(e) => setDetails(e.target.value)}
            />
            <h3>Video: </h3>
            <input className="input-image-video"
              type="file"
              name="video"
              accept="video/*"
              onChange={handleVideoChange}
            />
            <h3>Image: </h3>
            <input className="input-image-video"
              type="file"
              name="image"
              accept="image/*"
              onChange={handleImageChange}
            />
            <button type="submit" className="submit-btn">
              Upload Video
            </button>
          </div>
        </div>
      </form>

      {videoFile && (
        <div>
          <h3>Video Preview:</h3>
          <video controls src={URL.createObjectURL(videoFile)} width="500" />
        </div>
      )}

      {imageFile && (
        <div>
          <h3>Image Preview:</h3>
          <img src={URL.createObjectURL(imageFile)} alt="Preview" width="200" />
        </div>
      )}
    </div>
  );
}

export default CreateVideo;
