// Утилиты для работы с квестами

// Функция для преобразования массива даты в объект Date
const parseDate = (dateArray) => {
    if (Array.isArray(dateArray) && dateArray.length >= 3) {
        // Массив: [год, месяц, день, час, минута, секунда]
        // Важно: месяц в JavaScript начинается с 0, поэтому вычитаем 1
        return new Date(
            dateArray[0], // год
            dateArray[1] - 1, // месяц (вычитаем 1)
            dateArray[2], // день
            dateArray[3] || 0, // час
            dateArray[4] || 0, // минута
            dateArray[5] || 0  // секунда
        );
    }
    // Если это уже строка или другой формат, пытаемся создать Date напрямую
    return new Date(dateArray);
};

export const filterActiveQuests = (quests) => {
    const now = new Date();
    console.log('Current time:', now);

    return quests.filter(quest => {
        try {
            const startDate = parseDate(quest.startDate);
            const endDate = parseDate(quest.endDate);

            console.log(`Quest "${quest.questTitle}":`, {
                startDate: startDate,
                endDate: endDate,
                now: now,
                isActive: now >= startDate && now <= endDate
            });

            const isActive = now >= startDate && now <= endDate;
            return isActive;
        } catch (error) {
            console.error('Error parsing quest dates:', error, quest);
            return false;
        }
    });
};

export const groupQuestsByFrequency = (quests) => {
    return quests.reduce((groups, quest) => {
        const frequency = quest.frequency;
        if (!groups[frequency]) groups[frequency] = [];
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