import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import axios from 'axios'; // Import Axios for making HTTP requests
import { ThemeProvider } from './ThemeContext';
import './App.css';
import Registration from './components/Forms/LoginAndReg/Registration.js';
import Login from './components/Forms/LoginAndReg/Login.js';
import HomePage from './components/MainPage/HomePage/HomePage.js';
import Moviescreen from './components/MoviePage/moviescreen/Moviescreen.js';
import CreateVideo from './components/Forms/CreateVideo/CreateVideo.js';

function App() {
  

  return (
    <ThemeProvider>
      <div className="App">
        <BrowserRouter>
          <main>
            <Routes>
              <Route path="/" element={<HomePage  />} />
              <Route path="/register" element={<Registration />} />
              <Route path="/login" element={<Login />} />
              <Route path="/moviescreen" element={<Moviescreen />} />
              <Route path="/createvideo" element={<CreateVideo />} />
            </Routes>
          </main>
         
        </BrowserRouter>
      </div>
    </ThemeProvider>
  );
}

export default App;
