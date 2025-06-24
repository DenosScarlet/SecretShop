import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { questApi } from '../../services/questApi';
import styles from './TasksPage.module.css';

export default function TasksPage() {
    const { user, getUserQuests } = useAuth();
    const [isDocumentLoaded, setIsDocumentLoaded] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message, setMessage] = useState('');
    const [successCount] = useState(0);

    const handleLoadDocument = () => {
        setIsDocumentLoaded(true);
        setMessage('Документ успешно загружен! Теперь вы можете приступить к работе.');
    };

    const handleSubmitTasks = async () => {
        if (!user) return;

        setIsSubmitting(true);
        setMessage('Обработка задания...');

        try {
            const quests = await getUserQuests();
            const activeQuests = quests.filter(quest =>
                quest.questStatus !== 'COMPLETE'
            );

            if (activeQuests.length === 0) {
                setMessage('🎉 Поздравляем! Все ваши задания уже завершены');
                return;
            }

            const updatePromises = activeQuests.map(quest => {
                const requestData = {
                    userId: user.userId,
                    questId: quest.questId
                };
                return questApi.updateQuestSteps(requestData);
            });

            await Promise.all(updatePromises);
            setIsDocumentLoaded(false);
            setMessage(`✅ Успешно отправлено!`);
        } catch (error) {
            console.error('Ошибка при отправке задания:', error);
            setMessage('❌ Произошла ошибка при обработке заданий');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1>Общий поток документов</h1>

            <div className={styles.actions}>
                <button
                    onClick={handleLoadDocument}
                    disabled={isDocumentLoaded || isSubmitting}
                    className={styles.button}
                >
                    {isDocumentLoaded ? (
                        <>
                            <span className={styles.icon}>✅</span>
                            Документ загружен
                        </>
                    ) : (
                        <>
                            <span className={styles.icon}>📥</span>
                            Загрузить документ
                        </>
                    )}
                </button>

                <button
                    onClick={handleSubmitTasks}
                    disabled={!isDocumentLoaded || isSubmitting}
                    className={`${styles.button} ${styles.submitButton}`}
                >
                    {isSubmitting ? (
                        <>
                            <span className={styles.icon}>⏳</span>
                            Обработка...
                        </>
                    ) : (
                        <>
                            <span className={styles.icon}>🚀</span>
                            Отправить на проверку
                        </>
                    )}
                </button>
            </div>

            {message && <p className={styles.message}>{message}</p>}

            {successCount > 0 && (
                <div className={styles.successAnimation}>
                    <div className={styles.confetti}></div>
                    <div className={styles.confetti}></div>
                    <div className={styles.confetti}></div>
                </div>
            )}
        </div>
    );
}