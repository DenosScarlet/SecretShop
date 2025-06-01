import axios from 'axios';

// Базовые конфигурации для каждого микросервиса
const shopAxiosInstance  = axios.create({
    baseURL: 'http://localhost:8480',
    headers: {
        'Content-Type': 'application/json'
    }
});

const questAxiosInstance  = axios.create({
    baseURL: 'http://localhost:8380/api',
    headers: {
        'Content-Type': 'application/json'
    }
});



// Общий интерцептор для добавления токена
const addAuthInterceptor = (instance) => {
    instance.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem('keycloak-token');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error)
    );
};

// Общий интерцептор для обработки ошибок 401
const addRefreshInterceptor = (instance) => {
    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response?.status === 401) {
                const keycloak = window.keycloak;
                if (keycloak) {
                    return keycloak.updateToken(30)
                        .then(() => {
                            localStorage.setItem('keycloak-token', keycloak.token);
                            error.config.headers.Authorization = `Bearer ${keycloak.token}`;
                            return instance.request(error.config);
                        })
                        .catch(() => {
                            keycloak.login();
                            return Promise.reject(error);
                        });
                }
            }
            return Promise.reject(error);
        }
    );
};

// Применяем интерцепторы к обоим инстансам
addAuthInterceptor(shopAxiosInstance);
addAuthInterceptor(questAxiosInstance);
addRefreshInterceptor(shopAxiosInstance);
addRefreshInterceptor(questAxiosInstance);

// Методы для работы с магазином (микросервис 8480)
const shopApi = {
    // Товары
    getItem: (id) => shopAxiosInstance.get(`/shop/item/${id}`),
    getAllItems: () => shopAxiosInstance.get('/shop/items'),
    addItemWithFile: (itemData, file) => {
        const formData = new FormData();
        formData.append('item', new Blob([JSON.stringify(itemData)], { type: 'application/json' }));
        if (file) formData.append('file', file);
        return shopAxiosInstance.post('/shop/item', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    updateItem: (id, itemData) => shopAxiosInstance.put(`/shop/item/${id}`, itemData),
    deleteItem: (id) => shopAxiosInstance.delete(`/shop/item/${id}`),
    searchItems: (params) => shopAxiosInstance.get('/shop/item/search', { params }),

    // Покупки
    purchaseItem: (itemId) => shopAxiosInstance.post(`/shop/purchase/${itemId}`),

    // Операции
    getOperationsByUser: (userId) => shopAxiosInstance.get(`/shop/operations/user/${userId}`),
    getOperationsByItem: (itemId) => shopAxiosInstance.get(`/shop/operations/item/${itemId}`),
    getOperationById: (operationId) => shopAxiosInstance.get(`/shop/operations/${operationId}`),
    getAllOperations: (params) => shopAxiosInstance.get('/shop/operations', { params }),
    updateOperation: (operationId, updateData) => shopAxiosInstance.put(`/shop/operation/${operationId}`, updateData),

    // Файлы товаров
    uploadFile: (itemId, file) => {
        const formData = new FormData();
        formData.append('file', file);
        return shopAxiosInstance.post(`/shop/item/${itemId}/upload`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },
    downloadFile: (itemId, fileName) => shopAxiosInstance.get(`/shop/item/${itemId}/download`, {
        params: { fileName },
        responseType: 'blob'
    }),
    deleteFile: (itemId, fileName) => shopAxiosInstance.delete(`/shop/item/${itemId}/file`, {
        params: { fileName }
    })
};

// Экспортируем основной api и методы для магазина
export { questAxiosInstance as default, shopApi };