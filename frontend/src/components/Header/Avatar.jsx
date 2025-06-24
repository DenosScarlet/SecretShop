import React, { useState, useEffect } from 'react';
import styles from './Avatar.module.css';

const Avatar = ({ imageUrl, className }) => {
    const [displaySrc, setDisplaySrc] = useState('/images/avatar.jpg');
    const [isValid, setIsValid] = useState(false);

    useEffect(() => {
        if (!imageUrl) {
            console.log('imageUrl пустой, используется базовый аватар: /images/avatar.jpg');
            setDisplaySrc('/images/avatar.jpg');
            setIsValid(false);
            return;
        }

        const img = new Image();
        img.src = imageUrl;

        img.onload = () => {
            console.log('Изображение успешно загружено:', imageUrl);
            setDisplaySrc(imageUrl);
            setIsValid(true);
        };

        img.onerror = () => {
            console.warn('Не удалось загрузить изображение:', imageUrl);
            setDisplaySrc('/images/avatar.jpg');
            setIsValid(false);
        };

        return () => {
            img.onload = null;
            img.onerror = null;
        };
    }, [imageUrl]);

    return (
        <div className={`${styles.profileButton} ${className}`}>
            <img
                src={displaySrc}
                alt="User Avatar"
                className={styles.profileButtonImage}
            />
        </div>
    );
};

export default Avatar;