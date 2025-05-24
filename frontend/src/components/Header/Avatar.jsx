import React from 'react';
import styles from './Avatar.module.css';

export default function Avatar({ imageUrl }) {
    return (
        <div className={styles.profileButton}>
            <img
                src={imageUrl || 'public/images/avatar.png'}
                alt="Аватар"
                className={styles.profileButton}
            />
        </div>
    );
}