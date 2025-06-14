import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from './UserListPage.module.css';
import { keycloakApi } from '../../services/keycloakApi';

const UserListPage = () => {
    const [users, setUsers] = useState([]);
    const [page, setPage] = useState(0);
    const [size, setSize] = useState(20);
    const [totalPages, setTotalPages] = useState(1);

    useEffect(() => {
        fetchUsers();
    }, [page, size]);

    const fetchUsers = async () => {
        try {
            const response = await keycloakApi.getAllDtlUsers(page, size);
            setUsers(response.data.content || response.data);
            setTotalPages(response.data.totalPages || 1);
        } catch (error) {
            console.error('Ошибка загрузки пользователей:', error);
            alert('Ошибка загрузки пользователей');
        }
    };

    const handleDelete = async (userId) => {
        if (window.confirm('Вы уверены, что хотите удалить этого пользователя?')) {
            try {
                await keycloakApi.deleteUser(userId);
                fetchUsers();
            } catch (error) {
                console.error('Ошибка удаления пользователя:', error);
                alert('Ошибка удаления пользователя');
            }
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < totalPages) {
            setPage(newPage);
        }
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <div className={styles.header}>
                    <Link to="/users/create" className={styles.createButton}>
                        Создать нового пользователя
                    </Link>
                </div>

                <div className={styles.userList}>
                    {users.map(user => (
                        <div key={user.userId} className={styles.userItem}>
                            <div className={styles.userInfo}>
                                <h3>
                                    {user.firstName} {user.lastName} {user.middleName || ''}
                                </h3>
                                <p>Email: {user.email || 'Нет данных'}</p>
                                <div className={styles.details}>
                                    <span>ID: {user.userId}</span>
                                    <span>Рабочая группа: {user.workGroup || 'Нет'}</span>
                                    <span>Баланс: {user.balance || 0}</span>
                                    {user.avatar && (
                                        <span>
                                            Аватар:{' '}
                                            <a href={user.avatar} target="_blank" rel="noopener noreferrer">
                                                Просмотреть
                                            </a>
                                        </span>
                                    )}
                                </div>
                            </div>
                            <div className={styles.actions}>
                                <Link to={`/users/edit/${user.userId}`} className={styles.editButton}>
                                    Редактировать
                                </Link>
                                <button
                                    onClick={() => handleDelete(user.userId)}
                                    className={styles.deleteButton}
                                >
                                    Удалить
                                </button>
                            </div>
                        </div>
                    ))}
                </div>

                <div className={styles.pagination}>
                    <button
                        onClick={() => handlePageChange(page - 1)}
                        disabled={page === 0}
                        className={styles.paginationButton}
                    >
                        Предыдущая
                    </button>
                    <span>Страница {page + 1} из {totalPages}</span>
                    <button
                        onClick={() => handlePageChange(page + 1)}
                        disabled={page === totalPages - 1}
                        className={styles.paginationButton}
                    >
                        Следующая
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UserListPage;