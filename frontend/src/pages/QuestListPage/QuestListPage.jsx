import React, {useState, useEffect} from 'react';
import {Link} from 'react-router-dom';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import styles from './QuestListPage.module.css';
import api from '../../services/api';

const QuestListPage = ({collapsed}) => {
    const [quests, setQuests] = useState([]);
    const [editingId, setEditingId] = useState(null);
    const [editedData, setEditedData] = useState({});

    useEffect(() => {
        fetchQuests();
    }, []);

    const fetchQuests = async () => {
        try {
            const response = await api.get('/quest');
            console.log('Raw quest data from API:', response.data); // Отладочная информация

            // Преобразуем строки дат в объекты Date с тщательной проверкой валидности
            const questsWithDates = response.data.map(quest => {
                console.log('Processing quest:', quest.questId, 'startDate:', quest.startDate, 'endDate:', quest.endDate);

                let startDate, endDate;

                try {
                    startDate = quest.startDate ? new Date(quest.startDate) : new Date();
                    if (isNaN(startDate.getTime())) {
                        startDate = new Date();
                    }
                } catch (e) {
                    startDate = new Date();
                }

                try {
                    endDate = quest.endDate ? new Date(quest.endDate) : new Date();
                    if (isNaN(endDate.getTime())) {
                        endDate = new Date();
                    }
                } catch (e) {
                    endDate = new Date();
                }

                return {
                    ...quest,
                    startDate,
                    endDate
                };
            });
            setQuests(questsWithDates);
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

        // Создаем валидные объекты Date с более строгой проверкой
        let startDate, endDate;

        try {
            if (quest.startDate && quest.startDate instanceof Date && !isNaN(quest.startDate.getTime())) {
                startDate = quest.startDate;
            } else if (quest.startDate) {
                startDate = new Date(quest.startDate);
                if (isNaN(startDate.getTime())) {
                    startDate = new Date();
                }
            } else {
                startDate = new Date();
            }
        } catch (e) {
            console.error('Error parsing start date:', e);
            startDate = new Date();
        }

        try {
            if (quest.endDate && quest.endDate instanceof Date && !isNaN(quest.endDate.getTime())) {
                endDate = quest.endDate;
            } else if (quest.endDate) {
                endDate = new Date(quest.endDate);
                if (isNaN(endDate.getTime())) {
                    endDate = new Date();
                }
            } else {
                endDate = new Date();
            }
        } catch (e) {
            console.error('Error parsing end date:', e);
            endDate = new Date();
        }

        setEditedData({
            questTitle: quest.questTitle || '',
            description: quest.description || '',
            stepsToComplete: quest.stepsToComplete || 1,
            frequency: quest.frequency || 'DAILY',
            workGroup: quest.workGroup || 'DEVELOPMENT',
            startDate: startDate,
            endDate: endDate,
            cost: quest.cost || 0
        });
    };

    const handleEditChange = (e) => {
        const {name, value} = e.target;
        setEditedData(prev => ({
            ...prev,
            [name]: name === 'stepsToComplete' || name === 'cost'
                ? parseInt(value, 10) || 0
                : value
        }));
    };

    const handleDateChange = (date, field) => {
        // Убеждаемся, что дата валидна или создаем новую дату
        let validDate;
        try {
            if (date && date instanceof Date && !isNaN(date.getTime())) {
                validDate = date;
            } else if (date) {
                validDate = new Date(date);
                if (isNaN(validDate.getTime())) {
                    validDate = new Date();
                }
            } else {
                validDate = new Date();
            }
        } catch (e) {
            console.error('Error in handleDateChange:', e);
            validDate = new Date();
        }

        setEditedData(prev => ({
            ...prev,
            [field]: validDate
        }));
    };

    const formatDateForBackend = (date) => {
        try {
            // Проверяем, что дата валидна
            if (!date || !(date instanceof Date) || isNaN(date.getTime())) {
                date = new Date();
            }
            return new Date(date.getTime() - date.getTimezoneOffset() * 60000)
                .toISOString()
                .slice(0, 19);
        } catch (e) {
            console.error('Error formatting date for backend:', e);
            return new Date().toISOString().slice(0, 19);
        }
    };

    const saveChanges = async () => {
        try {
            await api.put(`/quest/${editingId}`, {
                ...editedData,
                startDate: formatDateForBackend(editedData.startDate),
                endDate: formatDateForBackend(editedData.endDate)
            });
            setEditingId(null);
            fetchQuests();
            alert('Квест успешно обновлён!');
        } catch (error) {
            console.error('Ошибка обновления:', error);
            alert('Ошибка обновления квеста');
        }
    };

    const formatDisplayDate = (date) => {
        try {
            if (!date) return 'Не указано';

            let validDate;
            if (date instanceof Date && !isNaN(date.getTime())) {
                validDate = date;
            } else {
                validDate = new Date(date);
                if (isNaN(validDate.getTime())) {
                    return 'Неверная дата';
                }
            }

            return validDate.toLocaleString();
        } catch (e) {
            console.error('Error formatting display date:', e);
            return 'Ошибка даты';
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
                                        placeholder="Название квеста"
                                    />
                                    <textarea
                                        name="description"
                                        value={editedData.description}
                                        onChange={handleEditChange}
                                        className={styles.editTextarea}
                                        placeholder="Описание"
                                    />
                                    <input
                                        type="number"
                                        name="stepsToComplete"
                                        value={editedData.stepsToComplete}
                                        onChange={handleEditChange}
                                        className={styles.editInput}
                                        min="1"
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
                                            timeFormat="HH:mm"
                                            timeIntervals={15}
                                            dateFormat="dd.MM.yyyy HH:mm"
                                            className={styles.editInput}
                                            placeholderText="Выберите дату начала"
                                        />
                                    </div>

                                    <div className={styles.datePickerContainer}>
                                        <DatePicker
                                            selected={editedData.endDate}
                                            onChange={(date) => handleDateChange(date, 'endDate')}
                                            showTimeSelect
                                            timeFormat="HH:mm"
                                            timeIntervals={15}
                                            dateFormat="dd.MM.yyyy HH:mm"
                                            className={styles.editInput}
                                            placeholderText="Выберите дату окончания"
                                        />
                                    </div>
                                    <input
                                        type="number"
                                        name="cost"
                                        value={editedData.cost}
                                        onChange={handleEditChange}
                                        className={styles.editInput}
                                        min="0"
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
                                        <span>Начало: {formatDisplayDate(quest.startDate)}</span>
                                        <span>Конец: {formatDisplayDate(quest.endDate)}</span>
                                    </div>
                                </div>
                            )}

                            <div className={styles.actions}>
                                {editingId === quest.questId ? (
                                    <>
                                        <button
                                            onClick={saveChanges}
                                            className={styles.saveButton}
                                        >
                                            Сохранить
                                        </button>
                                        <button
                                            onClick={() => setEditingId(null)}
                                            className={styles.cancelButton}
                                        >
                                            Отменить
                                        </button>
                                    </>
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
    {value: 'DAILY', label: 'Ежедневно'},
    {value: 'WEEKLY', label: 'Еженедельно'},
    {value: 'MONTHLY', label: 'Ежемесячно'},
    {value: 'ANNUAL', label: 'Ежегодно'},
    {value: 'ONCE', label: 'Однократно'}
];

const workGroupOptions = [
    {value: 'DEVELOPMENT', label: 'Разработка'},
    {value: 'DATA_PROCESSING', label: 'Обработка данных'},
    {value: 'MANAGEMENT', label: 'Управление'}
];

export default QuestListPage;