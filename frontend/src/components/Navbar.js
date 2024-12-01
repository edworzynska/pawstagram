import React, { useState } from "react";
import { NavLink } from "react-router-dom";
import { IoClose, IoMenu } from "react-icons/io5";
import { FaPaw } from "react-icons/fa";
import { AiOutlinePlusCircle, AiOutlineHome, AiOutlineLogout } from "react-icons/ai"; 
import './Navbar.css';

const Navbar = () => {
  const [menuOpen, setMenuOpen] = useState(false);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <header className="header">
      <nav className="nav container">
        <NavLink to="/" className="nav__logo">
          <FaPaw /> Pawstagram
        </NavLink>

        <div className={`nav__menu ${menuOpen ? "active" : ""}`}>
          <ul className="nav__list">
            <li className="nav__item">
              <NavLink to="/feed" className="nav__link">
                <FaPaw /> Feed
              </NavLink>
            </li>
            <li className="nav__item">
              <NavLink to="/new-post" className="nav__link">
                <FaPaw /> Create Post
              </NavLink>
            </li>
            <li className="nav__item">
              <NavLink to="/logout" className="nav__link nav__cta">
                <AiOutlineLogout /> Logout
              </NavLink>
            </li>
          </ul>
          <div className="nav__close" onClick={toggleMenu}>
            <IoClose />
          </div>
        </div>

        <div className="nav__toggle" onClick={toggleMenu}>
          <IoMenu />
        </div>
      </nav>
    </header>
  );
};

export default Navbar;
