import React from 'react';
import Header from './components/Header'; // Import component Header
import MainContent from './components/MainContent';
import './App.css'; // Style toàn cục và nền


const App = () => {
  return (
    <div className="app-container">
      {/* 1. Sử dụng component Header */}
      <Header />
      <MainContent />
    </div>
  );
};

export default App;
