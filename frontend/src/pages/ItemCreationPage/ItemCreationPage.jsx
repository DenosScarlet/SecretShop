import React from 'react';
import ItemCreationForm from './ItemCreationForm';
import styles from '../QuestCreationPage/QuestCreationPage.module.css';

export default function ItemCreationPage() {
    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <ItemCreationForm />
            </div>
        </div>
    );
}