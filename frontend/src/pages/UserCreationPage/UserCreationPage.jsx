import React, { useState } from 'react';
import styles from './UserCreationPage.module.css';
import { keycloakApi } from '../../services/keycloakApi';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const UserCreationPage = () => {
    const { isAuthenticated } = useAuth();
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        middleName: '',
        password: '',
        avatar: '',
        workGroup: '',
        balance: 0,
        group: '',
        enabled: true
    });
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();

    const validGroups = ['adminGroup', 'managerGroup', 'userGroup'];

    const validateForm = () => {
        const newErrors = {};
        if (!formData.username) newErrors.username = 'Логин обязателен';
        if (!formData.firstName) newErrors.firstName = 'Имя обязательно';
        if (!formData.lastName) newErrors.lastName = 'Фамилия обязательна';
        if (!formData.password) newErrors.password = 'Пароль обязателен';
        if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.email = 'Некорректный email';
        }
        if (formData.group && !validGroups.includes(formData.group)) {
            newErrors.group = 'Некорректная группа';
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const token = localStorage.getItem('keycloak-token');
        if (!token || !isAuthenticated) {
            alert('Необходимо войти в систему');
            return;
        }
        if (!validateForm()) {
            alert('Пожалуйста, исправьте ошибки в форме');
            return;
        }
        try {
            const userData = {
                username: formData.username,
                email: formData.email || undefined,
                firstName: formData.firstName,
                lastName: formData.lastName,
                middleName: formData.middleName || undefined,
                password: formData.password,
                avatar: formData.avatar || undefined,
                workGroup: formData.workGroup || undefined,
                balance: parseInt(formData.balance) || 0,
                enabled: formData.enabled
                // Если сервер поддержит группы, раскомментировать:
                // group: formData.group || undefined
            };
            console.log('Отправка данных пользователя:', userData);
            const response = await keycloakApi.createUser(userData);
            console.log('Пользователь создан:', response.data);

            // Назначение группы, если выбрана
            if (formData.group) {
                try {
                    console.log(`Назначение группы ${formData.group} пользователю ${response.data.userId}`);
                    await keycloakApi.assignGroup(response.data.userId, formData.group);
                    console.log(`Группа ${formData.group} успешно назначена`);
                } catch (groupError) {
                    console.error(`Ошибка назначения группы ${formData.group}:`, groupError);
                    alert(`Пользователь создан, но не удалось назначить группу: ${groupError.response?.data?.message || groupError.message}`);
                }
            } else {
                console.log('Группа не выбрана, пропускаем назначение');
            }

            alert('Пользователь успешно создан!');
            navigate('/users');
        } catch (error) {
            console.error('Ошибка создания пользователя:', error);
            console.error('Детали ошибки:', error.response?.data);
            alert(`Ошибка создания пользователя: ${error.response?.data?.message || error.message}`);
        }
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
        setErrors((prev) => ({ ...prev, [name]: undefined }));
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <h2>Создание нового пользователя</h2>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label>Логин</label>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            className={styles.input}
                        />
                        {errors.username && <span className={styles.error}>{errors.username}</span>}
                    </div>
                    <div className={styles.formGroup}>
                        <label>Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            className={styles.input}
                        />
                        {errors.email && <span className={styles.error}>{errors.email}</span>}
                    </div>
                    <div className={styles.formGroup}>
                        <label>Имя</label>
                        <input
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                            className={styles.input}
                        />
                        {errors.firstName && <span className={styles.error}>{errors.firstName}</span>}
                    </div>
                    <div className={styles.formGroup}>
                        <label>Фамилия</label>
                        <input
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                            className={styles.input}
                        />
                        {errors.lastName && <span className={styles.error}>{errors.lastName}</span>}
                    </div>
                    <div className={styles.formGroup}>
                        <label>Отчество</label>
                        <input
                            type="text"
                            name="middleName"
                            value={formData.middleName}
                            onChange={handleChange}
                            className={styles.input}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label>Пароль</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            className={styles.input}
                        />
                        {errors.password && <span className={styles.error}>{errors.password}</span>}
                    </div>
                    <div className={styles.formGroup}>
                        <label>URL аватара</label>
                        <input
                            type="text"
                            name="avatar"
                            value={formData.avatar}
                            onChange={handleChange}
                            className={styles.input}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label>Рабочая группа</label>
                        <select
                            name="workGroup"
                            value={formData.workGroup}
                            onChange={handleChange}
                            className={styles.input}
                        >
                            <option value="">Выберите группу</option>
                            <option value="DEVELOPMENT">Development</option>
                            <option value="MANAGEMENT">Management</option>
                            <option value="SUPPORT">Support</option>
                        </select>
                    </div>
                    <div className={styles.formGroup}>
                        <label>Баланс</label>
                        <input
                            type="number"
                            name="balance"
                            value={formData.balance}
                            onChange={handleChange}
                            min="0"
                            className={styles.input}
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label>Группа</label>
                        <select
                            name="group"
                            value={formData.group}
                            onChange={handleChange}
                            className={styles.input}
                        >
                            <option value="">Выберите группу</option>
                            <option value="adminGroup">Admin Group</option>
                            <option value="managerGroup">Manager Group</option>
                            <option value="userGroup">User Group</option>
                        </select>
                        {errors.group && <span className={styles.error}>{errors.group}</span>}
                    </div>
                    <div className={styles.formGroup}>
                        <label>
                            <input
                                type="checkbox"
                                name="enabled"
                                checked={formData.enabled}
                                onChange={handleChange}
                            />
                            Активен
                        </label>
                    </div>
                    <button type="submit" className={styles.submitButton}>
                        Создать пользователя
                    </button>
                </form>
            </div>
        </div>
    );
};

export default UserCreationPage;