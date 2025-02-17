import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './Header.css';

function Header({ isAuthenticated, onSearch }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [showModal, setShowModal] = useState(false);
  const [userData, setUserData] = useState(null); // State to store user data

  const img = sessionStorage.getItem('image');
  console.log(img)

  const url = `http://localhost:5002/${img}`;

  // Function to fetch user data from server based on token
  const fetchUserData = async () => {
    try {
      const token = sessionStorage.getItem('token');

      if (!token) {
        throw new Error('No token found');
      }

     

      setUserData(sessionStorage.getItem('name'));
      
      console.log(JSON.parse(userData))
    } catch (error) {
      console.error('Error fetching user data:', error);
    }
  };

  useEffect(() => {
    fetchUserData(); // Fetch user data on component mount
  }, []);

  const handleLoginClick = () => navigate('/login');
  const handleLogoutClick = () => {
    sessionStorage.removeItem('token');
    navigate('/login');
  };

  const handleSearchChange = event => onSearch(event.target.value);

  const { newVideos } = location.state || [];

  const handleCreateVideoClick = () => navigate('/createvideo', { state: { newVideos } });

  const toggleModal = () => setShowModal(!showModal);

  return (
    
    <div className="header">
    <img src={`${process.env.PUBLIC_URL}/youtube-logo.png`} alt="YouTube Logo" className="youtube-logo" />
     
 

      <div className="search">
        <input
          type="text"
          placeholder="Search . . ."
          onChange={handleSearchChange}
          required
        />
      </div>

      <button className="create-video-btn" onClick={toggleModal}>
      Profile
  </button>

     
      <button className="create-video-btn" onClick={handleCreateVideoClick}>
        Create
      </button>
      {isAuthenticated ? (
        <button className="create-video-btn" onClick={handleLogoutClick}>Logout</button>
      ) : (
        <button className="create-video-btn" onClick={handleLoginClick}>Login</button>
      )}

      {showModal && userData && (
        <div className="modal">
          <div className="modal-content">
            <span className="close" onClick={toggleModal}>&times;</span>
            <p className='username'>Username: {userData}</p>
            <img src={url} alt="Profile Image" className="medium-image" />
          </div>
        </div>
      )}
    </div>
  );

}

export default Header;
