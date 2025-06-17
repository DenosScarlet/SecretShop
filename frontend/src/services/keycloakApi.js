import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8280';

const keycloakApiClient = axios.create({
    baseURL: API_BASE_URL,
});

keycloakApiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('keycloak-token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

export const keycloakApi = {
    getUserById: (userId) => keycloakApiClient.get(`/api/users/${userId}`),
    getUserByUsername: (username) => keycloakApiClient.get(`/api/users/username/${username}`),
    getAllUsers: (first = 0, max = 20) => keycloakApiClient.get(`/api/users?first=${first}&max=${max}`),
    createUser: (userData) => keycloakApiClient.post(`/api/users/full`, userData),
    updateUser: (userId, userData) => keycloakApiClient.put(`/api/users/${userId}`, userData),
    deleteUser: (userId) => keycloakApiClient.delete(`/api/users/${userId}`),
    assignRole: (userId, roleName) => keycloakApiClient.post(`/api/users/${userId}/roles`, { roleName }),
    removeRole: (userId, roleName) => keycloakApiClient.delete(`/api/users/${userId}/roles/${roleName}`),
    getUserRoles: (userId) => keycloakApiClient.get(`/api/users/${userId}/roles`),
    getUserGroups: (userId) => keycloakApiClient.get(`/api/users/${userId}/groups`),
    assignGroup: (userId, groupName) => keycloakApiClient.post(`/api/users/${userId}/groups`, { groupName }),
    removeGroup: (userId, groupName) => keycloakApiClient.delete(`/api/users/${userId}/groups/${groupName}`),
    getDtlUser: (userId) => keycloakApiClient.get(`/api/dtl/users/${userId}`),
    updateDtlUser: (userId, updateData) => keycloakApiClient.put(`/api/dtl/users/${userId}`, updateData),
    getAllDtlUsers: (page = 0, size = 20) => keycloakApiClient.get(`/api/dtl/users?page=${page}&size=${size}`),
    updateUserBalance: (userId, newBalance) => keycloakApiClient.put(`/api/dtl/users/${userId}/balance`, { newBalance }),
    updateUserAvatar: (userId, avatarUrl) => keycloakApiClient.put(`/api/dtl/users/${userId}/avatar`, { avatar: avatarUrl }),
    uploadUserAvatar: (userId, file) => {
        const formData = new FormData();
        formData.append('file', file);

        return keycloakApiClient.post(
            `/api/dtl/users/${userId}/avatar/upload`,
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }
        );
    },
    downloadUserAvatar: (fileName, bucketName = 'secretshop') => keycloakApiClient.get(`/api/dtl/users/download?fileName=${fileName}&bucketName=${bucketName}`, {
        responseType: 'blob'
    })
};