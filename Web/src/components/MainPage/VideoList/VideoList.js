import React from 'react';
import VideoItem from '../VideoItem/VideoItem';
import './VideoList.css';

function VideoList({ videos, searchQuery, allVideos }) {


  const filteredVideos = videos.filter((video) =>
      video != null && video.title != null && video.title.toLowerCase().startsWith(searchQuery.toLowerCase())
  );

  return (
    <div className="video-list">
      {filteredVideos.map((video) => (
        <div key={video.id}>
        <VideoItem key={video.id} video={video} allVideos={allVideos} />
        </div>
      ))}
    </div>
  );
}

export default VideoList;
