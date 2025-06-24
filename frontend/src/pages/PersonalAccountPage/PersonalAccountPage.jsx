import React, {useState, useEffect} from 'react';
import {useAuth} from '../../contexts/AuthContext';
import {keycloakApi} from '../../services/keycloakApi';
import {shopApi} from '../../services/api';
import {questApi} from '../../services/questApi';
import {parseDate} from '../../utils/questUtils';
import {Link} from 'react-router-dom';
import styles from './PersonalAccountPage.module.css';
import Avatar from '../../components/Header/Avatar';


const PersonalAccountPage = () => {
    const {user, dtlUser, updateAvatar} = useAuth();
    const [avatarFile, setAvatarFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [avatarBlobUrl, setAvatarBlobUrl] = useState('');
    const [purchases, setPurchases] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [quests, setQuests] = useState([]);
    const [questsLoading, setQuestsLoading] = useState(true);
    const [questsSearchTerm, setQuestsSearchTerm] = useState('');


    // Загрузка аватара
    useEffect(() => {
        const fetchAvatar = async () => {
            if (!dtlUser?.avatar) {
                console.log('Аватар отсутствует в dtlUser, используется базовый');
                setAvatarBlobUrl('');
                return;
            }

            try {
                const fileName = dtlUser.avatar.split('fileName=')[1]?.split('&')[0];
                if (!fileName) throw new Error('Невалидный формат URL аватара');
                const response = await keycloakApi.downloadUserAvatar(fileName);
                const blob = new Blob([response.data], {type: response.headers['content-type']});
                const url = URL.createObjectURL(blob);
                setAvatarBlobUrl(url);
            } catch (err) {
                console.error('Ошибка загрузки аватара:', {
                    message: err.message,
                    status: err.response?.status,
                    fileName: dtlUser.avatar
                });
                setAvatarBlobUrl('');
            }
        };

        fetchAvatar();

        return () => {
            if (avatarBlobUrl) URL.revokeObjectURL(avatarBlobUrl);
        };
    }, [dtlUser?.avatar]);

    useEffect(() => {
        const fetchQuests = async () => {
            if (!user?.userId) return;

            setQuestsLoading(true);
            try {
                const response = await questApi.getUserQuests(user.userId);
                // Сортируем от новых к старым
                const sortedQuests = response.data.sort((a, b) =>
                    new Date(parseDate(b.startDate)) - new Date(parseDate(a.startDate))
                );
                setQuests(sortedQuests);
            } catch (err) {
                console.error('Ошибка загрузки квестов:', err);
                setError('Не удалось загрузить квесты');
            } finally {
                setQuestsLoading(false);
            }
        };

        fetchQuests();
    }, [user]);

    const filteredQuests = quests.filter(quest => {
        if (!questsSearchTerm) return true;
        const searchLower = questsSearchTerm.toLowerCase();
        return (
            quest.questTitle?.toLowerCase().includes(searchLower) ||
            quest.description?.toLowerCase().includes(searchLower)
        );
    });

    // Функция форматирования даты
    const formatDate = (dateArray) => {
        const date = parseDate(dateArray);
        return date.toLocaleDateString('ru-RU', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    // Загрузка покупок с изображениями
    useEffect(() => {
        const fetchPurchases = async () => {
            if (!user?.userId) return;

            setIsLoading(true);
            try {
                const response = await shopApi.getOperationsByUser(user.userId);
                const purchasesWithImages = await Promise.all(
                    response.data.map(async (operation) => {
                        try {
                            const history = JSON.parse(operation.operationHistory || '{}');
                            const itemId = history.itemId;
                            let imageUrl = 'https://via.placeholder.com/50';
                            if (itemId) {
                                const imageResponse = await shopApi.downloadItemImage(itemId, `${itemId}.jpg`);
                                imageUrl = URL.createObjectURL(new Blob([imageResponse.data]));
                            }
                            return {...operation, imageUrl};
                        } catch (error) {
                            console.error(`Ошибка загрузки изображения для операции ${operation.operationsId}:`, error);
                            return {...operation, imageUrl: 'https://via.placeholder.com/50'};
                        }
                    })
                );
                setPurchases(purchasesWithImages);
            } catch (err) {
                console.error('Ошибка загрузки покупок:', err);
                setError('Не удалось загрузить данные');
            } finally {
                setIsLoading(false);
            }
        };

        fetchPurchases();

        return () => {
            purchases.forEach((op) => {
                if (op.imageUrl && op.imageUrl.startsWith('blob:')) {
                    URL.revokeObjectURL(op.imageUrl);
                }
            });
        };
    }, [user]);

    // Фильтрация покупок по поиску
    const filteredPurchases = purchases.filter((operation) => {
        if (!searchTerm) return true;
        const history = JSON.parse(operation.operationHistory || '{}');
        const itemName = history.itemName?.toLowerCase() || '';
        return itemName.includes(searchTerm.toLowerCase());
    });

    // Обработка изменения аватарки
    const handleAvatarChange = (e) => {
        const file = e.target.files[0];
        if (file && file.type.startsWith('image/')) {
            if (file.size > 5 * 1024 * 1024) {
                setError('Файл слишком большой (максимум 5 МБ)');
                return;
            }
            setAvatarFile(file);
            if (previewUrl) URL.revokeObjectURL(previewUrl);
            setPreviewUrl(URL.createObjectURL(file));
        }
    };

    // Сохранение новой аватарки
    const saveAvatar = async () => {
        if (!avatarFile || !user?.userId) return;

        try {
            const response = await keycloakApi.uploadUserAvatar(user.userId, avatarFile);
            await updateAvatar(user.userId, response.data.avatar);

            const fileName = response.data.avatar.split('fileName=')[1]?.split('&')[0];
            const avatarResponse = await keycloakApi.downloadUserAvatar(fileName);
            const blob = new Blob([avatarResponse.data], {type: avatarResponse.headers['content-type']});
            const newUrl = URL.createObjectURL(blob);
            if (avatarBlobUrl) URL.revokeObjectURL(avatarBlobUrl);
            setAvatarBlobUrl(newUrl);

            setSuccess('Фото успешно обновлено!');
            setTimeout(() => setSuccess(''), 3000);

            setAvatarFile(null);
            if (previewUrl) URL.revokeObjectURL(previewUrl);
            setPreviewUrl('');
        } catch (err) {
            setError('Ошибка при обновлении данных: ' + (err.response?.data?.message || err.message));
            console.error('Ошибка загрузки фото:', err);
        }
    };

    // Получение информации о покупке
    const getPurchaseInfo = (operation) => {
        try {
            const history = JSON.parse(operation.operationHistory || '{}');
            return {
                itemId: history.itemId || null,
                itemName: history.itemName || 'Неизвестный товар',
                cost: history.cost || 0,
                status: operation.status,
            };
        } catch (e) {
            console.error('Ошибка парсинга истории операции:', e);
            return {
                itemId: null,
                itemName: 'Ошибка загрузки',
                cost: 0,
                status: 'UNKNOWN',
            };
        }
    };

    // Получение статуса операции
    const getStatusBadge = (status) => {
        const statusConfig = [
            {value: 'COMPLETE', label: 'Выдано', color: '#4caf50'},
            {value: 'IN_PROGRESS', label: 'Ожидает выдачи', color: '#ffeb3b'},
            {value: 'CANCEL', label: 'Отменено', color: '#f44336'},
        ].find(opt => opt.value === status) || {label: 'Неизвестно', color: '#ccc'};

        return (
            <span
                className={styles.statusBadge}
                style={{backgroundColor: statusConfig.color}}
            >
                {statusConfig.label}
            </span>
        );
    };

    // Получение URL аватара
    const getAvatarUrl = () => {
        return previewUrl || avatarBlobUrl;
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.accountCard}>
                <h1 className={styles.title}>Личный кабинет</h1>

                {/* Основная информация */}
                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>Основная информация</h2>
                    <div className={styles.profileInfo}>
                        <div className={styles.avatarSection}>
                            <div className={styles.avatarContainer}>
                                <Avatar
                                    imageUrl={getAvatarUrl()}
                                    className={styles.avatar}
                                />
                                <label className={styles.avatarUpload}>
                                    <input
                                        type="file"
                                        accept="image/*"
                                        onChange={handleAvatarChange}
                                        className={styles.fileInput}
                                    />
                                    Выбрать файл
                                </label>
                                {avatarFile && (
                                    <button
                                        onClick={saveAvatar}
                                        className={styles.saveButton}
                                    >
                                        Сохранить
                                    </button>
                                )}
                            </div>
                        </div>

                        <div className={styles.userDetails}>
                            <div className={styles.detailRow}>
                                <span className={styles.detailLabel}>ФИО:</span>
                                <span className={styles.detailValue}>
                                    {dtlUser?.lastName || ''} {dtlUser?.firstName || ''} {dtlUser?.middleName || ''}
                                </span>
                            </div>
                            <div className={styles.detailRow}>
                                <span className={styles.detailLabel}>Email:</span>
                                <span className={styles.detailValue}>{user?.email}</span>
                            </div>
                            <div className={styles.detailRow}>
                                <span className={styles.detailLabel}>ID:</span>
                                <span className={styles.detailValue}>{user?.userId}</span>
                            </div>
                            <div className={styles.detailRow}>
                                <span className={styles.detailLabel}>Рабочая группа:</span>
                                <span className={styles.detailValue}>
                                    {dtlUser?.workGroup || 'Не указана'}
                                </span>
                            </div>
                            <div className={styles.detailRow}>
                                <span className={styles.detailLabel}>Баланс:</span>
                                <span className={styles.balance}>
                                    <svg
                                        className={styles.iconCoin}
                                        width="20"
                                        height="20"
                                        viewBox="0 0 20 20"
                                        fill="none"
                                        xmlns="http://www.w3.org/2000/svg"
                                    >
                        <path d="M7.5 0H6.25V1.25H7.5V0Z" fill="black"/>
                        <path d="M8.75 0H7.5V1.25H8.75V0Z" fill="black"/>
                        <path d="M10 0H8.75V1.25H10V0Z" fill="black"/>
                        <path d="M11.25 0H10V1.25H11.25V0Z" fill="black"/>
                        <path d="M12.5 0H11.25V1.25H12.5V0Z" fill="black"/>
                        <path d="M13.75 0H12.5V1.25H13.75V0Z" fill="black"/>
                        <path d="M5 1.25006H3.75V2.50006H5V1.25006Z" fill="black"/>
                        <path d="M6.25 1.25006H5V2.50006H6.25V1.25006Z" fill="black"/>
                        <path d="M7.5 1.25006H6.25V2.50006H7.5V1.25006Z" fill="#FFB039"/>
                        <path d="M8.75 1.25006H7.5V2.50006H8.75V1.25006Z" fill="#FFB039"/>
                        <path d="M10 1.25006H8.75V2.50006H10V1.25006Z" fill="#FFB039"/>
                        <path d="M11.25 1.25006H10V2.50006H11.25V1.25006Z" fill="#FFB039"/>
                        <path d="M12.5 1.25006H11.25V2.50006H12.5V1.25006Z" fill="#FFB039"/>
                        <path d="M13.75 1.25006H12.5V2.50006H13.75V1.25006Z" fill="#FFB039"/>
                        <path d="M15 1.25006H13.75V2.50006H15V1.25006Z" fill="black"/>
                        <path d="M16.25 1.25006H15V2.50006H16.25V1.25006Z" fill="black"/>
                        <path d="M3.75 2.5H2.5V3.75H3.75V2.5Z" fill="black"/>
                        <path d="M5 2.5H3.75V3.75H5V2.5Z" fill="#FFB039"/>
                        <path d="M6.25 2.5H5V3.75H6.25V2.5Z" fill="#FFB039"/>
                        <path d="M7.5 2.5H6.25V3.75H7.5V2.5Z" fill="#FFFFFB"/>
                        <path d="M8.75 2.5H7.5V3.75H8.75V2.5Z" fill="#FFE85E"/>
                        <path d="M10 2.5H8.75V3.75H10V2.5Z" fill="#FFE85E"/>
                        <path d="M11.25 2.5H10V3.75H11.25V2.5Z" fill="#FFE85E"/>
                        <path d="M12.5 2.5H11.25V3.75H12.5V2.5Z" fill="#FFE85E"/>
                        <path d="M13.75 2.5H12.5V3.75H13.75V2.5Z" fill="#FFE85E"/>
                        <path d="M15 2.5H13.75V3.75H15V2.5Z" fill="#DB7103"/>
                        <path d="M16.25 2.5H15V3.75H16.25V2.5Z" fill="#DB7103"/>
                        <path d="M17.5 2.5H16.25V3.75H17.5V2.5Z" fill="black"/>
                        <path d="M2.5 3.75H1.25V5H2.5V3.75Z" fill="black"/>
                        <path d="M3.75 3.75H2.5V5H3.75V3.75Z" fill="#FFB039"/>
                        <path d="M5 3.75H3.75V5H5V3.75Z" fill="#FFFFFB"/>
                        <path d="M6.25 3.75H5V5H6.25V3.75Z" fill="#FFFFFB"/>
                        <path d="M7.5 3.75H6.25V5H7.5V3.75Z" fill="#FFE85E"/>
                        <path d="M8.75 3.75H7.5V5H8.75V3.75Z" fill="#DB7103"/>
                        <path d="M10 3.75H8.75V5H10V3.75Z" fill="#DB7103"/>
                        <path d="M11.25 3.75H10V5H11.25V3.75Z" fill="#DB7103"/>
                        <path d="M12.5 3.75H11.25V5H12.5V3.75Z" fill="#DB7103"/>
                        <path d="M13.75 3.75H12.5V5H13.75V3.75Z" fill="#FFE85E"/>
                        <path d="M15 3.75H13.75V5H15V3.75Z" fill="#FFE85E"/>
                        <path d="M16.25 3.75H15V5H16.25V3.75Z" fill="#FFE85E"/>
                        <path d="M17.5 3.75H16.25V5H17.5V3.75Z" fill="#DB7103"/>
                        <path d="M18.75 3.75H17.5V5H18.75V3.75Z" fill="black"/>
                        <path d="M2.5 4.99994H1.25V6.24994H2.5V4.99994Z" fill="black"/>
                        <path d="M3.75 4.99994H2.5V6.24994H3.75V4.99994Z" fill="#FFB039"/>
                        <path d="M5 4.99994H3.75V6.24994H5V4.99994Z" fill="#FFFFFB"/>
                        <path d="M6.25 4.99994H5V6.24994H6.25V4.99994Z" fill="#DB7103"/>
                        <path d="M7.5 4.99994H6.25V6.24994H7.5V4.99994Z" fill="#DB7103"/>
                        <path d="M8.75 4.99994H7.5V6.24994H8.75V4.99994Z" fill="#FFB039"/>
                        <path d="M10 4.99994H8.75V6.24994H10V4.99994Z" fill="#FFB039"/>
                        <path d="M11.25 4.99994H10V6.24994H11.25V4.99994Z" fill="#FFB039"/>
                        <path d="M12.5 4.99994H11.25V6.24994H12.5V4.99994Z" fill="#FFB039"/>
                        <path d="M13.75 4.99994H12.5V6.24994H13.75V4.99994Z" fill="#DB7103"/>
                        <path d="M15 4.99994H13.75V6.24994H15V4.99994Z" fill="#DB7103"/>
                        <path d="M16.25 4.99994H15V6.24994H16.25V4.99994Z" fill="#FFE85E"/>
                        <path d="M17.5 4.99994H16.25V6.24994H17.5V4.99994Z" fill="#DB7103"/>
                        <path d="M18.75 4.99994H17.5V6.24994H18.75V4.99994Z" fill="black"/>
                        <path d="M1.25 6.25H0V7.5H1.25V6.25Z" fill="black"/>
                        <path d="M2.5 6.25H1.25V7.5H2.5V6.25Z" fill="#FFB039"/>
                        <path d="M3.75 6.25H2.5V7.5H3.75V6.25Z" fill="#FFFFFB"/>
                        <path d="M5 6.25H3.75V7.5H5V6.25Z" fill="#FFE85E"/>
                        <path d="M6.25 6.25H5V7.5H6.25V6.25Z" fill="#DB7103"/>
                        <path d="M7.5 6.25H6.25V7.5H7.5V6.25Z" fill="#FFB039"/>
                        <path d="M8.75 6.25H7.5V7.5H8.75V6.25Z" fill="#FFB039"/>
                        <path d="M10 6.25H8.75V7.5H10V6.25Z" fill="#FFB039"/>
                        <path d="M11.25 6.25H10V7.5H11.25V6.25Z" fill="#FFB039"/>
                        <path d="M12.5 6.25H11.25V7.5H12.5V6.25Z" fill="#FFE85E"/>
                        <path d="M13.75 6.25H12.5V7.5H13.75V6.25Z" fill="#FFE85E"/>
                        <path d="M15 6.25H13.75V7.5H15V6.25Z" fill="#DB7103"/>
                        <path d="M16.25 6.25H15V7.5H16.25V6.25Z" fill="#FFE85E"/>
                        <path d="M17.5 6.25H16.25V7.5H17.5V6.25Z" fill="#FFE85E"/>
                        <path d="M18.75 6.25H17.5V7.5H18.75V6.25Z" fill="#DB7103"/>
                        <path d="M20 6.25H18.75V7.5H20V6.25Z" fill="black"/>
                        <path d="M1.25 7.50006H0V8.75006H1.25V7.50006Z" fill="black"/>
                        <path d="M2.5 7.50006H1.25V8.75006H2.5V7.50006Z" fill="#FFB039"/>
                        <path d="M3.75 7.50006H2.5V8.75006H3.75V7.50006Z" fill="#FFE85E"/>
                        <path d="M5 7.50006H3.75V8.75006H5V7.50006Z" fill="#DB7103"/>
                        <path d="M6.25 7.50006H5V8.75006H6.25V7.50006Z" fill="#FFB039"/>
                        <path d="M7.5 7.50006H6.25V8.75006H7.5V7.50006Z" fill="#FFB039"/>
                        <path d="M8.75 7.50006H7.5V8.75006H8.75V7.50006Z" fill="#FFB039"/>
                        <path d="M10 7.50006H8.75V8.75006H10V7.50006Z" fill="#FFE85E"/>
                        <path d="M11.25 7.50006H10V8.75006H11.25V7.50006Z" fill="#FFE85E"/>
                        <path d="M12.5 7.50006H11.25V8.75006H12.5V7.50006Z" fill="#FFE85E"/>
                        <path d="M13.75 7.50006H12.5V8.75006H13.75V7.50006Z" fill="#FFE85E"/>
                        <path d="M15 7.50006H13.75V8.75006H15V7.50006Z" fill="#FFE85E"/>
                        <path d="M16.25 7.50006H15V8.75006H16.25V7.50006Z" fill="#FFB039"/>
                        <path d="M17.5 7.50006H16.25V8.75006H17.5V7.50006Z" fill="#FFE85E"/>
                        <path d="M18.75 7.50006H17.5V8.75006H18.75V7.50006Z" fill="#DB7103"/>
                        <path d="M20 7.50006H18.75V8.75006H20V7.50006Z" fill="black"/>
                        <path d="M1.25 8.75H0V10H1.25V8.75Z" fill="black"/>
                        <path d="M2.5 8.75H1.25V10H2.5V8.75Z" fill="#FFB039"/>
                        <path d="M3.75 8.75H2.5V10H3.75V8.75Z" fill="#FFE85E"/>
                        <path d="M5 8.75H3.75V10H5V8.75Z" fill="#DB7103"/>
                        <path d="M6.25 8.75H5V10H6.25V8.75Z" fill="#FFB039"/>
                        <path d="M7.5 8.75H6.25V10H7.5V8.75Z" fill="#FFB039"/>
                        <path d="M8.75 8.75H7.5V10H8.75V8.75Z" fill="#FFE85E"/>
                        <path d="M10 8.75H8.75V10H10V8.75Z" fill="#FFE85E"/>
                        <path d="M11.25 8.75H10V10H11.25V8.75Z" fill="#FFE85E"/>
                        <path d="M12.5 8.75H11.25V10H12.5V8.75Z" fill="#FFE85E"/>
                        <path d="M13.75 8.75H12.5V10H13.75V8.75Z" fill="#FFE85E"/>
                        <path d="M15 8.75H13.75V10H15V8.75Z" fill="#FFFFFB"/>
                        <path d="M16.25 8.75H15V10H16.25V8.75Z" fill="#FFB039"/>
                        <path d="M17.5 8.75H16.25V10H17.5V8.75Z" fill="#FFE85E"/>
                        <path d="M18.75 8.75H17.5V10H18.75V8.75Z" fill="#DB7103"/>
                        <path d="M20 8.75H18.75V10H20V8.75Z" fill="black"/>
                        <path d="M1.25 10H0V11.25H1.25V10Z" fill="black"/>
                        <path d="M2.5 10H1.25V11.25H2.5V10Z" fill="#FFB039"/>
                        <path d="M3.75 10H2.5V11.25H3.75V10Z" fill="#FFE85E"/>
                        <path d="M5 10H3.75V11.25H5V10Z" fill="#DB7103"/>
                        <path d="M6.25 10H5V11.25H6.25V10Z" fill="#FFB039"/>
                        <path d="M7.5 10H6.25V11.25H7.5V10Z" fill="#FFB039"/>
                        <path d="M8.75 10H7.5V11.25H8.75V10Z" fill="#FFE85E"/>
                        <path d="M10 10H8.75V11.25H10V10Z" fill="#FFE85E"/>
                        <path d="M11.25 10H10V11.25H11.25V10Z" fill="#FFE85E"/>
                        <path d="M12.5 10H11.25V11.25H12.5V10Z" fill="#FFE85E"/>
                        <path d="M13.75 10H12.5V11.25H13.75V10Z" fill="#FFE85E"/>
                        <path d="M15 10H13.75V11.25H15V10Z" fill="#FFFFFB"/>
                        <path d="M16.25 10H15V11.25H16.25V10Z" fill="#FFB039"/>
                        <path d="M17.5 10H16.25V11.25H17.5V10Z" fill="#FFE85E"/>
                        <path d="M18.75 10H17.5V11.25H18.75V10Z" fill="#DB7103"/>
                        <path d="M20 10H18.75V11.25H20V10Z" fill="black"/>
                        <path d="M1.25 11.2499H0V12.4999H1.25V11.2499Z" fill="black"/>
                        <path d="M2.5 11.2499H1.25V12.4999H2.5V11.2499Z" fill="#FFB039"/>
                        <path d="M3.75 11.2499H2.5V12.4999H3.75V11.2499Z" fill="#FFE85E"/>
                        <path d="M5 11.2499H3.75V12.4999H5V11.2499Z" fill="#DB7103"/>
                        <path d="M6.25 11.2499H5V12.4999H6.25V11.2499Z" fill="#FFB039"/>
                        <path d="M7.5 11.2499H6.25V12.4999H7.5V11.2499Z" fill="#FFE85E"/>
                        <path d="M8.75 11.2499H7.5V12.4999H8.75V11.2499Z" fill="#FFE85E"/>
                        <path d="M10 11.2499H8.75V12.4999H10V11.2499Z" fill="#FFE85E"/>
                        <path d="M11.25 11.2499H10V12.4999H11.25V11.2499Z" fill="#FFE85E"/>
                        <path d="M12.5 11.2499H11.25V12.4999H12.5V11.2499Z" fill="#FFE85E"/>
                        <path d="M13.75 11.2499H12.5V12.4999H13.75V11.2499Z" fill="#FFE85E"/>
                        <path d="M15 11.2499H13.75V12.4999H15V11.2499Z" fill="#FFFFFB"/>
                        <path d="M16.25 11.2499H15V12.4999H16.25V11.2499Z" fill="#FFB039"/>
                        <path d="M17.5 11.2499H16.25V12.4999H17.5V11.2499Z" fill="#FFE85E"/>
                        <path d="M18.75 11.2499H17.5V12.4999H18.75V11.2499Z" fill="#DB7103"/>
                        <path d="M20 11.2499H18.75V12.4999H20V11.2499Z" fill="black"/>
                        <path d="M1.25 12.5H0V13.75H1.25V12.5Z" fill="black"/>
                        <path d="M2.5 12.5H1.25V13.75H2.5V12.5Z" fill="#FFB039"/>
                        <path d="M3.75 12.5H2.5V13.75H3.75V12.5Z" fill="#FFE85E"/>
                        <path d="M5 12.5H3.75V13.75H5V12.5Z" fill="#FFE85E"/>
                        <path d="M6.25 12.5H5V13.75H6.25V12.5Z" fill="#DB7103"/>
                        <path d="M7.5 12.5H6.25V13.75H7.5V12.5Z" fill="#FFE85E"/>
                        <path d="M8.75 12.5H7.5V13.75H8.75V12.5Z" fill="#FFE85E"/>
                        <path d="M10 12.5H8.75V13.75H10V12.5Z" fill="#FFE85E"/>
                        <path d="M11.25 12.5H10V13.75H11.25V12.5Z" fill="#FFE85E"/>
                        <path d="M12.5 12.5H11.25V13.75H12.5V12.5Z" fill="#FFE85E"/>
                        <path d="M13.75 12.5H12.5V13.75H13.75V12.5Z" fill="#FFFFFB"/>
                        <path d="M15 12.5H13.75V13.75H15V12.5Z" fill="#FFB039"/>
                        <path d="M16.25 12.5H15V13.75H16.25V12.5Z" fill="#FFE85E"/>
                        <path d="M17.5 12.5H16.25V13.75H17.5V12.5Z" fill="#FFE85E"/>
                        <path d="M18.75 12.5H17.5V13.75H18.75V12.5Z" fill="#DB7103"/>
                        <path d="M20 12.5H18.75V13.75H20V12.5Z" fill="black"/>
                        <path d="M2.5 13.7501H1.25V15.0001H2.5V13.7501Z" fill="black"/>
                        <path d="M3.75 13.7501H2.5V15.0001H3.75V13.7501Z" fill="#FFB039"/>
                        <path d="M5 13.7501H3.75V15.0001H5V13.7501Z" fill="#FFE85E"/>
                        <path d="M6.25 13.7501H5V15.0001H6.25V13.7501Z" fill="#DB7103"/>
                        <path d="M7.5 13.7501H6.25V15.0001H7.5V13.7501Z" fill="#DB7103"/>
                        <path d="M8.75 13.7501H7.5V15.0001H8.75V13.7501Z" fill="#FFE85E"/>
                        <path d="M10 13.7501H8.75V15.0001H10V13.7501Z" fill="#FFFFFB"/>
                        <path d="M11.25 13.7501H10V15.0001H11.25V13.7501Z" fill="#FFFFFB"/>
                        <path d="M12.5 13.7501H11.25V15.0001H12.5V13.7501Z" fill="#FFFFFB"/>
                        <path d="M13.75 13.7501H12.5V15.0001H13.75V13.7501Z" fill="#FFB039"/>
                        <path d="M15 13.7501H13.75V15.0001H15V13.7501Z" fill="#FFB039"/>
                        <path d="M16.25 13.7501H15V15.0001H16.25V13.7501Z" fill="#FFE85E"/>
                        <path d="M17.5 13.7501H16.25V15.0001H17.5V13.7501Z" fill="#DB7103"/>
                        <path d="M18.75 13.7501H17.5V15.0001H18.75V13.7501Z" fill="black"/>
                        <path d="M2.5 15H1.25V16.25H2.5V15Z" fill="black"/>
                        <path d="M3.75 15H2.5V16.25H3.75V15Z" fill="#FFB039"/>
                        <path d="M5 15H3.75V16.25H5V15Z" fill="#FFE85E"/>
                        <path d="M6.25 15H5V16.25H6.25V15Z" fill="#FFE85E"/>
                        <path d="M7.5 15H6.25V16.25H7.5V15Z" fill="#FFE85E"/>
                        <path d="M8.75 15H7.5V16.25H8.75V15Z" fill="#FFB039"/>
                        <path d="M10 15H8.75V16.25H10V15Z" fill="#FFB039"/>
                        <path d="M11.25 15H10V16.25H11.25V15Z" fill="#FFB039"/>
                        <path d="M12.5 15H11.25V16.25H12.5V15Z" fill="#FFB039"/>
                        <path d="M13.75 15H12.5V16.25H13.75V15Z" fill="#FFE85E"/>
                        <path d="M15 15H13.75V16.25H15V15Z" fill="#FFE85E"/>
                        <path d="M16.25 15H15V16.25H16.25V15Z" fill="#FFE85E"/>
                        <path d="M17.5 15H16.25V16.25H17.5V15Z" fill="#DB7103"/>
                        <path d="M18.75 15H17.5V16.25H18.75V15Z" fill="black"/>
                        <path d="M3.75 16.25H2.5V17.5H3.75V16.25Z" fill="black"/>
                        <path d="M5 16.25H3.75V17.5H5V16.25Z" fill="#FFB039"/>
                        <path d="M6.25 16.25H5V17.5H6.25V16.25Z" fill="#FFB039"/>
                        <path d="M7.5 16.25H6.25V17.5H7.5V16.25Z" fill="#FFE85E"/>
                        <path d="M8.75 16.25H7.5V17.5H8.75V16.25Z" fill="#FFE85E"/>
                        <path d="M10 16.25H8.75V17.5H10V16.25Z" fill="#FFE85E"/>
                        <path d="M11.25 16.25H10V17.5H11.25V16.25Z" fill="#FFE85E"/>
                        <path d="M12.5 16.25H11.25V17.5H12.5V16.25Z" fill="#FFE85E"/>
                        <path d="M13.75 16.25H12.5V17.5H13.75V16.25Z" fill="#FFE85E"/>
                        <path d="M15 16.25H13.75V17.5H15V16.25Z" fill="#DB7103"/>
                        <path d="M16.25 16.25H15V17.5H16.25V16.25Z" fill="#DB7103"/>
                        <path d="M17.5 16.25H16.25V17.5H17.5V16.25Z" fill="black"/>
                        <path d="M5 17.5H3.75V18.75H5V17.5Z" fill="black"/>
                        <path d="M6.25 17.5H5V18.75H6.25V17.5Z" fill="black"/>
                        <path d="M7.5 17.5H6.25V18.75H7.5V17.5Z" fill="#FFB039"/>
                        <path d="M8.75 17.5H7.5V18.75H8.75V17.5Z" fill="#FFB039"/>
                        <path d="M10 17.5H8.75V18.75H10V17.5Z" fill="#FFB039"/>
                        <path d="M11.25 17.5H10V18.75H11.25V17.5Z" fill="#FFB039"/>
                        <path d="M12.5 17.5H11.25V18.75H12.5V17.5Z" fill="#FFB039"/>
                        <path d="M13.75 17.5H12.5V18.75H13.75V17.5Z" fill="#FFB039"/>
                        <path d="M15 17.5H13.75V18.75H15V17.5Z" fill="black"/>
                        <path d="M16.25 17.5H15V18.75H16.25V17.5Z" fill="black"/>
                        <path d="M7.5 18.75H6.25V20H7.5V18.75Z" fill="black"/>
                        <path d="M8.75 18.75H7.5V20H8.75V18.75Z" fill="black"/>
                        <path d="M10 18.75H8.75V20H10V18.75Z" fill="black"/>
                        <path d="M11.25 18.75H10V20H11.25V18.75Z" fill="black"/>
                        <path d="M12.5 18.75H11.25V20H12.5V18.75Z" fill="black"/>
                        <path d="M13.75 18.75H12.5V20H13.75V18.75Z" fill="black"/>
                    </svg>
                                    <span>{dtlUser?.balance || 0}</span>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>История квестов</h2>
                    <div className={styles.filterContainer}>
                        <div className={styles.searchWrapper}>
                            <input
                                type="text"
                                placeholder="Поиск по названию или описанию квеста..."
                                value={questsSearchTerm}
                                onChange={(e) => setQuestsSearchTerm(e.target.value)}
                                className={styles.inputField}
                            />
                            <svg className={styles.searchIcon} width="16" height="16" viewBox="0 0 16 16" fill="none">
                                <path
                                    d="M11.435 10.063h-.723l-.256-.247a5.92 5.92 0 0 0 1.437-3.87 5.946 5.946 0 1 0-5.947 5.947 5.92 5.92 0 0 0 3.87-1.437l.247.256v.723L14.637 15 16 13.637l-4.565-4.574zm-5.489 0A4.375 4.375 0 1 1 10.32 5.946a4.381 4.381 0 0 1-4.375 4.375z"
                                    fill="#757575"/>
                            </svg>
                        </div>
                    </div>

                    {questsLoading ? (
                        <div className={styles.loading}>
                            <div className={styles.spinner}></div>
                            Загрузка истории квестов...
                        </div>
                    ) : filteredQuests.length === 0 ? (
                        <div className={styles.empty}>
                            Квестов не найдено
                            {questsSearchTerm && (
                                <button
                                    onClick={() => setQuestsSearchTerm('')}
                                    className={styles.passwordButton}
                                >
                                    Сбросить поиск
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className={styles.questsList}>
                            {filteredQuests.map((quest) => (
                                <div key={quest.questId} className={styles.questItem}>
                                    <div className={styles.questInfo}>
                                        <div className={styles.questTitle}>{quest.questTitle}</div>
                                        <div className={styles.questDescription}>
                                            {quest.description ?? "Описание отсутствует"}
                                        </div>
                                        <div className={styles.questSteps}>
                                            Шаги: <strong>{quest.completedSteps || 0}/{quest.stepsToComplete || 0}</strong>
                                        </div>
                                        <div className={styles.questMeta}>
                            <span className={styles.questDate}>
                                {formatDate(quest.startDate)} - {formatDate(quest.endDate)}
                            </span>
                                            <span className={`${styles.questStatus} ${
                                                quest.questStatus === 'COMPLETE' ? styles.statusComplete : styles.statusIncomplete
                                            }`}>
                                {quest.questStatus === 'COMPLETE' ? "Завершён" : "Не завершён"}
                            </span>
                                        </div>
                                    </div>
                                    <div className={styles.iconCoin}>
                                        <svg
                                            className={styles.iconCoin}
                                            width="20"
                                            height="20"
                                            viewBox="0 0 20 20"
                                            fill="none"
                                            xmlns="http://www.w3.org/2000/svg"
                                        >
                                            <path d="M7.5 0H6.25V1.25H7.5V0Z" fill="black"/>
                                            <path d="M8.75 0H7.5V1.25H8.75V0Z" fill="black"/>
                                            <path d="M10 0H8.75V1.25H10V0Z" fill="black"/>
                                            <path d="M11.25 0H10V1.25H11.25V0Z" fill="black"/>
                                            <path d="M12.5 0H11.25V1.25H12.5V0Z" fill="black"/>
                                            <path d="M13.75 0H12.5V1.25H13.75V0Z" fill="black"/>
                                            <path d="M5 1.25006H3.75V2.50006H5V1.25006Z" fill="black"/>
                                            <path d="M6.25 1.25006H5V2.50006H6.25V1.25006Z" fill="black"/>
                                            <path d="M7.5 1.25006H6.25V2.50006H7.5V1.25006Z" fill="#FFB039"/>
                                            <path d="M8.75 1.25006H7.5V2.50006H8.75V1.25006Z" fill="#FFB039"/>
                                            <path d="M10 1.25006H8.75V2.50006H10V1.25006Z" fill="#FFB039"/>
                                            <path d="M11.25 1.25006H10V2.50006H11.25V1.25006Z" fill="#FFB039"/>
                                            <path d="M12.5 1.25006H11.25V2.50006H12.5V1.25006Z" fill="#FFB039"/>
                                            <path d="M13.75 1.25006H12.5V2.50006H13.75V1.25006Z" fill="#FFB039"/>
                                            <path d="M15 1.25006H13.75V2.50006H15V1.25006Z" fill="black"/>
                                            <path d="M16.25 1.25006H15V2.50006H16.25V1.25006Z" fill="black"/>
                                            <path d="M3.75 2.5H2.5V3.75H3.75V2.5Z" fill="black"/>
                                            <path d="M5 2.5H3.75V3.75H5V2.5Z" fill="#FFB039"/>
                                            <path d="M6.25 2.5H5V3.75H6.25V2.5Z" fill="#FFB039"/>
                                            <path d="M7.5 2.5H6.25V3.75H7.5V2.5Z" fill="#FFFFFB"/>
                                            <path d="M8.75 2.5H7.5V3.75H8.75V2.5Z" fill="#FFE85E"/>
                                            <path d="M10 2.5H8.75V3.75H10V2.5Z" fill="#FFE85E"/>
                                            <path d="M11.25 2.5H10V3.75H11.25V2.5Z" fill="#FFE85E"/>
                                            <path d="M12.5 2.5H11.25V3.75H12.5V2.5Z" fill="#FFE85E"/>
                                            <path d="M13.75 2.5H12.5V3.75H13.75V2.5Z" fill="#FFE85E"/>
                                            <path d="M15 2.5H13.75V3.75H15V2.5Z" fill="#DB7103"/>
                                            <path d="M16.25 2.5H15V3.75H16.25V2.5Z" fill="#DB7103"/>
                                            <path d="M17.5 2.5H16.25V3.75H17.5V2.5Z" fill="black"/>
                                            <path d="M2.5 3.75H1.25V5H2.5V3.75Z" fill="black"/>
                                            <path d="M3.75 3.75H2.5V5H3.75V3.75Z" fill="#FFB039"/>
                                            <path d="M5 3.75H3.75V5H5V3.75Z" fill="#FFFFFB"/>
                                            <path d="M6.25 3.75H5V5H6.25V3.75Z" fill="#FFFFFB"/>
                                            <path d="M7.5 3.75H6.25V5H7.5V3.75Z" fill="#FFE85E"/>
                                            <path d="M8.75 3.75H7.5V5H8.75V3.75Z" fill="#DB7103"/>
                                            <path d="M10 3.75H8.75V5H10V3.75Z" fill="#DB7103"/>
                                            <path d="M11.25 3.75H10V5H11.25V3.75Z" fill="#DB7103"/>
                                            <path d="M12.5 3.75H11.25V5H12.5V3.75Z" fill="#DB7103"/>
                                            <path d="M13.75 3.75H12.5V5H13.75V3.75Z" fill="#FFE85E"/>
                                            <path d="M15 3.75H13.75V5H15V3.75Z" fill="#FFE85E"/>
                                            <path d="M16.25 3.75H15V5H16.25V3.75Z" fill="#FFE85E"/>
                                            <path d="M17.5 3.75H16.25V5H17.5V3.75Z" fill="#DB7103"/>
                                            <path d="M18.75 3.75H17.5V5H18.75V3.75Z" fill="black"/>
                                            <path d="M2.5 4.99994H1.25V6.24994H2.5V4.99994Z" fill="black"/>
                                            <path d="M3.75 4.99994H2.5V6.24994H3.75V4.99994Z" fill="#FFB039"/>
                                            <path d="M5 4.99994H3.75V6.24994H5V4.99994Z" fill="#FFFFFB"/>
                                            <path d="M6.25 4.99994H5V6.24994H6.25V4.99994Z" fill="#DB7103"/>
                                            <path d="M7.5 4.99994H6.25V6.24994H7.5V4.99994Z" fill="#DB7103"/>
                                            <path d="M8.75 4.99994H7.5V6.24994H8.75V4.99994Z" fill="#FFB039"/>
                                            <path d="M10 4.99994H8.75V6.24994H10V4.99994Z" fill="#FFB039"/>
                                            <path d="M11.25 4.99994H10V6.24994H11.25V4.99994Z" fill="#FFB039"/>
                                            <path d="M12.5 4.99994H11.25V6.24994H12.5V4.99994Z" fill="#FFB039"/>
                                            <path d="M13.75 4.99994H12.5V6.24994H13.75V4.99994Z" fill="#DB7103"/>
                                            <path d="M15 4.99994H13.75V6.24994H15V4.99994Z" fill="#DB7103"/>
                                            <path d="M16.25 4.99994H15V6.24994H16.25V4.99994Z" fill="#FFE85E"/>
                                            <path d="M17.5 4.99994H16.25V6.24994H17.5V4.99994Z" fill="#DB7103"/>
                                            <path d="M18.75 4.99994H17.5V6.24994H18.75V4.99994Z" fill="black"/>
                                            <path d="M1.25 6.25H0V7.5H1.25V6.25Z" fill="black"/>
                                            <path d="M2.5 6.25H1.25V7.5H2.5V6.25Z" fill="#FFB039"/>
                                            <path d="M3.75 6.25H2.5V7.5H3.75V6.25Z" fill="#FFFFFB"/>
                                            <path d="M5 6.25H3.75V7.5H5V6.25Z" fill="#FFE85E"/>
                                            <path d="M6.25 6.25H5V7.5H6.25V6.25Z" fill="#DB7103"/>
                                            <path d="M7.5 6.25H6.25V7.5H7.5V6.25Z" fill="#FFB039"/>
                                            <path d="M8.75 6.25H7.5V7.5H8.75V6.25Z" fill="#FFB039"/>
                                            <path d="M10 6.25H8.75V7.5H10V6.25Z" fill="#FFB039"/>
                                            <path d="M11.25 6.25H10V7.5H11.25V6.25Z" fill="#FFB039"/>
                                            <path d="M12.5 6.25H11.25V7.5H12.5V6.25Z" fill="#FFE85E"/>
                                            <path d="M13.75 6.25H12.5V7.5H13.75V6.25Z" fill="#FFE85E"/>
                                            <path d="M15 6.25H13.75V7.5H15V6.25Z" fill="#DB7103"/>
                                            <path d="M16.25 6.25H15V7.5H16.25V6.25Z" fill="#FFE85E"/>
                                            <path d="M17.5 6.25H16.25V7.5H17.5V6.25Z" fill="#FFE85E"/>
                                            <path d="M18.75 6.25H17.5V7.5H18.75V6.25Z" fill="#DB7103"/>
                                            <path d="M20 6.25H18.75V7.5H20V6.25Z" fill="black"/>
                                            <path d="M1.25 7.50006H0V8.75006H1.25V7.50006Z" fill="black"/>
                                            <path d="M2.5 7.50006H1.25V8.75006H2.5V7.50006Z" fill="#FFB039"/>
                                            <path d="M3.75 7.50006H2.5V8.75006H3.75V7.50006Z" fill="#FFE85E"/>
                                            <path d="M5 7.50006H3.75V8.75006H5V7.50006Z" fill="#DB7103"/>
                                            <path d="M6.25 7.50006H5V8.75006H6.25V7.50006Z" fill="#FFB039"/>
                                            <path d="M7.5 7.50006H6.25V8.75006H7.5V7.50006Z" fill="#FFB039"/>
                                            <path d="M8.75 7.50006H7.5V8.75006H8.75V7.50006Z" fill="#FFB039"/>
                                            <path d="M10 7.50006H8.75V8.75006H10V7.50006Z" fill="#FFE85E"/>
                                            <path d="M11.25 7.50006H10V8.75006H11.25V7.50006Z" fill="#FFE85E"/>
                                            <path d="M12.5 7.50006H11.25V8.75006H12.5V7.50006Z" fill="#FFE85E"/>
                                            <path d="M13.75 7.50006H12.5V8.75006H13.75V7.50006Z" fill="#FFE85E"/>
                                            <path d="M15 7.50006H13.75V8.75006H15V7.50006Z" fill="#FFE85E"/>
                                            <path d="M16.25 7.50006H15V8.75006H16.25V7.50006Z" fill="#FFB039"/>
                                            <path d="M17.5 7.50006H16.25V8.75006H17.5V7.50006Z" fill="#FFE85E"/>
                                            <path d="M18.75 7.50006H17.5V8.75006H18.75V7.50006Z" fill="#DB7103"/>
                                            <path d="M20 7.50006H18.75V8.75006H20V7.50006Z" fill="black"/>
                                            <path d="M1.25 8.75H0V10H1.25V8.75Z" fill="black"/>
                                            <path d="M2.5 8.75H1.25V10H2.5V8.75Z" fill="#FFB039"/>
                                            <path d="M3.75 8.75H2.5V10H3.75V8.75Z" fill="#FFE85E"/>
                                            <path d="M5 8.75H3.75V10H5V8.75Z" fill="#DB7103"/>
                                            <path d="M6.25 8.75H5V10H6.25V8.75Z" fill="#FFB039"/>
                                            <path d="M7.5 8.75H6.25V10H7.5V8.75Z" fill="#FFB039"/>
                                            <path d="M8.75 8.75H7.5V10H8.75V8.75Z" fill="#FFE85E"/>
                                            <path d="M10 8.75H8.75V10H10V8.75Z" fill="#FFE85E"/>
                                            <path d="M11.25 8.75H10V10H11.25V8.75Z" fill="#FFE85E"/>
                                            <path d="M12.5 8.75H11.25V10H12.5V8.75Z" fill="#FFE85E"/>
                                            <path d="M13.75 8.75H12.5V10H13.75V8.75Z" fill="#FFE85E"/>
                                            <path d="M15 8.75H13.75V10H15V8.75Z" fill="#FFFFFB"/>
                                            <path d="M16.25 8.75H15V10H16.25V8.75Z" fill="#FFB039"/>
                                            <path d="M17.5 8.75H16.25V10H17.5V8.75Z" fill="#FFE85E"/>
                                            <path d="M18.75 8.75H17.5V10H18.75V8.75Z" fill="#DB7103"/>
                                            <path d="M20 8.75H18.75V10H20V8.75Z" fill="black"/>
                                            <path d="M1.25 10H0V11.25H1.25V10Z" fill="black"/>
                                            <path d="M2.5 10H1.25V11.25H2.5V10Z" fill="#FFB039"/>
                                            <path d="M3.75 10H2.5V11.25H3.75V10Z" fill="#FFE85E"/>
                                            <path d="M5 10H3.75V11.25H5V10Z" fill="#DB7103"/>
                                            <path d="M6.25 10H5V11.25H6.25V10Z" fill="#FFB039"/>
                                            <path d="M7.5 10H6.25V11.25H7.5V10Z" fill="#FFB039"/>
                                            <path d="M8.75 10H7.5V11.25H8.75V10Z" fill="#FFE85E"/>
                                            <path d="M10 10H8.75V11.25H10V10Z" fill="#FFE85E"/>
                                            <path d="M11.25 10H10V11.25H11.25V10Z" fill="#FFE85E"/>
                                            <path d="M12.5 10H11.25V11.25H12.5V10Z" fill="#FFE85E"/>
                                            <path d="M13.75 10H12.5V11.25H13.75V10Z" fill="#FFE85E"/>
                                            <path d="M15 10H13.75V11.25H15V10Z" fill="#FFFFFB"/>
                                            <path d="M16.25 10H15V11.25H16.25V10Z" fill="#FFB039"/>
                                            <path d="M17.5 10H16.25V11.25H17.5V10Z" fill="#FFE85E"/>
                                            <path d="M18.75 10H17.5V11.25H18.75V10Z" fill="#DB7103"/>
                                            <path d="M20 10H18.75V11.25H20V10Z" fill="black"/>
                                            <path d="M1.25 11.2499H0V12.4999H1.25V11.2499Z" fill="black"/>
                                            <path d="M2.5 11.2499H1.25V12.4999H2.5V11.2499Z" fill="#FFB039"/>
                                            <path d="M3.75 11.2499H2.5V12.4999H3.75V11.2499Z" fill="#FFE85E"/>
                                            <path d="M5 11.2499H3.75V12.4999H5V11.2499Z" fill="#DB7103"/>
                                            <path d="M6.25 11.2499H5V12.4999H6.25V11.2499Z" fill="#FFB039"/>
                                            <path d="M7.5 11.2499H6.25V12.4999H7.5V11.2499Z" fill="#FFE85E"/>
                                            <path d="M8.75 11.2499H7.5V12.4999H8.75V11.2499Z" fill="#FFE85E"/>
                                            <path d="M10 11.2499H8.75V12.4999H10V11.2499Z" fill="#FFE85E"/>
                                            <path d="M11.25 11.2499H10V12.4999H11.25V11.2499Z" fill="#FFE85E"/>
                                            <path d="M12.5 11.2499H11.25V12.4999H12.5V11.2499Z" fill="#FFE85E"/>
                                            <path d="M13.75 11.2499H12.5V12.4999H13.75V11.2499Z" fill="#FFE85E"/>
                                            <path d="M15 11.2499H13.75V12.4999H15V11.2499Z" fill="#FFFFFB"/>
                                            <path d="M16.25 11.2499H15V12.4999H16.25V11.2499Z" fill="#FFB039"/>
                                            <path d="M17.5 11.2499H16.25V12.4999H17.5V11.2499Z" fill="#FFE85E"/>
                                            <path d="M18.75 11.2499H17.5V12.4999H18.75V11.2499Z" fill="#DB7103"/>
                                            <path d="M20 11.2499H18.75V12.4999H20V11.2499Z" fill="black"/>
                                            <path d="M1.25 12.5H0V13.75H1.25V12.5Z" fill="black"/>
                                            <path d="M2.5 12.5H1.25V13.75H2.5V12.5Z" fill="#FFB039"/>
                                            <path d="M3.75 12.5H2.5V13.75H3.75V12.5Z" fill="#FFE85E"/>
                                            <path d="M5 12.5H3.75V13.75H5V12.5Z" fill="#FFE85E"/>
                                            <path d="M6.25 12.5H5V13.75H6.25V12.5Z" fill="#DB7103"/>
                                            <path d="M7.5 12.5H6.25V13.75H7.5V12.5Z" fill="#FFE85E"/>
                                            <path d="M8.75 12.5H7.5V13.75H8.75V12.5Z" fill="#FFE85E"/>
                                            <path d="M10 12.5H8.75V13.75H10V12.5Z" fill="#FFE85E"/>
                                            <path d="M11.25 12.5H10V13.75H11.25V12.5Z" fill="#FFE85E"/>
                                            <path d="M12.5 12.5H11.25V13.75H12.5V12.5Z" fill="#FFE85E"/>
                                            <path d="M13.75 12.5H12.5V13.75H13.75V12.5Z" fill="#FFFFFB"/>
                                            <path d="M15 12.5H13.75V13.75H15V12.5Z" fill="#FFB039"/>
                                            <path d="M16.25 12.5H15V13.75H16.25V12.5Z" fill="#FFE85E"/>
                                            <path d="M17.5 12.5H16.25V13.75H17.5V12.5Z" fill="#FFE85E"/>
                                            <path d="M18.75 12.5H17.5V13.75H18.75V12.5Z" fill="#DB7103"/>
                                            <path d="M20 12.5H18.75V13.75H20V12.5Z" fill="black"/>
                                            <path d="M2.5 13.7501H1.25V15.0001H2.5V13.7501Z" fill="black"/>
                                            <path d="M3.75 13.7501H2.5V15.0001H3.75V13.7501Z" fill="#FFB039"/>
                                            <path d="M5 13.7501H3.75V15.0001H5V13.7501Z" fill="#FFE85E"/>
                                            <path d="M6.25 13.7501H5V15.0001H6.25V13.7501Z" fill="#DB7103"/>
                                            <path d="M7.5 13.7501H6.25V15.0001H7.5V13.7501Z" fill="#DB7103"/>
                                            <path d="M8.75 13.7501H7.5V15.0001H8.75V13.7501Z" fill="#FFE85E"/>
                                            <path d="M10 13.7501H8.75V15.0001H10V13.7501Z" fill="#FFFFFB"/>
                                            <path d="M11.25 13.7501H10V15.0001H11.25V13.7501Z" fill="#FFFFFB"/>
                                            <path d="M12.5 13.7501H11.25V15.0001H12.5V13.7501Z" fill="#FFFFFB"/>
                                            <path d="M13.75 13.7501H12.5V15.0001H13.75V13.7501Z" fill="#FFB039"/>
                                            <path d="M15 13.7501H13.75V15.0001H15V13.7501Z" fill="#FFB039"/>
                                            <path d="M16.25 13.7501H15V15.0001H16.25V13.7501Z" fill="#FFE85E"/>
                                            <path d="M17.5 13.7501H16.25V15.0001H17.5V13.7501Z" fill="#DB7103"/>
                                            <path d="M18.75 13.7501H17.5V15.0001H18.75V13.7501Z" fill="black"/>
                                            <path d="M2.5 15H1.25V16.25H2.5V15Z" fill="black"/>
                                            <path d="M3.75 15H2.5V16.25H3.75V15Z" fill="#FFB039"/>
                                            <path d="M5 15H3.75V16.25H5V15Z" fill="#FFE85E"/>
                                            <path d="M6.25 15H5V16.25H6.25V15Z" fill="#FFE85E"/>
                                            <path d="M7.5 15H6.25V16.25H7.5V15Z" fill="#FFE85E"/>
                                            <path d="M8.75 15H7.5V16.25H8.75V15Z" fill="#FFB039"/>
                                            <path d="M10 15H8.75V16.25H10V15Z" fill="#FFB039"/>
                                            <path d="M11.25 15H10V16.25H11.25V15Z" fill="#FFB039"/>
                                            <path d="M12.5 15H11.25V16.25H12.5V15Z" fill="#FFB039"/>
                                            <path d="M13.75 15H12.5V16.25H13.75V15Z" fill="#FFE85E"/>
                                            <path d="M15 15H13.75V16.25H15V15Z" fill="#FFE85E"/>
                                            <path d="M16.25 15H15V16.25H16.25V15Z" fill="#FFE85E"/>
                                            <path d="M17.5 15H16.25V16.25H17.5V15Z" fill="#DB7103"/>
                                            <path d="M18.75 15H17.5V16.25H18.75V15Z" fill="black"/>
                                            <path d="M3.75 16.25H2.5V17.5H3.75V16.25Z" fill="black"/>
                                            <path d="M5 16.25H3.75V17.5H5V16.25Z" fill="#FFB039"/>
                                            <path d="M6.25 16.25H5V17.5H6.25V16.25Z" fill="#FFB039"/>
                                            <path d="M7.5 16.25H6.25V17.5H7.5V16.25Z" fill="#FFE85E"/>
                                            <path d="M8.75 16.25H7.5V17.5H8.75V16.25Z" fill="#FFE85E"/>
                                            <path d="M10 16.25H8.75V17.5H10V16.25Z" fill="#FFE85E"/>
                                            <path d="M11.25 16.25H10V17.5H11.25V16.25Z" fill="#FFE85E"/>
                                            <path d="M12.5 16.25H11.25V17.5H12.5V16.25Z" fill="#FFE85E"/>
                                            <path d="M13.75 16.25H12.5V17.5H13.75V16.25Z" fill="#FFE85E"/>
                                            <path d="M15 16.25H13.75V17.5H15V16.25Z" fill="#DB7103"/>
                                            <path d="M16.25 16.25H15V17.5H16.25V16.25Z" fill="#DB7103"/>
                                            <path d="M17.5 16.25H16.25V17.5H17.5V16.25Z" fill="black"/>
                                            <path d="M5 17.5H3.75V18.75H5V17.5Z" fill="black"/>
                                            <path d="M6.25 17.5H5V18.75H6.25V17.5Z" fill="black"/>
                                            <path d="M7.5 17.5H6.25V18.75H7.5V17.5Z" fill="#FFB039"/>
                                            <path d="M8.75 17.5H7.5V18.75H8.75V17.5Z" fill="#FFB039"/>
                                            <path d="M10 17.5H8.75V18.75H10V17.5Z" fill="#FFB039"/>
                                            <path d="M11.25 17.5H10V18.75H11.25V17.5Z" fill="#FFB039"/>
                                            <path d="M12.5 17.5H11.25V18.75H12.5V17.5Z" fill="#FFB039"/>
                                            <path d="M13.75 17.5H12.5V18.75H13.75V17.5Z" fill="#FFB039"/>
                                            <path d="M15 17.5H13.75V18.75H15V17.5Z" fill="black"/>
                                            <path d="M16.25 17.5H15V18.75H16.25V17.5Z" fill="black"/>
                                            <path d="M7.5 18.75H6.25V20H7.5V18.75Z" fill="black"/>
                                            <path d="M8.75 18.75H7.5V20H8.75V18.75Z" fill="black"/>
                                            <path d="M10 18.75H8.75V20H10V18.75Z" fill="black"/>
                                            <path d="M11.25 18.75H10V20H11.25V18.75Z" fill="black"/>
                                            <path d="M12.5 18.75H11.25V20H12.5V18.75Z" fill="black"/>
                                            <path d="M13.75 18.75H12.5V20H13.75V18.75Z" fill="black"/>
                                        </svg>
                                        <span className={styles.iconCoin}>{quest.cost || 0}</span>
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </div>

                <div className={styles.section}>
                    <h2 className={styles.sectionTitle}>История покупок</h2>
                    <div className={styles.filterContainer}>
                        <div className={styles.searchWrapper}>
                            <input
                                type="text"
                                placeholder="Поиск по названию товара..."
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                                className={styles.inputField}
                            />
                            <svg className={styles.searchIcon} width="16" height="16" viewBox="0 0 16 16" fill="none">
                                <path
                                    d="M11.435 10.063h-.723l-.256-.247a5.92 5.92 0 0 0 1.437-3.87 5.946 5.946 0 1 0-5.947 5.947 5.92 5.92 0 0 0 3.87-1.437l.247.256v.723L14.637 15 16 13.637l-4.565-4.574zm-5.489 0A4.375 4.375 0 1 1 10.32 5.946a4.381 4.381 0 0 1-4.375 4.375z"
                                    fill="#757575"/>
                            </svg>
                        </div>
                    </div>
                    {isLoading ? (
                        <div className={styles.loading}>
                            <div className={styles.spinner}></div>
                            Загрузка истории покупок...
                        </div>
                    ) : filteredPurchases.length === 0 ? (
                        <div className={styles.empty}>
                            Покупок не найдено
                            {searchTerm && (
                                <button
                                    onClick={() => setSearchTerm('')}
                                    className={styles.passwordButton}
                                >
                                    Сбросить поиск
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className={styles.purchasesList}>
                            {filteredPurchases.map((operation) => {
                                const info = getPurchaseInfo(operation);
                                return (
                                    <div key={operation.operationsId} className={styles.purchaseItem}>
                                        <div className={styles.purchaseImage}>
                                            {info.itemId ? (
                                                <Link to={`/shop/item/${info.itemId}`}>
                                                    <img
                                                        src={operation.imageUrl}
                                                        alt={info.itemName}
                                                        className={styles.itemImage}
                                                    />
                                                </Link>
                                            ) : (
                                                <img
                                                    src={operation.imageUrl}
                                                    alt={info.itemName}
                                                    className={styles.itemImage}
                                                />
                                            )}
                                        </div>
                                        <div className={styles.purchaseInfo}>
                                            <div className={styles.purchaseTitle}>
                                                {info.itemId ? (
                                                    <Link
                                                        to={`/shop/item/${info.itemId}`}
                                                        className={styles.itemLink}
                                                    >
                                                        <span className={styles.itemName}>
                                                            {info.itemName}
                                                        </span>
                                                    </Link>
                                                ) : (
                                                    <span className={styles.itemName}>
                                                        {info.itemName}
                                                    </span>
                                                )}
                                            </div>
                                            <div className={styles.purchaseMeta}>
                                                <span className={styles.purchaseDate}>{info.date}</span>
                                                {getStatusBadge(info.status)}
                                            </div>
                                        </div>
                                        <div className={styles.purchaseCost}>
                                            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                                                <path
                                                    d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 18C5.58 18 2 14.42 2 10C2 5.58 5.58 2 10 2C14.42 2 18 5.58 18 10C18 14.42 14.42 18 10 18Z"
                                                    fill="#f57c00"/>
                                                <path
                                                    d="M10 4C7.79 4 6 5.79 6 8C6 10.21 7.79 12 10 12C12.21 12 14 10.21 14 8C14 5.79 12.21 4 10 4ZM10 10C8.9 10 8 9.1 8 8C8 6.9 8.9 6 10 6C11.1 6 12 6.9 12 8C12 9.1 11.1 10 10 10Z"
                                                    fill="#f57c00"/>
                                            </svg>
                                            <span>{info.cost}</span>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    )}
                </div>

                {/* Сообщения об ошибках/успехе */}
                {error && <div className={styles.errorMessage}>{error}</div>}
                {success && <div className={styles.successMessage}>{success}</div>}
            </div>
        </div>
    );
};

export default PersonalAccountPage;