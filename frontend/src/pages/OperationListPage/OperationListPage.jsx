import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import styles from './OperationListPage.module.css';
import { shopApi } from '../../services/api';
import Modal from '../../components/Modal/Modal';
import ConfirmModal from '../../components/Modal/ConfirmModal';
import modalStyles from '../../components/Modal/Modal.module.css';

// Исправленные статусы в соответствии с бэкендом
const statusOptions = [
    { value: 'IN_PROGRESS', label: 'Ожидает выдачи', color: '#ffeb3b' },
    { value: 'COMPLETE', label: 'Выдано', color: '#4caf50' }, // Из COMPLETE в COMPLETED
    { value: 'CANCEL', label: 'Отменено', color: '#f44336' }, // Из CANCEL в CANCELED
];

// Сообщения для истории операций
const statusMessages = {
    'IN_PROGRESS': 'Ожидание выдачи',
    'COMPLETE': 'Товар выдан',
    'CANCEL': 'Запрос отменён'
};

const OperationListPage = () => {
    const [operations, setOperations] = useState([]);
    const [filteredOperations, setFilteredOperations] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedStatus, setSelectedStatus] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [modalTitle, setModalTitle] = useState('');
    const [modalMessage, setModalMessage] = useState('');
    const [modalType, setModalType] = useState('info');
    const [confirmModalOpen, setConfirmModalOpen] = useState(false);
    const [operationToUpdate, setOperationToUpdate] = useState({
        id: null,
        status: '',
        message: ''
    });

    // Загрузка операций
    const fetchOperations = useCallback(async () => {
        setIsLoading(true);
        try {
            const response = await shopApi.getAllOperations();
            if (response.data && Array.isArray(response.data)) {
                setOperations(response.data);
            } else {
                throw new Error('Неверный формат данных операций');
            }
        } catch (error) {
            console.error('Ошибка при загрузке операций:', error);
            showModal('Ошибка', 'Не удалось загрузить операции', 'error');
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchOperations();
    }, [fetchOperations]);

    // Фильтрация операций
    useEffect(() => {
        const searchLower = searchTerm ? searchTerm.toLowerCase() : '';

        const filtered = operations.filter(operation => {
            // Проверка статуса
            if (selectedStatus && operation.status !== selectedStatus) {
                return false;
            }

            // Проверка поискового запроса
            if (searchLower) {
                try {
                    const history = JSON.parse(operation.operationHistory || '{}');
                    const itemName = history.itemName?.toLowerCase() || '';
                    const userName = history.userName?.toLowerCase() || '';

                    if (!itemName.includes(searchLower) &&
                        !userName.includes(searchLower)) {
                        return false;
                    }
                } catch (e) {
                    console.error('Ошибка парсинга истории операции:', e);
                }
            }

            return true;
        });

        setFilteredOperations(filtered);
    }, [operations, selectedStatus, searchTerm]);

    // Показ модального окна
    const showModal = useCallback((title, message, type = 'info') => {
        setModalTitle(title);
        setModalMessage(message);
        setModalType(type);
        setModalOpen(true);
    }, []);

    // Обработка изменения статуса
    const handleStatusChange = (operationsId, newStatus) => {
        const message = statusMessages[newStatus] || 'Статус изменен';
        setOperationToUpdate({
            id: operationsId,
            status: newStatus,
            message: message
        });
        setConfirmModalOpen(true);
    };

    // Подтверждение изменения статуса
    const confirmStatusChange = useCallback(async () => {
        try {
            // Проверяем наличие operationsId
            if (!operationToUpdate.id) {
                throw new Error('Не найден идентификатор операции');
            }

            // Находим текущую операцию
            const operation = operations.find(op => op.operationsId === operationToUpdate.id);
            if (!operation) return;

            // Парсим текущую историю
            let history = {};
            try {
                history = JSON.parse(operation.operationHistory || '{}');
            } catch (e) {
                console.error('Ошибка парсинга истории операции:', e);
            }

            // Добавляем новое сообщение
            history.message = operationToUpdate.message;

            // Отправляем обновление на сервер
            await shopApi.updateOperation(operationToUpdate.id, {
                status: operationToUpdate.status,
                operationHistory: JSON.stringify(history)
            });

            // Обновляем локальное состояние
            setOperations(prev => prev.map(op =>
                op.operationsId === operationToUpdate.id
                    ? {
                        ...op,
                        status: operationToUpdate.status,
                        operationHistory: JSON.stringify(history)
                    }
                    : op
            ));

            showModal('Успех', 'Статус операции успешно обновлён', 'success');
        } catch (error) {
            console.error('Ошибка обновления статуса:', error);
            showModal('Ошибка', `Ошибка обновления статуса: ${error.message}`, 'error');
        } finally {
            setConfirmModalOpen(false);
        }
    }, [operationToUpdate, operations, showModal]);

    // Получение информации из истории операции
    const getOperationInfo = (operation) => {
        try {
            const history = JSON.parse(operation.operationHistory || '{}');
            return {
                itemName: history.itemName || 'Неизвестный товар',
                userName: history.userName || 'Неизвестный пользователь',
                cost: history.cost || 0,
                message: history.message || '',
            };
        } catch (e) {
            console.error('Ошибка парсинга истории операции:', e);
            return {
                itemName: 'Ошибка загрузки',
                userName: 'Ошибка загрузки',
                cost: 0,
                message: 'Не удалось загрузить информацию',
            };
        }
    };

    // Получение конфигурации статуса
    const getStatusConfig = (status) => {
        return statusOptions.find(opt => opt.value === status) ||
            { label: status || 'Неизвестно', color: '#ccc' };
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <div className={styles.header}>
                    <div className={styles.headerContent}>
                        <Link to="/shop/items" className={styles.backButton}>
                            ← Назад к товарам
                        </Link>
                        <h2 className={styles.pageTitle}>Операции магазина</h2>
                    </div>
                </div>

                {/* Панель фильтров */}
                <div className={styles.filterPanel}>
                    <input
                        type="text"
                        placeholder="Поиск по товару или пользователю..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className={styles.searchInput}
                    />

                    <select
                        value={selectedStatus}
                        onChange={(e) => setSelectedStatus(e.target.value)}
                        className={styles.statusSelect}
                    >
                        <option value="">Все статусы</option>
                        {statusOptions.map(option => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                </div>

                {/* Статус загрузки */}
                {isLoading ? (
                    <div className={styles.loadingContainer}>
                        <div className={styles.spinner}></div>
                        <p>Загрузка операций...</p>
                    </div>
                ) : (
                    /* Список операций */
                    <div className={styles.operationsContainer}>
                        {filteredOperations.length === 0 ? (
                            <div className={styles.emptyState}>
                                <p>Нет операций для отображения</p>
                                <button
                                    onClick={() => {
                                        setSearchTerm('');
                                        setSelectedStatus('');
                                    }}
                                    className={styles.resetButton}
                                >
                                    Сбросить фильтры
                                </button>
                            </div>
                        ) : (
                            <div className={styles.operationsGrid}>
                                {filteredOperations.map(operation => {
                                    const operationInfo = getOperationInfo(operation);
                                    const statusConfig = getStatusConfig(operation.status);

                                    return (
                                        <div key={operation.operationsId} className={styles.operationCard}>
                                            <div className={styles.operationHeader}>
                                                <span className={styles.operationId}>
                                                    Операция #{operation.operationsId}
                                                </span>
                                                <span
                                                    className={styles.statusBadge}
                                                    style={{ backgroundColor: statusConfig.color }}
                                                >
                                                    {statusConfig.label}
                                                </span>
                                            </div>

                                            <div className={styles.operationBody}>
                                                <div className={styles.operationSection}>
                                                    <label>Пользователь:</label>
                                                    <div className={styles.operationValue}>
                                                        {operationInfo.userName}
                                                    </div>
                                                </div>

                                                <div className={styles.operationSection}>
                                                    <label>Товар:</label>
                                                    <div className={styles.operationValue}>
                                                        {operationInfo.itemName}
                                                    </div>
                                                </div>

                                                <div className={styles.operationSection}>
                                                    <label>Стоимость:</label>
                                                    <div className={styles.operationValue}>
                                                        <div className={styles.coinContainer}>
                                                            <svg
                                                                width="20"
                                                                height="20"
                                                                viewBox="0 0 20 20"
                                                                fill="none"
                                                                className={styles.coinIcon}
                                                            >
                                                                <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 18C5.58 18 2 14.42 2 10C2 5.58 5.58 2 10 2C14.42 2 18 5.58 18 10C18 14.42 14.42 18 10 18Z" fill="#FFD700"/>
                                                                <path d="M10 4C7.79 4 6 5.79 6 8C6 10.21 7.79 12 10 12C12.21 12 14 10.21 14 8C14 5.79 12.21 4 10 4ZM10 10C8.9 10 8 9.1 8 8C8 6.9 8.9 6 10 6C11.1 6 12 6.9 12 8C12 9.1 11.1 10 10 10Z" fill="#FFD700"/>
                                                            </svg>
                                                            <span className={styles.coinAmount}>
                                                                {operationInfo.cost}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div className={styles.operationSection}>
                                                    <label>История:</label>
                                                    <div className={styles.operationValue}>
                                                        {operationInfo.message}
                                                    </div>
                                                </div>
                                            </div>

                                            <div className={styles.operationActions}>
                                                <select
                                                    value={operation.status}
                                                    onChange={(e) => handleStatusChange(operation.operationsId, e.target.value)}
                                                    className={styles.statusSelectAction}
                                                >
                                                    {statusOptions.map(option => (
                                                        <option
                                                            key={option.value}
                                                            value={option.value}
                                                        >
                                                            {option.label}
                                                        </option>
                                                    ))}
                                                </select>
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        )}
                    </div>
                )}
            </div>

            {/* Модальные окна */}
            <Modal
                isOpen={modalOpen}
                onClose={() => setModalOpen(false)}
                title={modalTitle}
                type={modalType}
            >
                <p>{modalMessage}</p>
                <div className={modalStyles.modalActions}>
                    <button
                        onClick={() => setModalOpen(false)}
                        className={`${modalStyles.modalButton} ${modalStyles.modalButtonPrimary}`}
                    >
                        OK
                    </button>
                </div>
            </Modal>

            <ConfirmModal
                isOpen={confirmModalOpen}
                onClose={() => setConfirmModalOpen(false)}
                onConfirm={confirmStatusChange}
                title="Подтверждение изменения статуса"
                message="Вы уверены, что хотите изменить статус операции?"
                confirmText="Изменить"
            />
        </div>
    );
};

export default OperationListPage;