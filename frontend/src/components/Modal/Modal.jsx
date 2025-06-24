import React from 'react';
import styles from './Modal.module.css';

const Modal = ({ isOpen, onClose, title, children, type = 'info' }) => {
    if (!isOpen) return null;

    const getTypeStyles = () => {
        switch (type) {
            case 'success':
                return { header: styles.successHeader, icon: '✅' };
            case 'error':
                return { header: styles.errorHeader, icon: '❌' };
            case 'warning':
                return { header: styles.warningHeader, icon: '⚠️' };
            case 'confirm':
                return { header: styles.confirmHeader, icon: '❓' };
            default:
                return { header: styles.infoHeader, icon: 'ℹ️' };
        }
    };

    const { header, icon } = getTypeStyles();

    return (
        <div className={styles.modalOverlay}>
            <div className={styles.modal}>
                <div className={`${styles.modalHeader} ${header}`}>
                    <span className={styles.modalIcon}>{icon}</span>
                    <h3>{title}</h3>
                    <button onClick={onClose} className={styles.closeButton}>
                        &times;
                    </button>
                </div>
                <div className={styles.modalBody}>
                    {children}
                </div>
            </div>
        </div>
    );
};

export default Modal;