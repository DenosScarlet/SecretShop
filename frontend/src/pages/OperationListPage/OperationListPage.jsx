import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';
import Modal from '../../components/Modal/Modal';
import ConfirmModal from '../../components/Modal/ConfirmModal';
import modalStyles from '../../components/Modal/Modal.module.css';

const statusOptions = [
    { value: 'PENDING', label: 'Ожидает выдачи' },
    { value: 'COMPLETED', label: 'Выдано' },
    { value: 'CANCELLED', label: 'Отменено' },
];

const OperationListPage = () => {
    const [operations, setOperations] = useState([]);
    const [filteredOperations, setFilteredOperations] = useState([]);
    const [filter, setFilter] = useState('');
    const [selectedStatus, setSelectedStatus] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const [modalOpen, setModalOpen] = useState(false);
    const [modalTitle, setModalTitle] = useState('');
    const [modalMessage, setModalMessage] = useState('');
    const [modalType, setModalType] = useState('info');
    const [confirmModalOpen, setConfirmModalOpen] = useState(false);
    const [operationToUpdate, setOperationToUpdate] = useState({ id: null, status: '' });

    useEffect(() => {
        fetchOperations();
    }, []);

    useEffect(() => {
        applyFilters();
    }, [filter, selectedStatus, operations]);

    const fetchOperations = async () => {
        setIsLoading(true);
        try {
            const response = await shopApi.getAllOperations();
            setOperations(response.data);
        } catch (error) {
            console.error('Ошибка при загрузке операций:', error);
            showModal('Ошибка', 'Ошибка загрузки операций', 'error');
        } finally {
            setIsLoading(false);
        }
    };

    const applyFilters = () => {
        const lowerFilter = filter.trim().toLowerCase();
        const filtered = operations.filter(operation => {
            const matchesText =
                operation.itemName?.toLowerCase().includes(lowerFilter) ||
                operation.username?.toLowerCase().includes(lowerFilter);

            const matchesStatus = selectedStatus ? operation.status === selectedStatus : true;

            return matchesText && matchesStatus;
        });
        setFilteredOperations(filtered);
    };

    const showModal = (title, message, type = 'info') => {
        setModalTitle(title);
        setModalMessage(message);
        setModalType(type);
        setModalOpen(true);
    };

    const handleStatusChange = (operationId, newStatus) => {
        setOperationToUpdate({ id: operationId, status: newStatus });
        setConfirmModalOpen(true);
    };

    const confirmStatusChange = async () => {
        try {
            await shopApi.updateOperation(operationToUpdate.id, { status: operationToUpdate.status });
            fetchOperations();
            showModal('Успех', 'Статус операции успешно обновлён!', 'success');
        } catch (error) {
            console.error('Ошибка обновления статуса:', error);
            showModal('Ошибка', `Ошибка обновления статуса: ${error.response?.data || error.message}`, 'error');
        }
    };

    const getStatusLabel = (statusValue) => {
        const status = statusOptions.find(s => s.value === statusValue);
        return status ? status.label : statusValue;
    };

    const getStatusStyle = (status) => {
        switch (status) {
            case 'PENDING':
                return { backgroundColor: '#ffeb3b', color: '#333' };
            case 'COMPLETED':
                return { backgroundColor: '#4caf50', color: '#fff' };
            case 'CANCELLED':
                return { backgroundColor: '#f44336', color: '#fff' };
            default:
                return {};
        }
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <div className={styles.header} style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '1.5rem',
                    paddingTop: '1rem'
                }}>
                    <Link to="/shop/items" className={styles.createButton}>
                        &larr; Назад к товарам
                    </Link>
                    <h2>Управление операциями покупок</h2>
                </div>

                <div className={styles.filterContainer} style={{
                    marginBottom: '1rem',
                    display: 'flex',
                    flexWrap: 'wrap',
                    gap: '1rem',
                    alignItems: 'center'
                }}>
                    <input
                        type="text"
                        placeholder="Поиск по товару или пользователю"
                        value={filter}
                        onChange={(e) => setFilter(e.target.value)}
                        className={styles.editInput}
                        style={{ flexGrow: 1, minWidth: '200px' }}
                    />

                    <select
                        value={selectedStatus}
                        onChange={(e) => setSelectedStatus(e.target.value)}
                        className={styles.editSelect}
                        style={{ minWidth: '180px' }}
                    >
                        <option value="">Все статусы</option>
                        {statusOptions.map(option => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                    </select>
                </div>

                {isLoading ? (
                    <div>Загрузка операций...</div>
                ) : (
                    <div className={styles.questList}>
                        {filteredOperations.length === 0 ? (
                            <div>Нет операций, соответствующих фильтру.</div>
                        ) : (
                            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                                <thead>
                                <tr style={{ backgroundColor: '#f5f5f5' }}>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>ID</th>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>Товар</th>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>Пользователь</th>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>Дата</th>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>Сумма</th>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>Статус</th>
                                    <th style={{ padding: '12px', textAlign: 'left' }}>Действия</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredOperations.map(operation => (
                                    <tr key={operation.operationId} style={{ borderBottom: '1px solid #eee' }}>
                                        <td style={{ padding: '12px' }}>{operation.operationId}</td>
                                        <td style={{ padding: '12px' }}>
                                            {operation.itemName}
                                            <div style={{ fontSize: '0.9em', color: '#666' }}>
                                                ID: {operation.itemId}
                                            </div>
                                        </td>
                                        <td style={{ padding: '12px' }}>
                                            {operation.username}
                                            <div style={{ fontSize: '0.9em', color: '#666' }}>
                                                ID: {operation.userId}
                                            </div>
                                        </td>
                                        <td style={{ padding: '12px' }}>
                                            {new Date(operation.purchaseDate).toLocaleString()}
                                        </td>
                                        <td style={{ padding: '12px' }}>
                                            {operation.amount}
                                        </td>
                                        <td style={{ padding: '12px' }}>
                                            <span
                                                style={{
                                                    padding: '4px 8px',
                                                    borderRadius: '4px',
                                                    ...getStatusStyle(operation.status)
                                                }}
                                            >
                                                {getStatusLabel(operation.status)}
                                            </span>
                                        </td>
                                        <td style={{ padding: '12px' }}>
                                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                                                {operation.status === 'PENDING' && (
                                                    <button
                                                        onClick={() => handleStatusChange(operation.operationId, 'COMPLETED')}
                                                        className={styles.saveButton}
                                                        style={{
                                                            padding: '8px 12px',
                                                            margin: '0.25rem 0'
                                                        }}
                                                    >
                                                        Подтвердить выдачу
                                                    </button>
                                                )}
                                                {operation.status === 'PENDING' && (
                                                    <button
                                                        onClick={() => handleStatusChange(operation.operationId, 'CANCELLED')}
                                                        className={styles.deleteButton}
                                                        style={{
                                                            padding: '8px 12px',
                                                            margin: '0.25rem 0'
                                                        }}
                                                    >
                                                        Отменить
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                )}
            </div>

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