import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import styles from './QuestListPage.module.css';
import api from '../../services/api';

const QuestListPage = ({ collapsed }) => {
    const [quests, setQuests] = useState([]);
    const [editingId, setEditingId] = useState(null);
    const [editedData, setEditedData] = useState({});

    useEffect(() => {
        fetchQuests();
    }, []);

    const fetchQuests = async () => {
        try {
            const response = await api.get('/quest');
            setQuests(response.data);
        } catch (error) {
            console.error('Error fetching quests:', error);
            alert('Ошибка загрузки квестов');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Вы уверены, что хотите удалить этот квест?')) {
            try {
                await api.delete(`/quest/${id}`);
                fetchQuests();
            } catch (error) {
                console.error('Error deleting quest:', error);
                alert('Ошибка удаления квеста');
            }
        }
    };

    const startEditing = (quest) => {
        setEditingId(quest.questId);
        setEditedData({
            questTitle: quest.questTitle,
            description: quest.description,
            stepsToComplete: quest.stepsToComplete,
            frequency: quest.frequency,
            workGroup: quest.workGroup,
            startDate: new Date(quest.startDate),
            endDate: new Date(quest.endDate),
            cost: quest.cost
        });
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditedData(prev => ({
            ...prev,
            [name]: name === 'stepsToComplete' || name === 'cost'
                ? parseInt(value, 10)
                : value
        }));
    };

    const handleDateChange = (date, field) => {
        setEditedData(prev => ({
            ...prev,
            [field]: date
        }));
    };

    const saveChanges = async () => {
        try {
            await api.put(`/quest/${editingId}`, {
                ...editedData,
                startDate: editedData.startDate.toISOString(),
                endDate: editedData.endDate.toISOString()
            });
            setEditingId(null);
            fetchQuests();
            alert('Квест успешно обновлён!');
        } catch (error) {
            console.error('Ошибка обновления:', error);
            alert('Ошибка обновления квеста');
        }
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <div className={styles.header}>
                    <Link to="/quest/create" className={styles.createButton}>
                        Создать новый квест
                    </Link>
                </div>

                <div className={styles.questList}>
                    {quests.map(quest => (
                        <div key={quest.questId} className={styles.questItem}>
                            {editingId === quest.questId ? (
                                // Режим редактирования
                                <div className={styles.editForm}>
                                    <input
                                        name="questTitle"
                                        value={editedData.questTitle}
                                        onChange={handleEditChange}
                                        className={styles.editInput}
                                    />
                                    <textarea
                                        name="description"
                                        value={editedData.description}
                                        onChange={handleEditChange}
                                        className={styles.editTextarea}
                                    />
                                    <input
                                        type="number"
                                        name="stepsToComplete"
                                        value={editedData.stepsToComplete}
                                        onChange={handleEditChange}
                                        className={styles.editInput}
                                    />
                                    <select
                                        name="frequency"
                                        value={editedData.frequency}
                                        onChange={handleEditChange}
                                        className={styles.editSelect}
                                    >
                                        {frequencyOptions.map(option => (
                                            <option key={option.value} value={option.value}>
                                                {option.label}
                                            </option>
                                        ))}
                                    </select>
                                    <select
                                        name="workGroup"
                                        value={editedData.workGroup}
                                        onChange={handleEditChange}
                                        className={styles.editSelect}
                                    >
                                        {workGroupOptions.map(option => (
                                            <option key={option.value} value={option.value}>
                                                {option.label}
                                            </option>
                                        ))}
                                    </select>
                                    <div className={styles.datePickerContainer}>
                                        <DatePicker
                                            selected={editedData.startDate}
                                            onChange={(date) => handleDateChange(date, 'startDate')}
                                            showTimeSelect
                                            dateFormat="dd.MM.yyyy HH:mm"
                                            className={styles.editInput}
                                        />
                                    </div>

                                    <div className={styles.datePickerContainer}>
                                        <DatePicker
                                            selected={editedData.endDate}
                                            onChange={(date) => handleDateChange(date, 'endDate')}
                                            showTimeSelect
                                            dateFormat="dd.MM.yyyy HH:mm"
                                            className={styles.editInput}
                                        />
                                    </div>
                                    <input
                                        type="number"
                                        name="cost"
                                        value={editedData.cost}
                                        onChange={handleEditChange}
                                        className={styles.editInput}
                                    />
                                </div>
                            ) : (
                                // Режим просмотра
                                <div className={styles.questInfo}>
                                    <h3>{quest.questTitle}</h3>
                                    <p>{quest.description}</p>
                                    <div className={styles.details}>
                                        <span>Шаги: {quest.stepsToComplete}</span>
                                        <span>Частота: {quest.frequency}</span>
                                        <span>Группа: {quest.workGroup}</span>
                                        <span>Стоимость: {quest.cost}</span>
                                        <span>Начало: {new Date(quest.startDate).toLocaleString()}</span>
                                        <span>Конец: {new Date(quest.endDate).toLocaleString()}</span>
                                    </div>
                                </div>
                            )}

                            <div className={styles.actions}>
                                {editingId === quest.questId ? (
                                    <button
                                        onClick={saveChanges}
                                        className={styles.saveButton}
                                    >
                                        Сохранить
                                    </button>
                                ) : (
                                    <button
                                        onClick={() => startEditing(quest)}
                                        className={styles.editButton}
                                    >
                                        Редактировать
                                    </button>
                                )}
                                <button
                                    onClick={() => handleDelete(quest.questId)}
                                    className={styles.deleteButton}
                                >
                                    Удалить
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

// Добавляем константы из QuestCreationForm
const frequencyOptions = [
    { value: 'DAILY', label: 'Ежедневно' },
    { value: 'WEEKLY', label: 'Еженедельно' },
    { value: 'MONTHLY', label: 'Ежемесячно' },
    { value: 'ANNUAL', label: 'Ежегодно' },
    { value: 'ONCE', label: 'Однократно' }
];

const workGroupOptions = [
    { value: 'DEVELOPMENT', label: 'Разработка' },
    { value: 'DATA_PROCESSING', label: 'Обработка данных' },
    { value: 'MANAGEMENT', label: 'Управление' }
];

export default QuestListPage;