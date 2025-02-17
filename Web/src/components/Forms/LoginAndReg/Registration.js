import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './FormStyles.css';

function Registration() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [profileImage, setProfileImage] = useState(null);
  const [error, setError] = useState({});
  const [darkMode, setDarkMode] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    const errors = {};

    if (!username) errors.username = 'Username is required';
    if (!password) {
      errors.password = 'Password is required';
    } else if (password.length < 8 || !/\d/.test(password) || !/[a-zA-Z]/.test(password)) {
      errors.password = 'Password must be at least 8 characters long and contain both letters and numbers';
    }
    if (!confirmPassword) {
      errors.confirmPassword = 'Confirm password is required';
    } else if (password !== confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
    }
    if (!displayName) errors.displayName = 'Display name is required';
    if (!profileImage) errors.profileImage = 'Profile image is required';

    setError(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validateForm()) return;

    const formData = new FormData();
    formData.append('name', username);
    formData.append('password', password);
    formData.append('profileImage', profileImage);

  

    try {
      const response = await axios.post('http://localhost:5002/api/users', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      console.log(response);
      navigate('/login');
    } catch (error) {
      if (error.response) {
        setError({ form: error.response.data.error || 'Unknown error' });
      } else {
        setError({ form: 'Error connecting to server' });
      }
      console.error('Registration failed:', error);
    }
  };

  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      setProfileImage(file);
    }
  };

  const handleImageClicked = () => {
    navigate('/');
  };
  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
  };



  return (
    <div className={`app-container ${darkMode ? 'dark' : ''}`}>
    <div className={`form-container ${darkMode ? 'dark' : ''}`}>
    <img onClick={handleImageClicked} src={`${process.env.PUBLIC_URL}/youtube-logo.png`} alt="YouTube Logo" />
      <h2>Registration</h2>
      <button onClick={toggleDarkMode}>
      {darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
    </button>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username:</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className={error.username ? 'input-error' : ''}
            placeholder="Username"
          />
          {error.username && <p className="error-message">{error.username}</p>}
        </div>
        <div>
          <label htmlFor="password">Password:</label>
          <small className="password-hint">Password must be at least 8 characters long and contain both letters and numbers.</small>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className={error.password ? 'input-error' : ''}
            placeholder="Password"
          />
          {error.password && <p className="error-message">{error.password}</p>}
        </div>
        <div>
          <label htmlFor="confirmPassword">Confirm Password:</label>
          <input
            type="password"
            id="confirmPassword"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className={error.confirmPassword ? 'input-error' : ''}
            placeholder="Confirm Password"
          />
          {error.confirmPassword && <p className="error-message">{error.confirmPassword}</p>}
        </div>
        <div>
          <label htmlFor="displayName">Display Name:</label>
          <input
            type="text"
            id="displayName"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            className={error.displayName ? 'input-error' : ''}
            placeholder="Display Name"
          />
          {error.displayName && <p className="error-message">{error.displayName}</p>}
        </div>
        <div>
          <label htmlFor="profileImage">Profile Image:</label>
          <input
            type="file"
            id="profileImage"
            accept="image/*"
            onChange={handleImageChange}
            className={error.profileImage ? 'input-error' : ''}
          />
          {error.profileImage && <p className="error-message">{error.profileImage}</p>}
        </div>
        <button type="submit">Register</button>
      </form>
    </div>
    </div>
  );
}

export default Registration;
