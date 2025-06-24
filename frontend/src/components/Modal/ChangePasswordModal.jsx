import React, { useState } from 'react';
import Modal from './Modal';
import styles from './Modal.module.css';

const ChangePasswordModal = ({ isOpen, onClose, onChangePassword }) => {
    const [passwordForm, setPasswordForm] = useState({
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
    });
    const [error, setError] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPasswordForm(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = () => {
        if (passwordForm.newPassword !== passwordForm.confirmPassword) {
            setError('Новые пароли не совпадают');
            return;
        }

        if (passwordForm.newPassword.length < 6) {
            setError('Пароль должен быть не менее 6 символов');
            return;
        }

        onChangePassword(passwordForm);
        onClose();
    };

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title="Смена пароля"
            type="confirm"
        >
            <div className={styles.modalBody}>
                {error && <div className={styles.errorMessage}>{error}</div>}

                <div className={styles.formGroup}>
                    <label className={styles.inputLabel}>Текущий пароль</label>
                    <input
                        type="password"
                        name="currentPassword"
                        value={passwordForm.currentPassword}
                        onChange={handleChange}
                        className={styles.inputField}
                        autoComplete="current-password"
                    />
                </div>

                <div className={styles.formGroup}>
                    <label className={styles.inputLabel}>Новый пароль</label>
                    <input
                        type="password"
                        name="newPassword"
                        value={passwordForm.newPassword}
                        onChange={handleChange}
                        className={styles.inputField}
                        autoComplete="new-password"
                    />
                </div>

                <div className={styles.formGroup}>
                    <label className={styles.inputLabel}>Подтвердите пароль</label>
                    <input
                        type="password"
                        name="confirmPassword"
                        value={passwordForm.confirmPassword}
                        onChange={handleChange}
                        className={styles.inputField}
                        autoComplete="new-password"
                    />
                </div>

                <div className={styles.modalActions}>
                    <button
                        onClick={onClose}
                        className={`${styles.modalButton} ${styles.modalButtonSecondary}`}
                    >
                        Отмена
                    </button>
                    <button
                        onClick={handleSubmit}
                        className={`${styles.modalButton} ${styles.modalButtonPrimary}`}
                    >
                        Изменить пароль
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default ChangePasswordModal;