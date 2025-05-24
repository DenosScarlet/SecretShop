import React, { useState } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import styles from './QuestCreationForm.module.css';
import api from '../../services/api';

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

export default function QuestCreationForm() {
    const [formData, setFormData] = useState({
        questTitle: '',
        description: '',
        stepsToComplete: 1,
        frequency: 'DAILY',
        workGroup: 'DEVELOPMENT',
        startDate: new Date(),
        endDate: new Date(),
        cost: 0
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post('/quest', {
                ...formData,
                startDate: formData.startDate.toISOString(),
                endDate: formData.endDate.toISOString()
            });
            alert('Квест успешно создан!');
        } catch (error) {
            console.error('Ошибка создания:', error);
            alert('Ошибка при создании квеста');
        }
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.formGroup}>
                <label className={styles.label}>Название квеста</label>
                <input
                    className={styles.input}
                    type="text"
                    required
                    value={formData.questTitle}
                    onChange={(e) => setFormData({...formData, questTitle: e.target.value})}
                    placeholder="Введите значение"
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Описание</label>
                <textarea
                    className={`${styles.input} ${styles.textarea}`}
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    placeholder="Введите значение"
                    rows="3"
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Количество шагов для завершения</label>
                <input
                    className={styles.input}
                    type="number"
                    min="1"
                    value={formData.stepsToComplete}
                    onChange={(e) => setFormData({...formData, stepsToComplete: parseInt(e.target.value, 10)})}
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Частота</label>
                <select
                    className={styles.select}
                    value={formData.frequency}
                    onChange={(e) => setFormData({...formData, frequency: e.target.value})}
                >
                    {frequencyOptions.map(option => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Рабочая группа</label>
                <select
                    className={styles.select}
                    value={formData.workGroup}
                    onChange={(e) => setFormData({...formData, workGroup: e.target.value})}
                >
                    {workGroupOptions.map(option => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Дата начала</label>
                <div className={styles.datepickerWrapper}>
                    <svg
                        className={styles.calendarIcon}
                        width="20"
                        height="20"
                        viewBox="0 0 20 20"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                    >
                        <path
                            d="M6.5 1.5V3.5M13.5 1.5V3.5M2.5 7.09V15.5C2.5 16.6046 3.39543 17.5 4.5 17.5H15.5C16.6046 17.5 17.5 16.6046 17.5 15.5V7.09H2.5ZM2.5 7.09V5.5C2.5 4.39543 3.39543 3.5 4.5 3.5H15.5C16.6046 3.5 17.5 4.39543 17.5 5.5V7.09H2.5Z"
                            stroke="#1E1E1E"
                            strokeWidth="1.2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        />
                    </svg>
                    <DatePicker
                        selected={formData.startDate}
                        onChange={(date) => setFormData({...formData, startDate: date})}
                        showTimeSelect
                        timeFormat="HH:mm"
                        timeIntervals={15}
                        dateFormat="dd.MM.yyyy HH:mm"
                        placeholderText="Выберите дату и время"
                        className={styles.input}
                    />
                </div>
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Дата конца</label>
                <div className={styles.datepickerWrapper}>
                    <svg
                        className={styles.calendarIcon}
                        width="20"
                        height="20"
                        viewBox="0 0 20 20"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                    >
                        <path
                            d="M6.5 1.5V3.5M13.5 1.5V3.5M2.5 7.09V15.5C2.5 16.6046 3.39543 17.5 4.5 17.5H15.5C16.6046 17.5 17.5 16.6046 17.5 15.5V7.09H2.5ZM2.5 7.09V5.5C2.5 4.39543 3.39543 3.5 4.5 3.5H15.5C16.6046 3.5 17.5 4.39543 17.5 5.5V7.09H2.5Z"
                            stroke="#1E1E1E"
                            strokeWidth="1.2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                        />
                    </svg>
                    <DatePicker
                        selected={formData.endDate}
                        onChange={(date) => setFormData({...formData, endDate: date})}
                        showTimeSelect
                        timeFormat="HH:mm"
                        timeIntervals={15}
                        dateFormat="dd.MM.yyyy HH:mm"
                        placeholderText="Выберите дату и время"
                        className={styles.input}
                    />
                </div>
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Стоимость</label>
                <input
                    className={styles.input}
                    type="number"
                    value={formData.cost}
                    onChange={(e) => setFormData({...formData, cost: parseInt(e.target.value, 10)})}
                />
            </div>

            <button type="submit" className={styles.submitButton}>
                Создать квест
            </button>
        </form>
    );
}