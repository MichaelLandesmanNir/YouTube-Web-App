import React from 'react';
import './Sidebar.css';

function Sidebar() {
  return (
    <div className="sidebar">
      <button className="sidebar-button">Home</button>
      <button className="sidebar-button">Trending</button>
      <button className="sidebar-button">Subscriptions</button>
      <button className="sidebar-button">Library</button>
    </div>
  );
}

export default Sidebar;
