// Утилиты для работы с квестами
export const filterActiveQuests = (quests) => {
    const now = new Date();
    return quests.filter(quest => {
        const startDate = new Date(quest.startDate);
        const endDate = new Date(quest.endDate);
        const isActive = now >= startDate && now <= endDate;

        if (quest.questStatus === 'COMPLETE') {
            const completedDate = new Date(quest.completedDate);
            const oneDayAfter = new Date(completedDate);
            oneDayAfter.setDate(oneDayAfter.getDate() + 1);
            return now <= oneDayAfter;
        }

        return isActive;
    });
};

export const groupQuestsByFrequency = (quests) => {
    return quests.reduce((groups, quest) => {
        const frequency = quest.frequency;
        if (!groups[frequency]) {
            groups[frequency] = [];
        }
        groups[frequency].push(quest);
        return groups;
    }, {});
};

export const getFrequencyLabel = (frequency) => {
    const labels = {
        DAILY: 'Ежедневные',
        WEEKLY: 'Еженедельные',
        MONTHLY: 'Ежемесячные',
        ANNUAL: 'Ежегодные',
        ONCE: 'Однократные',
    };
    return labels[frequency] || frequency;
};