import React, { useState, useEffect } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import { keycloakApi } from '../../services/keycloakApi';
import { shopApi } from '../../services/api';
import { Link } from 'react-router-dom';
import styles from './PersonalAccountPage.module.css';
import Avatar from '../../components/Header/Avatar';

const PersonalAccountPage = () => {
    const { user, dtlUser, updateAvatar } = useAuth();
    const [avatarFile, setAvatarFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState('');
    const [avatarBlobUrl, setAvatarBlobUrl] = useState('');
    const [purchases, setPurchases] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    const [searchTerm, setSearchTerm] = useState('');

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
                const blob = new Blob([response.data], { type: response.headers['content-type'] });
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
                            return { ...operation, imageUrl };
                        } catch (error) {
                            console.error(`Ошибка загрузки изображения для операции ${operation.operationsId}:`, error);
                            return { ...operation, imageUrl: 'https://via.placeholder.com/50' };
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
            const blob = new Blob([avatarResponse.data], { type: avatarResponse.headers['content-type'] });
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
            { value: 'COMPLETE', label: 'Выдано', color: '#4caf50' },
            { value: 'IN_PROGRESS', label: 'Ожидает выдачи', color: '#ffeb3b' },
            { value: 'CANCEL', label: 'Отменено', color: '#f44336' },
        ].find(opt => opt.value === status) || { label: 'Неизвестно', color: '#ccc' };

        return (
            <span
                className={styles.statusBadge}
                style={{ backgroundColor: statusConfig.color }}
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
                                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                                        <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 18C5.58 18 2 14.42 2 10C2 5.58 5.58 2 10 2C14.42 2 18 5.58 18 10C18 14.42 14.42 18 10 18Z" fill="#f57c00"/>
                                        <path d="M10 4C7.79 4 6 5.79 6 8C6 10.21 7.79 12 10 12C12.21 12 14 10.21 14 8C14 5.79 12.21 4 10 4ZM10 10C8.9 10 8 9.1 8 8C8 6.9 8.9 6 10 6C11.1 6 12 6.9 12 8C12 9.1 11.1 10 10 10Z" fill="#f57c00"/>
                                    </svg>
                                    <span>{dtlUser?.balance || 0}</span>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                {/* История покупок */}
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
                                <path d="M11.435 10.063h-.723l-.256-.247a5.92 5.92 0 0 0 1.437-3.87 5.946 5.946 0 1 0-5.947 5.947 5.92 5.92 0 0 0 3.87-1.437l.247.256v.723L14.637 15 16 13.637l-4.565-4.574zm-5.489 0A4.375 4.375 0 1 1 10.32 5.946a4.381 4.381 0 0 1-4.375 4.375z" fill="#757575"/>
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
                                                <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 18C5.58 18 2 14.42 2 10C2 5.58 5.58 2 10 2C14.42 2 18 5.58 18 10C18 14.42 14.42 18 10 18Z" fill="#f57c00"/>
                                                <path d="M10 4C7.79 4 6 5.79 6 8C6 10.21 7.79 12 10 12C12.21 12 14 10.21 14 8C14 5.79 12.21 4 10 4ZM10 10C8.9 10 8 9.1 8 8C8 6.9 8.9 6 10 6C11.1 6 12 6.9 12 8C12 9.1 11.1 10 10 10Z" fill="#f57c00"/>
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