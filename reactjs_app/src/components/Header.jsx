import React from 'react';
import './Header.css';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faRightToBracket } from '@fortawesome/free-solid-svg-icons';
import logo from '../i.png'; 

const Header = () => {
  return (
    <header className="app-header">
      <div className="logo-container">
            <img src={logo} alt="Logo" className="logo-icon" />
        <span className="logo-text">HACKATHON2025_Moc</span>
      </div>

      <div className="login-container">
        <div className="login-icon-box">
          {/* Icon FontAwesome */}
          <FontAwesomeIcon icon={faRightToBracket} className="login-icon" />
        </div>
        <span className="login-text">Login</span>
      </div>
    </header>
  );
};

export default Header;
