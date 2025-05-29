import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8380/api',
    headers: {
        'Content-Type': 'application/json'
    }
});

// Добавляем интерцептор для автоматического добавления токена авторизации
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('keycloak-token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Интерцептор для обработки ошибок авторизации
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            // Если токен истек, попробуем обновить его
            const keycloak = window.keycloak;
            if (keycloak) {
                keycloak.updateToken(30).then(() => {
                    localStorage.setItem('keycloak-token', keycloak.token);
                    // Повторяем запрос с новым токеном
                    return api.request(error.config);
                }).catch(() => {
                    keycloak.login();
                });
            }
        }
        return Promise.reject(error);
    }
);

export default api;