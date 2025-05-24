import React from 'react';
import styles from './LeftMenu.module.css';

export default function LeftMenu() {
    return (
        <div className={styles.leftMenu}>
            {[...Array(15)].map((_, i) => (
                <div
                    key={i}
                    className={`${styles.menuItem} ${i % 5 === 0 ? styles.fullWidth : ''}`}
                    onClick={() => console.log(`Clicked item ${i + 1}`)}
                />
            ))}
        </div>
    );
}