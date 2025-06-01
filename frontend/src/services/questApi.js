import api from './api';

export const questApi = {
    // Получение квестов текущего пользователя
    getUserQuests: () => api.get('/api/quest/user'),

    // Обновление шагов квеста
    updateQuestSteps: (data) => api.patch('/api/quest/steps/update', data),

    // Получение информации о шагах квеста
    getQuestSteps: (userId, questId) => api.get(`/api/quest/steps?userId=${userId}&questId=${questId}`),
};