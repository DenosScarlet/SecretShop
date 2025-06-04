import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

const ItemPage = () => {
    const { itemId } = useParams();
    const navigate = useNavigate();
    const [item, setItem] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [purchasing, setPurchasing] = useState(false);
    const { user } = useAuth();

    useEffect(() => {
        const fetchItem = async () => {
            setIsLoading(true);
            try {
                // Получаем данные товара
                const itemResponse = await shopApi.getItem(itemId);
                const itemData = itemResponse.data;

                // Получаем изображение товара
                try {
                    const imageResponse = await shopApi.downloadItemImage(
                        itemId,
                        `${itemId}.jpg`
                    );
                    const imageUrl = URL.createObjectURL(new Blob([imageResponse.data]));
                    setItem({
                        ...itemData,
                        imageUrl
                    });
                } catch (imageError) {
                    console.error('Ошибка загрузки изображения:', imageError);
                    setItem({
                        ...itemData,
                        imageUrl: 'https://via.placeholder.com/300'
                    });
                }
            } catch (error) {
                console.error('Ошибка при загрузке товара:', error);
                alert('Товар не найден');
                navigate('/shop');
            } finally {
                setIsLoading(false);
            }
        };

        fetchItem();
    }, [itemId, navigate]);

    const handlePurchase = async () => {
        if (!user?.userId) {
            alert('Для покупки товаров необходимо авторизоваться');
            return;
        }

        if (!window.confirm(`Вы уверены, что хотите приобрести "${item.itemName}" за ${item.cost} монет?`)) {
            return;
        }

        setPurchasing(true);
        try {
            await shopApi.purchaseItem(itemId);
            alert('Товар успешно приобретен!');
            navigate('/shop');
        } catch (error) {
            console.error('Ошибка при покупке товара:', error);
            const errorMessage = error.response?.data?.message || error.message;
            alert(`Ошибка при покупке: ${errorMessage}`);
        } finally {
            setPurchasing(false);
        }
    };

    const getTypeLabel = (typeValue) => {
        const typeOptions = [
            { value: 'MERCH', label: 'Мерч' },
            { value: 'DEVICES', label: 'Устройства' },
            { value: 'ACCESSORIES', label: 'Аксессуары' },
            { value: 'COUPONS', label: 'Купоны' }
        ];
        const type = typeOptions.find(t => t.value === typeValue);
        return type ? type.label : typeValue;
    };

    if (isLoading) {
        return (
            <div className={styles.pageContainer}>
                <div className={styles.formWrapper}>
                    <div style={{ textAlign: 'center', padding: '2rem' }}>
                        Загрузка товара...
                    </div>
                </div>
            </div>
        );
    }

    if (!item) {
        return (
            <div className={styles.pageContainer}>
                <div className={styles.formWrapper}>
                    <div style={{ textAlign: 'center', padding: '2rem' }}>
                        Товар не найден
                    </div>
                    <button
                        onClick={() => navigate('/shop')}
                        className={styles.saveButton}
                        style={{ margin: '0 auto' }}
                    >
                        Вернуться в магазин
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <button
                    onClick={() => navigate('/shop')}
                    className={styles.backButton}
                    style={{ marginBottom: '1rem' }}
                >
                    &larr; Назад в магазин
                </button>

                <div className={styles.itemDetail}>
                    <div className={styles.itemImage}>
                        <img
                            src={item.imageUrl}
                            alt={item.itemName}
                            onError={(e) => {
                                e.target.src = 'https://via.placeholder.com/300';
                            }}
                        />
                    </div>

                    <div className={styles.itemInfo}>
                        <h1 className={styles.itemTitle}>{item.itemName}</h1>
                        <div className={styles.itemType}>
                            Категория: <strong>{getTypeLabel(item.type)}</strong>
                        </div>

                        <div className={styles.itemPrice}>
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
                                <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 20C7.58 20 4 16.42 4 12C4 7.58 7.58 4 12 4C16.42 4 20 7.58 20 12C20 16.42 16.42 20 12 20Z" fill="#FFD700"/>
                                <path d="M12 5C9.24 5 7 7.24 7 10C7 12.76 9.24 15 12 15C14.76 15 17 12.76 17 10C17 7.24 14.76 5 12 5ZM12 13C10.34 13 9 11.66 9 10C9 8.34 10.34 7 12 7C13.66 7 15 8.34 15 10C15 11.66 13.66 13 12 13Z" fill="#FFD700"/>
                            </svg>
                            <span>{item.cost}</span>
                        </div>

                        <div className={styles.itemStock}>
                            {item.count > 0 ? (
                                <span className={styles.inStock}>В наличии: {item.count} шт.</span>
                            ) : (
                                <span className={styles.outOfStock}>Нет в наличии</span>
                            )}
                        </div>

                        <div className={styles.itemOwner}>
                            Продавец: <strong>{item.owner}</strong>
                        </div>

                        <p className={styles.itemDescription}>{item.description}</p>

                        <button
                            onClick={handlePurchase}
                            className={styles.purchaseButton}
                            disabled={purchasing || item.count <= 0}
                        >
                            {purchasing
                                ? 'Обработка покупки...'
                                : (item.count > 0 ? 'Купить сейчас' : 'Товар закончился')}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default ItemPage;