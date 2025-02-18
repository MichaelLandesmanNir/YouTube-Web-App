import React, { useEffect } from 'react';
import './Moviescreensidebar.css';
import VideoList from '../moviescreenvideolist/Moviescreenvideolist';

const Sidebar = ({ setSelectedVideo, sidebarVideos }) => {

  // when sidebar videos change, log the videos
  useEffect(() => {
      console.log(sidebarVideos);
  }, [sidebarVideos])
  return (
      <VideoList 
          key={sidebarVideos.length} // Force re-render by changing key
          setSelectedVideo={setSelectedVideo} 
          allVideos={sidebarVideos} 
      />
  );
};

export default Sidebar;