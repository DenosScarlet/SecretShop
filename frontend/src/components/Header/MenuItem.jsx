import styles from './QuestMenu.module.css';

export const MenuItem = ({ title, progress, reward, isCompleted }) => {
    return (
        <div className={`${styles.menuListItem} ${isCompleted ? styles.completed : ''}`}>
            <div className={styles.itemContent}>
                <svg className={styles.iconExclamation} width="32"
                     height="32"
                     viewBox="0 0 32 32"
                     fill="none"
                     xmlns="http://www.w3.org/2000/svg"
                >
                    <path d="M16 6.6665V9.33317" stroke={isCompleted ? "#4CAF50" : "#FEB238"} strokeWidth="2" strokeLinecap="round"/>
                    <path d="M16 22.6665V13.3332" stroke={isCompleted ? "#4CAF50" : "#FEB238"} strokeWidth="2" strokeLinecap="round"/>
                    <path d="M16 25.3332V28.6665" stroke={isCompleted ? "#4CAF50" : "#FEB238"} strokeWidth="2" strokeLinecap="round"/>
                </svg>
                <div className={styles.textWrapper}>
                    <div className={styles.labelText}>{title}</div>
                    <div className={styles.progress}>{progress}</div>
                </div>
                <div className={styles.rewardWrapper}>
                    <svg className={styles.iconCoin} width="20"
                         height="20"
                         viewBox="0 0 20 20"
                         fill="none"
                         xmlns="http://www.w3.org/2000/svg"
                    >
                        <path d="M10 15.8333C13.2217 15.8333 15.8333 13.2217 15.8333 10C15.8333 6.77834 13.2217 4.16667 10 4.16667C6.77834 4.16667 4.16667 6.77834 4.16667 10C4.16667 13.2217 6.77834 15.8333 10 15.8333Z" stroke="#000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M10 12.5V7.5" stroke="#000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M7.575 9.14999L10 7.5L12.425 9.14999" stroke="#000" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                    <div className={styles.reward}>{reward}</div>
                </div>
            </div>
            <div className={styles.divider} />
        </div>
    );
};