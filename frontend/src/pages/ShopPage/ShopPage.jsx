import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';

const typeOptions = [
    { value: '', label: 'Все товары' },
    { value: 'MERCH', label: 'Мерч' },
    { value: 'DEVICES', label: 'Устройства' },
    { value: 'ACCESSORIES', label: 'Аксессуары' },
    { value: 'COUPONS', label: 'Купоны' }
];

const ShopPage = () => {
    const [items, setItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [selectedType, setSelectedType] = useState('');
    const [isLoading, setIsLoading] = useState(true);
    const { user } = useAuth();

    useEffect(() => {
        fetchItems();
    }, []);

    useEffect(() => {
        applyFilters();
    }, [selectedType, items]);

    const fetchItems = async () => {
        setIsLoading(true);
        try {
            const response = await shopApi.getAllItems();
            const itemsWithImageUrls = await Promise.all(response.data.map(async (item) => {
                try {
                    const imageResponse = await shopApi.downloadItemImage(item.itemId, `${item.itemId}.jpg`);
                    const imageUrl = URL.createObjectURL(new Blob([imageResponse.data]));
                    return {
                        ...item,
                        imageUrl
                    };
                } catch (error) {
                    console.error(`Ошибка загрузки изображения для товара ${item.itemId}:`, error);
                    return {
                        ...item,
                        imageUrl: 'https://via.placeholder.com/150'
                    };
                }
            }));
            setItems(itemsWithImageUrls);
        } catch (error) {
            console.error('Ошибка при загрузке товаров:', error);
            alert('Ошибка загрузки товаров');
        } finally {
            setIsLoading(false);
        }
    };

    const applyFilters = () => {
        const filtered = items.filter(item => {
            const matchesType = selectedType ? item.type === selectedType : true;
            const inStock = item.count > 0;
            return matchesType && inStock;
        });
        setFilteredItems(filtered);
    };

    const getTypeLabel = (typeValue) => {
        const type = typeOptions.find(t => t.value === typeValue);
        return type ? type.label : typeValue;
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <h1 style={{ marginBottom: '1rem', textAlign: 'center' }}>Магазин</h1>

                {/* Фильтр по типу */}
                <div className={styles.filterContainer} style={{
                    marginBottom: '1rem',
                    display: 'flex',
                    justifyContent: 'center',
                    alignItems: 'center',
                    gap: '10px'
                }}>
                    <span>Фильтр:</span>
                    <select
                        value={selectedType}
                        onChange={(e) => setSelectedType(e.target.value)}
                        className={styles.editSelect}
                        style={{ minWidth: '200px' }}
                    >
                        {typeOptions.map(option => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                    </select>
                </div>

                {isLoading ? (
                    <div style={{ textAlign: 'center', padding: '2rem' }}>Загрузка товаров...</div>
                ) : (
                    <div className={styles.itemGrid}>
                        {filteredItems.length === 0 ? (
                            <div style={{ textAlign: 'center', padding: '2rem', gridColumn: '1 / -1' }}>
                                Нет доступных товаров
                            </div>
                        ) : (
                            filteredItems.map(item => (
                                <Link
                                    key={item.itemId}
                                    to={`/shop/item/${item.itemId}`}
                                    className={styles.itemCard}
                                >
                                    <div className={styles.cardImage}>
                                        <img
                                            src={item.imageUrl}
                                            alt={item.itemName}
                                            onError={(e) => {
                                                e.target.src = 'https://via.placeholder.com/150';
                                            }}
                                        />
                                    </div>
                                    <div className={styles.cardContent}>
                                        <h3 className={styles.cardTitle}>{item.itemName}</h3>
                                        <div className={styles.cardPrice}>
                                            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
                                                <path d="M10 0C4.48 0 0 4.48 0 10C0 15.52 4.48 20 10 20C15.52 20 20 15.52 20 10C20 4.48 15.52 0 10 0ZM10 18C5.58 18 2 14.42 2 10C2 5.58 5.58 2 10 2C14.42 2 18 5.58 18 10C18 14.42 14.42 18 10 18Z" fill="#FFD700"/>
                                                <path d="M10 4C7.79 4 6 5.79 6 8C6 10.21 7.79 12 10 12C12.21 12 14 10.21 14 8C14 5.79 12.21 4 10 4ZM10 10C8.9 10 8 9.1 8 8C8 6.9 8.9 6 10 6C11.1 6 12 6.9 12 8C12 9.1 11.1 10 10 10Z" fill="#FFD700"/>
                                            </svg>
                                            <span>{item.cost}</span>
                                        </div>
                                        {item.count <= 5 && item.count > 0 && (
                                            <div className={styles.lowStock}>
                                                Осталось: {item.count} шт.
                                            </div>
                                        )}
                                    </div>
                                </Link>
                            ))
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ShopPage;