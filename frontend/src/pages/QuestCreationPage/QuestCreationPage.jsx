import React from 'react';
import QuestCreationForm from './QuestCreationForm';
import styles from './QuestCreationPage.module.css';

export default function QuestCreationPage({ collapsed }) {
    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <QuestCreationForm />
            </div>
        </div>
    );
}