import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';

// Константы для типов товаров
const typeOptions = [
    { value: '', label: 'Все типы' },
    { value: 'MERCH', label: 'Мерч' },
    { value: 'DEVICES', label: 'Устройства' },
    { value: 'ACCESSORIES', label: 'Аксессуары' },
    { value: 'COUPONS', label: 'Купоны' }
];

// Функция для получения читаемого названия типа
const getTypeLabel = (typeValue) => {
    const type = typeOptions.find(t => t.value === typeValue);
    return type ? type.label : typeValue;
};

const ItemListPage = () => {
    const [items, setItems] = useState([]);
    const [filteredItems, setFilteredItems] = useState([]);
    const [filter, setFilter] = useState('');
    const [selectedType, setSelectedType] = useState('');
    const [minPrice, setMinPrice] = useState('');
    const [maxPrice, setMaxPrice] = useState('');
    const [editingId, setEditingId] = useState(null);
    const [editedData, setEditedData] = useState({});
    const [newFile, setNewFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const fileInputRef = useRef(null);

    useEffect(() => {
        fetchItems();
    }, []);

    useEffect(() => {
        applyFilters();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [filter, selectedType, minPrice, maxPrice, items]);

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
        const lowerFilter = filter.trim().toLowerCase();
        const filtered = items.filter(item => {
            const matchesText =
                item.itemName.toLowerCase().includes(lowerFilter) ||
                item.description.toLowerCase().includes(lowerFilter) ||
                item.owner.toLowerCase().includes(lowerFilter);

            const matchesType = selectedType ? item.type === selectedType : true;

            const cost = Number(item.cost);
            const min = minPrice !== '' ? Number(minPrice) : null;
            const max = maxPrice !== '' ? Number(maxPrice) : null;

            const matchesMinPrice = min !== null ? cost >= min : true;
            const matchesMaxPrice = max !== null ? cost <= max : true;

            return matchesText && matchesType && matchesMinPrice && matchesMaxPrice;
        });
        setFilteredItems(filtered);
    };

    const handleDelete = async (itemId) => {
        if (window.confirm('Вы уверены, что хотите удалить этот товар? Все связанные изображения также будут удалены.')) {
            try {
                await shopApi.deleteItemImage(itemId);
                await shopApi.deleteItem(itemId);
                fetchItems();
            } catch (error) {
                console.error('Ошибка при удалении товара:', error);
                alert('Ошибка удаления товара: ' + (error.response?.data || error.message));
            }
        }
    };

    const startEditing = (item) => {
        setEditingId(item.itemId);
        setEditedData({
            itemName: item.itemName,
            description: item.description,
            owner: item.owner,
            type: item.type,
            cost: item.cost,
            count: item.count
        });
        setNewFile(null);
        setPreviewUrl(null);
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditedData(prev => ({
            ...prev,
            [name]: name === 'cost' || name === 'count'
                ? parseInt(value, 10) || 0
                : value
        }));
    };

    const handleFileUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            setNewFile(file);
            if (previewUrl) {
                URL.revokeObjectURL(previewUrl);
            }
            const url = URL.createObjectURL(file);
            setPreviewUrl(url);
        }
    };

    const triggerFileInput = () => {
        fileInputRef.current.click();
    };

    const saveChanges = async () => {
        try {
            await shopApi.updateItem(editingId, editedData);

            if (newFile) {
                try {
                    await shopApi.deleteItemImage(editingId);
                } catch (deleteError) {
                    console.warn('Ошибка удаления старого изображения:', deleteError);
                }
                await shopApi.uploadItemImage(editingId, newFile);
            }

            setEditingId(null);
            setNewFile(null);
            if (previewUrl) {
                URL.revokeObjectURL(previewUrl);
                setPreviewUrl(null);
            }
            fetchItems();
            alert('Товар успешно обновлён!');
        } catch (error) {
            console.error('Ошибка обновления:', error);
            alert('Ошибка обновления товара: ' + (error.response?.data || error.message));
        }
    };

    const cancelEditing = () => {
        setEditingId(null);
        setNewFile(null);
        if (previewUrl) {
            URL.revokeObjectURL(previewUrl);
            setPreviewUrl(null);
        }
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <div className={styles.header} style={{ marginBottom: '1rem' }}>
                    <Link to="/shop/item/create" className={styles.createButton}>
                        Создать новый товар
                    </Link>
                </div>

                {/* Фильтрация */}
                <div className={styles.filterContainer} style={{ marginBottom: '1rem', display: 'flex', flexWrap: 'wrap', gap: '1rem' }}>
                    <input
                        type="text"
                        placeholder="Поиск по названию, описанию или владельцу"
                        value={filter}
                        onChange={(e) => setFilter(e.target.value)}
                        className={styles.editInput}
                        style={{ flexGrow: 1, minWidth: '200px' }}
                    />

                    <select
                        value={selectedType}
                        onChange={(e) => setSelectedType(e.target.value)}
                        className={styles.editSelect}
                        style={{ minWidth: '150px' }}
                    >
                        {typeOptions.map(option => (
                            <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                    </select>

                    <input
                        type="number"
                        min="0"
                        placeholder="Мин. цена"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                        className={styles.editInput}
                        style={{ width: '120px' }}
                    />

                    <input
                        type="number"
                        min="0"
                        placeholder="Макс. цена"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                        className={styles.editInput}
                        style={{ width: '120px' }}
                    />
                </div>

                {isLoading ? (
                    <div>Загрузка...</div>
                ) : (
                    <div className={styles.questList}>
                        {filteredItems.length === 0 ? (
                            <div>Нет товаров, соответствующих фильтру.</div>
                        ) : (
                            filteredItems.map(item => (
                                <div key={item.itemId} className={styles.questItem}>
                                    <div style={{ display: 'flex', gap: '20px', width: '100%' }}>
                                        <div style={{ flexShrink: 0 }}>
                                            {item.imageUrl && (
                                                <img
                                                    src={item.imageUrl}
                                                    alt="Изображение товара"
                                                    style={{
                                                        width: '150px',
                                                        height: '150px',
                                                        objectFit: 'cover',
                                                        borderRadius: '8px',
                                                        border: '1px solid #ddd'
                                                    }}
                                                    onError={(e) => {
                                                        e.target.src = 'https://via.placeholder.com/150';
                                                    }}
                                                />
                                            )}
                                        </div>

                                        {editingId === item.itemId ? (
                                            <div className={styles.editForm} style={{ flexGrow: 1 }}>
                                                <input
                                                    name="itemName"
                                                    value={editedData.itemName}
                                                    onChange={handleEditChange}
                                                    className={styles.editInput}
                                                    placeholder="Название товара"
                                                />
                                                <textarea
                                                    name="description"
                                                    value={editedData.description}
                                                    onChange={handleEditChange}
                                                    className={styles.editTextarea}
                                                    placeholder="Описание товара"
                                                />
                                                <input
                                                    name="owner"
                                                    value={editedData.owner}
                                                    onChange={handleEditChange}
                                                    className={styles.editInput}
                                                    placeholder="Владелец"
                                                />
                                                <select
                                                    name="type"
                                                    value={editedData.type}
                                                    onChange={handleEditChange}
                                                    className={styles.editSelect}
                                                >
                                                    {typeOptions.slice(1).map(option => (
                                                        <option key={option.value} value={option.value}>
                                                            {option.label}
                                                        </option>
                                                    ))}
                                                </select>
                                                <input
                                                    type="number"
                                                    name="cost"
                                                    value={editedData.cost}
                                                    onChange={handleEditChange}
                                                    className={styles.editInput}
                                                    placeholder="Цена"
                                                    min="0"
                                                />
                                                <input
                                                    type="number"
                                                    name="count"
                                                    value={editedData.count}
                                                    onChange={handleEditChange}
                                                    className={styles.editInput}
                                                    placeholder="Количество"
                                                    min="1"
                                                />

                                                <div className={styles.fileUploadSection} style={{ marginTop: '10px' }}>
                                                    <button
                                                        type="button"
                                                        onClick={triggerFileInput}
                                                        className={styles.fileUploadButton}
                                                    >
                                                        Заменить изображение
                                                    </button>
                                                    <input
                                                        type="file"
                                                        ref={fileInputRef}
                                                        onChange={handleFileUpload}
                                                        accept="image/*"
                                                        style={{ display: 'none' }}
                                                    />

                                                    {previewUrl && (
                                                        <div className={styles.previewContainer} style={{ marginTop: '10px' }}>
                                                            <img
                                                                src={previewUrl}
                                                                alt="Предпросмотр нового изображения"
                                                                className={styles.previewImage}
                                                                style={{ maxWidth: '150px', maxHeight: '150px', borderRadius: '8px', border: '1px solid #ddd' }}
                                                            />
                                                        </div>
                                                    )}
                                                </div>
                                            </div>
                                        ) : (
                                            <div className={styles.questInfo} style={{ flexGrow: 1 }}>
                                                <h3>{item.itemName}</h3>
                                                <p>{item.description}</p>
                                                <div className={styles.details} style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
                                                    <span><b>Владелец:</b> {item.owner}</span>
                                                    <span><b>Тип:</b> {getTypeLabel(item.type)}</span>
                                                    <span><b>Цена:</b> {item.cost}</span>
                                                    <span><b>Количество:</b> {item.count}</span>
                                                </div>
                                            </div>
                                        )}
                                    </div>

                                    <div className={styles.actions} style={{ marginTop: '10px' }}>
                                        {editingId === item.itemId ? (
                                            <>
                                                <button
                                                    onClick={saveChanges}
                                                    className={styles.saveButton}
                                                    style={{ marginRight: '10px' }}
                                                >
                                                    Сохранить
                                                </button>
                                                <button
                                                    onClick={cancelEditing}
                                                    className={styles.deleteButton}
                                                >
                                                    Отмена
                                                </button>
                                            </>
                                        ) : (
                                            <>
                                                <button
                                                    onClick={() => startEditing(item)}
                                                    className={styles.editButton}
                                                    style={{ marginRight: '10px' }}
                                                >
                                                    Редактировать
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(item.itemId)}
                                                    className={styles.deleteButton}
                                                >
                                                    Удалить
                                                </button>
                                            </>
                                        )}
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ItemListPage;
