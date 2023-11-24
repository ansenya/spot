import React, { useEffect, useState } from 'react';
import { isTemplateHead } from 'typescript';
import menuIcon from "../assets/menu-svgrepo-com (1) 1.svg"
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg"
import NavbarMainContent from './NavbarMainContent';
import CButton from './UI/CButton/CButton';

const Navbar = () => {
    const [isSmallScreen, setIsSmallScreen] = useState<boolean>(window.innerWidth < 576) 
    const [searchBarVisible, setSearchBarVisible] = useState<boolean>(false)

    const handleResize = () => {
      setIsSmallScreen(window.innerWidth < 576)
    } 

    useEffect(() => {
      window.addEventListener("resize", handleResize)
      return () => {
        window.removeEventListener("resize", handleResize)
      }
    }, [])
    return (
        <div className="navbar">
          {
            (searchBarVisible && isSmallScreen)
            ?
            <div></div>
            :
            <NavbarMainContent setSearchBarVisible={setSearchBarVisible} isSmallScreen={isSmallScreen}/> 
          }
        </div>
    );
};

export default Navbar;
