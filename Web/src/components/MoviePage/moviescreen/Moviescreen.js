import React, { useEffect, useState } from 'react';
import './Moviescreen.css';
import axios from 'axios';
import Sidebar from '../moviescreensidebar/Moviescreensidebar';
import VideoPlayer from '../moviescreenvideoplayer/Moviescreenvideoplayer';
import { useLocation, useNavigate } from 'react-router-dom';

const Moviescreen = () => {
    const [selectedVideo, setSelectedVideo] = useState(null);
    const [sidebarVideos, setSidebarVideos] = useState([]);
  
    const location = useLocation();
    const navigate = useNavigate();
  
    const { videoId, allVideos } = location?.state || [];
  
    console.log("all vids: " + allVideos);
  
    useEffect(() => {
      const fetchVideoDetails = async () => {
        try {
          const response = await axios.get(`http://localhost:5002/api/videos/${videoId}`);
          setSelectedVideo(response.data.video);
        } catch (error) {
          console.error('Error fetching video details:', error);
        }
      };
  
      if (videoId) {
        fetchVideoDetails();
      }
    }, [videoId]);
  
    useEffect(() => {
      setSidebarVideos(allVideos);
    }, [allVideos]);
  
    return (
      <div className='body'>
        <div className="container">
          <main className="main-content">
            <button className="button" onClick={() => { navigate("/") }}> go back </button>
            <section className="video-detail">
              {selectedVideo && (
                <VideoPlayer
                  video={selectedVideo}
                  setSelectedVideo={setSelectedVideo}
                  setSidebarVideos={setSidebarVideos}
                  sidebarVideos={sidebarVideos}
                />
              )}
            </section>
          </main>
          <Sidebar setSelectedVideo={setSelectedVideo} sidebarVideos={sidebarVideos} />
        </div>
      </div>
    );
};

export default Moviescreen;
