import React, { useRef, useState } from 'react';
import './VideoItem.css';
import { useNavigate } from 'react-router';

function VideoItem({ video, allVideos }) {

  const [playing, setPlaying] = useState(true);
  const videoRef = useRef(null);

  

  const navigate = useNavigate();

  console.log("vid: ", video.path);


  const stopVideo = () => {
    videoRef.current.pause();
    console.log("video paused");
}


const handleClick = (e) => {
  console.log(video);
  stopVideo();
  navigate(`/moviescreen`, { state: { videoId: video._id, allVideos: allVideos } });
};
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const videoSrc = video.path && video.path.includes("/videos") ? video.path : `http://localhost:5002/${video.path}`;
  
  return (
    <div className="video-item">
      <video ref={videoRef} className="video-player" controls>
      <source src={videoSrc} type="video/mp4" />
      </video>
      <div className="video-details" onClick={handleClick}>
        <h4 className="video-title">{video.title}</h4>
        <p className="video-channel">{video.channel}</p>
        <div className="video-meta">
          <p className="video-views">{video.views} views • {formatDate(video.date)}</p>
        </div>
      </div>
    </div>
  );
}

export default VideoItem;
