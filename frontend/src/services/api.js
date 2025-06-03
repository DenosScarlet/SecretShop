import axios from 'axios';

// Базовые конфигурации для каждого микросервиса
const shopAxiosInstance  = axios.create({
    baseURL: 'http://localhost:8480',
});

const api  = axios.create({
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
addAuthInterceptor(api);
addRefreshInterceptor(shopAxiosInstance);
addRefreshInterceptor(api);

// Методы для работы с магазином (микросервис 8480)
const shopApi = {
    // Товары
    getItem: (id) => shopAxiosInstance.get(`/shop/item/${id}`),
    getAllItems: () => shopAxiosInstance.get('/shop/items'),
    addItemWithFile: (itemJson, file) => {
        const formData = new FormData();

        // Добавляем JSON как часть формы
        formData.append('item', itemJson);

        // Добавляем файл
        if (file) {
            formData.append('file', file, file.name);
        }

        return shopAxiosInstance.post('/shop/item', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
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
    uploadItemImage: (itemId, file) => {
        const formData = new FormData();

        // Получаем расширение файла
        const extension = file.name.substring(file.name.lastIndexOf('.'));
        // Формируем новое имя файла: id + расширение
        const newFileName = `${itemId}${extension}`;

        // Добавляем файл с новым именем
        formData.append('file', file, newFileName);

        return shopAxiosInstance.post(`/shop/item/${itemId}/upload`, formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    },

    // Удаление изображения товара (обновленная версия)
    deleteItemImage: (itemId) => {
        return shopAxiosInstance.delete(`/shop/item/${itemId}/file`);
    }

};

// Экспортируем основной api и методы для магазина
export { api as default, shopApi };