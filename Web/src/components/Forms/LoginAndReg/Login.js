import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios'; // Import Axios for making HTTP requests
import './FormStyles.css';

function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState({});
  const [darkMode, setDarkMode] = useState(false);
  const navigate = useNavigate();

  const validateForm = () => {
    const errors = {};

    if (!username) errors.username = 'Username is required';
    if (!password) errors.password = 'Password is required';

    setError(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!validateForm()) return;

    try {
      const response = await axios.post('http://localhost:5002/api/tokens', {
        username,
        password
      });

      // Assuming the backend returns a token upon successful authentication
      const { token, user } = response.data;

      sessionStorage.setItem('token', token);
      sessionStorage.setItem('isAuthenticated', 'true');
      sessionStorage.setItem('name', username);
      sessionStorage.setItem('image', user.imageUrl);

      console.log(response);
      console.log(sessionStorage);

      navigate('/');
    } catch (error) {
      if (error.response) {
        setError({ form: error.response.data.error || 'Unknown error' });
      } else {
        setError({ form: 'Error connecting to server' });
      }
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
        <h2>Login</h2>
        <button onClick={toggleDarkMode}>
        {darkMode ? 'Switch to Light Mode' : 'Switch to Dark Mode'}
      </button>
        <form onSubmit={handleSubmit}>
          <div>
            <label htmlFor="username"></label>
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
            <label htmlFor="password"></label>
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
          {error.form && <p className="error-message">{error.form}</p>}
          <button type="submit">LOGIN</button>
        </form>
        <p>Not registered? <a href="/register">Create an account</a></p>
      </div>
        
    </div>

  );
}

export default Login;
