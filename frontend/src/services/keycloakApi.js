import axios from 'axios';

// API для работы с keycloak admin client микросервисом
const keycloakApiClient = axios.create({
    baseURL: 'http://localhost:8280',
    headers: {
        'Content-Type': 'application/json'
    }
});

// Добавляем интерцептор для добавления токена в заголовки
keycloakApiClient.interceptors.request.use(
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

export const keycloakApi = {
    // ========== KEYCLOAK USER INTEGRATION API ==========

    // Получение данных пользователя по ID (из Keycloak)
    getUserById: (userId) => keycloakApiClient.get(`/api/users/${userId}`),

    // Поиск пользователей по username или email
    searchUsers: (params) => keycloakApiClient.get('/api/users/search', { params }),

    // Получение пользователя по username (нужно добавить в backend)
    getUserByUsername: (username) => keycloakApiClient.get('/api/users/search', {
        params: { username }
    }),

    // Получение всех пользователей (нужно добавить в backend или использовать DTL API)
    getAllUsers: (page = 0, size = 20) => keycloakApiClient.get('/api/dtl/users', {
        params: { page, size }
    }),

    // Создание пользователя (полное создание)
    createUser: (userData) => keycloakApiClient.post('/api/users/full', userData),

    // Обновление пользователя в Keycloak
    updateUser: (userId, userData) => keycloakApiClient.put(`/api/users/${userId}`, userData),

    // Удаление пользователя
    deleteUser: (userId) => keycloakApiClient.delete(`/api/users/${userId}`),

    // Управление ролями пользователя
    getUserRoles: (userId) => keycloakApiClient.get(`/api/users/${userId}/roles`),

    assignRole: (userId, roleName) => keycloakApiClient.post(`/api/users/${userId}/roles`, { roleName }),

    removeRole: (userId, roleName) => keycloakApiClient.delete(`/api/users/${userId}/roles/${roleName}`),

    // Управление паролем
    resetPassword: (userId, newPassword) => keycloakApiClient.put(`/api/users/${userId}/reset-password`, { newPassword }),

    // Управление статусом пользователя
    enableUser: (userId) => keycloakApiClient.put(`/api/users/${userId}/enable`),

    disableUser: (userId) => keycloakApiClient.put(`/api/users/${userId}/disable`),

    // Получение информации об аутентификации
    getAuthInfo: () => keycloakApiClient.get('/api/users/auth-info'),

    // ========== DTL USER API ==========

    // Получение пользователя из DTL
    getDtlUser: (userId) => keycloakApiClient.get(`/api/dtl/users/${userId}`),

    // Получение всех пользователей из DTL с пагинацией
    getAllDtlUsers: (page = 0, size = 20) => keycloakApiClient.get('/api/dtl/users', {
        params: { page, size }
    }),

    // Обновление пользователя в DTL
    updateDtlUser: (userId, updateData) => keycloakApiClient.put(`/api/dtl/users/${userId}`, updateData),

    // Обновление баланса пользователя
    updateUserBalance: (userId, newBalance) => keycloakApiClient.patch(`/api/dtl/users/${userId}/balance`, { newBalance }),

    // Получение и обновление аватара
    getUserAvatar: (userId) => keycloakApiClient.get(`/api/dtl/users/${userId}/avatar`),

    updateUserAvatar: (userId, avatarUrl) => keycloakApiClient.put(`/api/dtl/users/${userId}/avatar`, { avatarUrl }),

    // Получение рабочей группы пользователя
    getUserWorkGroup: (userId) => keycloakApiClient.get(`/api/dtl/users/${userId}/work-group`),

    // ========== УСТАРЕВШИЕ МЕТОДЫ (для обратной совместимости) ==========

    // Получение групп пользователя (заменено на роли)
    getUserGroups: (userId) => keycloakApiClient.get(`/api/users/${userId}/roles`),

    // Добавление пользователя в группу (заменено на назначение роли)
    addUserToGroup: (userId, groupId) => keycloakApiClient.post(`/api/users/${userId}/roles`, { roleName: groupId }),

    // Удаление пользователя из группы (заменено на удаление роли)
    removeUserFromGroup: (userId, groupId) => keycloakApiClient.delete(`/api/users/${userId}/roles/${groupId}`)
};

export default keycloakApiClient;