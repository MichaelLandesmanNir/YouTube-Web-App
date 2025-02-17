import React, { useEffect, useState } from 'react';
import './HomePage.css';
import Header from '../Header/Header.js';
import Sidebar from '../SideBar/SideBar.js';
import VideoList from '../VideoList/VideoList.js';
import { useLocation } from 'react-router';
import { useTheme } from '../../../ThemeContext.jsx';
import axios from 'axios';
function HomePage() {



  const [searchQuery, setSearchQuery] = useState('');
  const [darkMode, setDarkMode] = useState(false);
  const [vids, setVids] = useState([]);

  const { theme, toggleTheme } = useTheme();


  const [videos, setVideos] = useState([]);

  useEffect(() => {
    // Fetch videos from the API when the component mounts
    const fetchVideos = async () => {
      try {
        const response = await axios.get('http://localhost:5002/api/videos');

        setVideos(response.data);
        console.log(response);
      } catch (error) {
        console.error('Error fetching videos:', error);
      }
    };

    fetchVideos();
  }, []); 

  
  const location = useLocation();
  let newVideos = [];
  if (location && location.state) {
    if(location.state.newVideos) {
      newVideos = location.state.newVideos;
    } 
    
  }
  const allVideos = [...videos, ...newVideos];


  const handleSearch = (query) => {
    setSearchQuery(query);
  };

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
  };
  
  // Apply dark mode class to the body or top-level div
  useEffect(() => {
    document.body.className = darkMode ? 'dark-mode' : '';
  }, [darkMode]);




  
  const isAuthenticated = sessionStorage.getItem('isAuthenticated');
  

  if(location && location.state && location.state.deletedVideo) {
    allVideos.splice(location.state.deletedVideo - 1, 1);
    console.log('got deleted video');
  }

  return (
    <div className={`HomePage ${darkMode ? 'dark-mode' : ''}`}>
      <Header isAuthenticated={isAuthenticated} onSearch={handleSearch}/>
      <div className="main">
        <Sidebar />
        <div className="video-list-container">
          <VideoList videos={allVideos} searchQuery={searchQuery} allVideos={allVideos}/>
        </div>
        <button onClick={toggleDarkMode} className="toggle-dark-mode">
          Toggle Dark Mode
        </button>

      
      </div>
    </div>
  );
}

export default HomePage;
