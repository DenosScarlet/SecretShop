import api from './api';

export const questApi = {
    getUserQuests: (userId) => api.get(`/quest/user?userId=${userId}`),

    getQuestById: (questId) => api.get(`/quest/${questId}`),

    updateQuestSteps: (data) => api.patch('/quest/steps/update', data),

    getQuestSteps: (userId, questId) => api.get(`/quest/steps?userId=${userId}&questId=${questId}`),
};