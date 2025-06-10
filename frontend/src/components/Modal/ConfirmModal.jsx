import React from 'react';
import Modal from './Modal';
import styles from './Modal.module.css';

const ConfirmModal = ({
                          isOpen,
                          onClose,
                          onConfirm,
                          title = "Подтверждение действия",
                          message = "Вы уверены, что хотите выполнить это действие?",
                          confirmText = "Подтвердить",
                          cancelText = "Отмена"
                      }) => {
    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={title}
            type="confirm"
        >
            <div className={styles.modalBody}>
                <p>{message}</p>
                <div className={styles.modalActions}>
                    <button
                        onClick={onClose}
                        className={`${styles.modalButton} ${styles.modalButtonSecondary}`}
                    >
                        {cancelText}
                    </button>
                    <button
                        onClick={() => {
                            onConfirm();
                            onClose();
                        }}
                        className={`${styles.modalButton} ${styles.modalButtonPrimary}`}
                    >
                        {confirmText}
                    </button>
                </div>
            </div>
        </Modal>
    );
};

export default ConfirmModal;