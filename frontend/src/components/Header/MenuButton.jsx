import { useState, useRef, useEffect } from 'react';
import styles from './MenuButton.module.css';
import { MenuItem } from './MenuItem';

export default function MenuButton() {
    const [isOpen, setIsOpen] = useState(false);
    const menuRef = useRef(null);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => document.removeEventListener('mousedown', handleClickOutside);
    }, []);

    const menuItems = [
        { title: "Quest 1", progress: "0/3", reward: "+5" },
        { title: "Quest 2", progress: "1/5", reward: "+10" },
        { title: "Special Quest", progress: "2/2", reward: "+20" },
        { title: "Daily Challenge", progress: "3/4", reward: "+15" }
    ];

    return (
        <div className={styles.menuContainer} ref={menuRef}>
            <button
                className={`${styles.questMenuButton} ${isOpen ? styles.active : ''}`}
                onClick={() => setIsOpen(!isOpen)}
                aria-label="Меню каталога"
            >
                <div className={styles.container}>
                    <div className={styles.stateLayer}>
                        <svg
                            className={styles.iconCatalog}
                            width="35"
                            height="35"
                            viewBox="0 0 35 35"
                            fill="none"
                            xmlns="http://www.w3.org/2000/svg"
                        >
                            <path
                                d="M28.3364 2.3292H8.8311C8.2563 2.3292 7.70505 2.55754 7.29861 2.96398C6.89217 3.37042 6.66384 3.92167 6.66384 4.49646V8.83097H4.49658V10.9982H6.66384V16.4164H4.49658V18.5836H6.66384V24.0018H4.49658V26.169H6.66384V30.5036C6.66384 31.0783 6.89217 31.6296 7.29861 32.036C7.70505 32.4425 8.2563 32.6708 8.8311 32.6708H28.3364C28.9112 32.6708 29.4625 32.4425 29.8689 32.036C30.2753 31.6296 30.5037 31.0783 30.5037 30.5036V4.49646C30.5037 3.92167 30.2753 3.37042 29.8689 2.96398C29.4625 2.55754 28.9112 2.3292 28.3364 2.3292ZM28.3364 30.5036H8.8311V26.169H10.9984V24.0018H8.8311V18.5836H10.9984V16.4164H8.8311V10.9982H10.9984V8.83097H8.8311V4.49646H28.3364V30.5036Z"
                                fill="white"
                            />
                            <path d="M15.333 8.83097H24.002V10.9982H15.333V8.83097Z" fill="white"/>
                            <path d="M15.333 16.4164H24.002V18.5836H15.333V16.4164Z" fill="white"/>
                            <path d="M15.333 24.0018H24.002V26.169H15.333V24.0018Z" fill="white"/>
                        </svg>
                    </div>
                </div>
            </button>

            <div className={`${styles.headerMenu} ${isOpen ? styles.menuVisible : ''}`}>
                <div className={styles.menuList}>
                    {menuItems.map((item, index) => (
                        <MenuItem
                            key={index}
                            title={item.title}
                            progress={item.progress}
                            reward={item.reward}
                        />
                    ))}
                </div>
            </div>
        </div>
    );
}