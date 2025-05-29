import React, { useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import styles from './LoginPage.module.css';

const LoginPage = () => {
    const { isAuthenticated, login, loading } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (isAuthenticated) {
            navigate('/');
        }
    }, [isAuthenticated, navigate]);

    if (loading) {
        return <div className={styles.loading}>Загрузка...</div>;
    }

    return (
        <div className={styles.container}>
            <div className={styles.loginCard}>
                <h1 className={styles.title}>Добро пожаловать</h1>
                <p className={styles.description}>
                    Войдите в систему для доступа к функциям приложения
                </p>
                <button
                    onClick={login}
                    className={styles.loginButton}
                >
                    Войти через Keycloak
                </button>
            </div>
        </div>
    );
};

export default LoginPage;