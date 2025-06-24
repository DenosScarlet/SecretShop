import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styles from './UserEditPage.module.css';
import { keycloakApi } from '../../services/keycloakApi';

const UserEditPage = () => {
    const { userId } = useParams();
    const [formData, setFormData] = useState(null);
    const [groups, setGroups] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        fetchUser();
    }, [userId]);

    const fetchUser = async () => {
        try {
            const userResponse = await keycloakApi.getUserById(userId);
            const dtlUserResponse = await keycloakApi.getDtlUser(userId);
            const groupsResponse = await keycloakApi.getUserGroups(userId);
            setFormData({
                email: userResponse.data.email || '',
                firstName: userResponse.data.firstName || '',
                lastName: userResponse.data.lastName || '',
                middleName: dtlUserResponse.data.middleName || '',
                avatar: dtlUserResponse.data.avatar || '',
                workGroup: dtlUserResponse.data.workGroup || '',
                balance: dtlUserResponse.data.balance || 0,
                group: groupsResponse.data[0] || '',
                enabled: userResponse.data.enabled
            });
            setGroups(groupsResponse.data);
        } catch (error) {
            console.error('Ошибка загрузки пользователя:', error);
            alert('Ошибка загрузки пользователя');
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            // Обновление данных в Keycloak
            const userUpdateData = {
                email: formData.email,
                firstName: formData.firstName,
                lastName: formData.lastName,
                middleName: formData.middleName,
                enabled: formData.enabled
            };
            await keycloakApi.updateUser(userId, userUpdateData);

            // Обновление данных в DTL
            const dtlUpdateData = {
                firstName: formData.firstName,
                lastName: formData.lastName,
                middleName: formData.middleName,
                workGroup: formData.workGroup || undefined
            };
            await keycloakApi.updateDtlUser(userId, dtlUpdateData);

            // Обновление аватара
            if (formData.avatar) {
                await keycloakApi.updateUserAvatar(userId, formData.avatar);
            }

            // Обновление баланса
            await keycloakApi.updateUserBalance(userId, formData.balance);

            // Обновление группы
            if (formData.group && formData.group !== groups[0]) {
                if (groups.length > 0) {
                    await keycloakApi.removeGroup(userId, groups[0]);
                }
                await keycloakApi.assignGroup(userId, formData.group);
            }

            alert('Пользователь успешно обновлён!');
            navigate('/users');
        } catch (error) {
            console.error('Ошибка обновления пользователя:', error);
            alert(`Ошибка обновления пользователя: ${error.response?.data?.message || error.message}`);
        }
    };

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    if (!formData) {
        return <div>Загрузка...</div>;
    }

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <h2>Редактирование пользователя</h2>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.formGroup}>
                        <label>Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            className={styles.input}
                        />
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
                        Сохранить изменения
                    </button>
                </form>
            </div>
        </div>
    );
};

export default UserEditPage;