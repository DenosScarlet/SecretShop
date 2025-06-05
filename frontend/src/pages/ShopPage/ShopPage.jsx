import React, { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import { FaSearch, FaFilter, FaSortAmountDown, FaSortAmountDownAlt } from 'react-icons/fa';

const typeOptions = [
    { value: '', label: 'Все товары' },
    { value: 'MERCH', label: 'Мерч' },
    { value: 'DEVICES', label: 'Устройства' },
    { value: 'ACCESSORIES', label: 'Аксессуары' },
    { value: 'COUPONS', label: 'Купоны' }
];

const sortOptions = [
    { value: 'price_asc', label: 'Цена (по возрастанию)', icon: <FaSortAmountDown /> },
    { value: 'price_desc', label: 'Цена (по убыванию)', icon: <FaSortAmountDownAlt /> },
    { value: 'name_asc', label: 'Название (А-Я)', icon: <FaSortAmountDown /> },
    { value: 'name_desc', label: 'Название (Я-А)', icon: <FaSortAmountDownAlt /> },
    { value: 'newest', label: 'Сначала новые', icon: <FaSortAmountDown /> },
    { value: 'popular', label: 'Популярные', icon: <FaSortAmountDownAlt /> }
];

const ShopPage = () => {
    const [items, setItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [showFilters, setShowFilters] = useState(false);
    const [filters, setFilters] = useState({
        search: '',
        type: '',
        minPrice: '',
        maxPrice: '',
        inStockOnly: true
    });
    const [sortOption, setSortOption] = useState('price_asc');
    const { user } = useAuth();

    useEffect(() => {
        fetchItems();
    }, []);

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
                        imageUrl,
                        createdAt: item.createdAt || new Date().toISOString() // Добавляем дату создания, если её нет
                    };
                } catch (error) {
                    console.error(`Ошибка загрузки изображения для товара ${item.itemId}:`, error);
                    return {
                        ...item,
                        imageUrl: 'https://via.placeholder.com/150',
                        createdAt: item.createdAt || new Date().toISOString()
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

    const applyFiltersAndSort = useCallback(() => {
        let result = [...items];

        // Применяем фильтры
        if (filters.search) {
            const searchLower = filters.search.toLowerCase();
            result = result.filter(item =>
                item.itemName.toLowerCase().includes(searchLower) ||
                item.description.toLowerCase().includes(searchLower) ||
                (item.tags && item.tags.some(tag => tag.toLowerCase().includes(searchLower))))
        }

        if (filters.type) {
            result = result.filter(item => item.type === filters.type);
        }

        if (filters.minPrice) {
            const min = Number(filters.minPrice);
            result = result.filter(item => Number(item.cost) >= min);
        }

        if (filters.maxPrice) {
            const max = Number(filters.maxPrice);
            result = result.filter(item => Number(item.cost) <= max);
        }

        if (filters.inStockOnly) {
            result = result.filter(item => item.count > 0);
        }

        // Применяем сортировку
        switch (sortOption) {
            case 'price_asc':
                result.sort((a, b) => Number(a.cost) - Number(b.cost));
                break;
            case 'price_desc':
                result.sort((a, b) => Number(b.cost) - Number(a.cost));
                break;
            case 'name_asc':
                result.sort((a, b) => a.itemName.localeCompare(b.itemName));
                break;
            case 'name_desc':
                result.sort((a, b) => b.itemName.localeCompare(a.itemName));
                break;
            case 'newest':
                result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                break;
            case 'popular':
                // Предполагаем, что у товара есть поле popularity
                result.sort((a, b) => (b.popularity || 0) - (a.popularity || 0));
                break;
            default:
                break;
        }

        setFilteredItems(result);
    }, [items, filters, sortOption]);

    useEffect(() => {
        if (items.length > 0) {
            applyFiltersAndSort();
        }
    }, [items, filters, sortOption, applyFiltersAndSort]);

    const handleFilterChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFilters(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const resetFilters = () => {
        setFilters({
            search: '',
            type: '',
            minPrice: '',
            maxPrice: '',
            inStockOnly: true
        });
        setSortOption('price_asc');
    };

    const getTypeLabel = (typeValue) => {
        const type = typeOptions.find(t => t.value === typeValue);
        return type ? type.label : typeValue;
    };

    const selectedSortOption = sortOptions.find(opt => opt.value === sortOption);

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <h1 style={{ marginBottom: '1rem', textAlign: 'center' }}>Магазин</h1>

                {/* Поисковая строка */}
                <div className={styles.searchBar} style={{
                    display: 'flex',
                    marginBottom: '1rem',
                    gap: '10px'
                }}>
                    <div style={{ position: 'relative', flexGrow: 1 }}>
                        <input
                            type="text"
                            name="search"
                            value={filters.search}
                            onChange={handleFilterChange}
                            placeholder="Поиск по названию, описанию или тегам..."
                            className={styles.editInput}
                            style={{ width: '100%', paddingLeft: '35px' }}
                        />
                        <FaSearch style={{
                            position: 'absolute',
                            left: '10px',
                            top: '50%',
                            transform: 'translateY(-50%)',
                            color: '#888'
                        }} />
                    </div>

                    <button
                        onClick={() => setShowFilters(!showFilters)}
                        className={styles.filterToggle}
                        style={{
                            display: 'flex',
                            alignItems: 'center',
                            gap: '5px',
                            padding: '8px 15px',
                            background: '#f0f0f0',
                            border: '1px solid #ddd',
                            borderRadius: '4px',
                            cursor: 'pointer'
                        }}
                    >
                        <FaFilter /> Фильтры
                    </button>
                </div>

                {/* Расширенные фильтры */}
                {showFilters && (
                    <div className={styles.advancedFilters} style={{
                        backgroundColor: '#f9f9f9',
                        padding: '15px',
                        borderRadius: '8px',
                        marginBottom: '1rem',
                        border: '1px solid #eee'
                    }}>
                        <div style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                            gap: '15px',
                            marginBottom: '15px'
                        }}>
                            <div>
                                <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                                    Категория
                                </label>
                                <select
                                    name="type"
                                    value={filters.type}
                                    onChange={handleFilterChange}
                                    className={styles.editSelect}
                                    style={{ width: '100%' }}
                                >
                                    {typeOptions.map(option => (
                                        <option key={option.value} value={option.value}>{option.label}</option>
                                    ))}
                                </select>
                            </div>

                            <div>
                                <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                                    Минимальная цена
                                </label>
                                <input
                                    type="number"
                                    name="minPrice"
                                    min="0"
                                    value={filters.minPrice}
                                    onChange={handleFilterChange}
                                    placeholder="От"
                                    className={styles.editInput}
                                    style={{ width: '100%' }}
                                />
                            </div>

                            <div>
                                <label style={{ display: 'block', marginBottom: '5px', fontWeight: '500' }}>
                                    Максимальная цена
                                </label>
                                <input
                                    type="number"
                                    name="maxPrice"
                                    min="0"
                                    value={filters.maxPrice}
                                    onChange={handleFilterChange}
                                    placeholder="До"
                                    className={styles.editInput}
                                    style={{ width: '100%' }}
                                />
                            </div>

                            <div style={{ display: 'flex', alignItems: 'flex-end' }}>
                                <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                                    <input
                                        type="checkbox"
                                        name="inStockOnly"
                                        checked={filters.inStockOnly}
                                        onChange={handleFilterChange}
                                        style={{ marginRight: '8px' }}
                                    />
                                    Только в наличии
                                </label>
                            </div>
                        </div>

                        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px' }}>
                            <button
                                onClick={resetFilters}
                                className={styles.resetButton}
                                style={{
                                    padding: '8px 15px',
                                    background: '#f5f5f5',
                                    border: '1px solid #ddd',
                                    borderRadius: '4px',
                                    cursor: 'pointer'
                                }}
                            >
                                Сбросить фильтры
                            </button>
                        </div>
                    </div>
                )}

                {/* Сортировка */}
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '1rem',
                    flexWrap: 'wrap',
                    gap: '10px'
                }}>
                    <div>
                        Найдено товаров: <strong>{filteredItems.length}</strong>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <span>Сортировка:</span>
                        <div style={{ position: 'relative' }}>
                            <select
                                value={sortOption}
                                onChange={(e) => setSortOption(e.target.value)}
                                className={styles.editSelect}
                                style={{
                                    minWidth: '200px',
                                    paddingRight: '30px',
                                    appearance: 'none'
                                }}
                            >
                                {sortOptions.map(option => (
                                    <option key={option.value} value={option.value}>
                                        {option.label}
                                    </option>
                                ))}
                            </select>
                            <div style={{
                                position: 'absolute',
                                right: '10px',
                                top: '50%',
                                transform: 'translateY(-50%)',
                                pointerEvents: 'none'
                            }}>
                                {selectedSortOption?.icon}
                            </div>
                        </div>
                    </div>
                </div>

                {isLoading ? (
                    <div style={{ textAlign: 'center', padding: '2rem' }}>Загрузка товаров...</div>
                ) : (
                    <div className={styles.itemGrid}>
                        {filteredItems.length === 0 ? (
                            <div style={{
                                textAlign: 'center',
                                padding: '2rem',
                                gridColumn: '1 / -1',
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                gap: '15px'
                            }}>
                                <div>Товары не найдены</div>
                                <button
                                    onClick={resetFilters}
                                    className={styles.resetButton}
                                    style={{
                                        padding: '8px 15px',
                                        background: '#FEB238',
                                        color: 'white',
                                        border: 'none',
                                        borderRadius: '4px',
                                        cursor: 'pointer'
                                    }}
                                >
                                    Сбросить фильтры
                                </button>
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
                                        {item.tags && item.tags.length > 0 && (
                                            <div className={styles.tagsContainer} style={{
                                                display: 'flex',
                                                flexWrap: 'wrap',
                                                gap: '5px',
                                                marginTop: '8px'
                                            }}>
                                                {item.tags.slice(0, 3).map(tag => (
                                                    <span key={tag} className={styles.tag} style={{
                                                        padding: '3px 8px',
                                                        background: '#f0f0f0',
                                                        borderRadius: '12px',
                                                        fontSize: '12px'
                                                    }}>
                                                        {tag}
                                                    </span>
                                                ))}
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