import React, { useState, useEffect } from 'react';
import styles from './LeftMenu.module.css';

export default function LeftMenu() {
    const [isMobile, setIsMobile] = useState(false);
    const [menuOpen, setMenuOpen] = useState(false);

    useEffect(() => {
        const checkMobile = () => {
            setIsMobile(window.innerWidth < 768);
        };

        checkMobile();
        window.addEventListener('resize', checkMobile);

        return () => window.removeEventListener('resize', checkMobile);
    }, []);

    return (
        <>
            {isMobile && (
                <button
                    className={styles.menuToggle}
                    onClick={() => setMenuOpen(!menuOpen)}
                >
                    ☰
                </button>
            )}

            <div className={`${styles.leftMenu} ${isMobile && !menuOpen ? styles.hidden : ''}`}>
                {[...Array(15)].map((_, i) => (
                    <div
                        key={i}
                        className={`${styles.menuItem} ${i % 5 === 0 ? styles.fullWidth : ''}`}
                        onClick={() => console.log(`Clicked item ${i + 1}`)}
                    />
                ))}
            </div>
        </>
    );
}