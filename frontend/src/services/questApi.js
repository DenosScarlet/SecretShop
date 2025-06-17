import api from './api';

export const questApi = {
    // Получение квестов текущего пользователя
    getUserQuests: (userId) => api.get(`/quest/user?userId=${userId}`),

    getQuestById: (questId) => api.get(`/quest/${questId}`),

    // Обновление шагов квеста
    updateQuestSteps: (data) => api.patch('/quest/steps/update', data),

    // Получение информации о шагах квеста
    getQuestSteps: (userId, questId) => api.get(`/quest/steps?userId=${userId}&questId=${questId}`),
};