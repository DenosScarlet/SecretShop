import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';

// Константы для типов товаров
const typeOptions = [
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
    const [editingId, setEditingId] = useState(null);
    const [editedData, setEditedData] = useState({});
    const [newFile, setNewFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const fileInputRef = useRef(null);

    useEffect(() => {
        fetchItems();
    }, []);

    const fetchItems = async () => {
        setIsLoading(true);
        try {
            const response = await shopApi.getAllItems();
            setItems(response.data);
        } catch (error) {
            console.error('Ошибка при загрузке товаров:', error);
            alert('Ошибка загрузки товаров');
        } finally {
            setIsLoading(false);
        }
    };

    const handleDelete = async (itemId) => {
        if (window.confirm('Вы уверены, что хотите удалить этот товар? Все связанные изображения также будут удалены.')) {
            try {
                // 1. Удаляем изображение товара
                await shopApi.deleteItemImage(itemId);

                // 2. Удаляем сам товар
                await shopApi.deleteItem(itemId);

                // 3. Обновляем список
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
            const previewUrl = URL.createObjectURL(file);
            setPreviewUrl(previewUrl);
        }
    };

    const triggerFileInput = () => {
        fileInputRef.current.click();
    };

    const saveChanges = async () => {
        try {
            // 1. Обновляем данные товара
            await shopApi.updateItem(editingId, editedData);

            // 2. Если выбран новый файл
            if (newFile) {
                try {
                    // Удаляем старое изображение
                    await shopApi.deleteItemImage(editingId);
                } catch (deleteError) {
                    console.warn('Ошибка удаления старого изображения:', deleteError);
                }

                // Загружаем новое изображение
                await shopApi.uploadItemImage(editingId, newFile);
            }

            // 3. Обновляем список и сбрасываем режим редактирования
            setEditingId(null);
            fetchItems();
            alert('Товар успешно обновлён!');
        } catch (error) {
            console.error('Ошибка обновления:', error);
            alert('Ошибка обновления товара: ' + (error.response?.data || error.message));
        }
    };

    const cancelEditing = () => {
        setEditingId(null);
        // Освобождаем ресурс превью
        if (previewUrl) {
            URL.revokeObjectURL(previewUrl);
        }
    };

    return (
        <div className={styles.pageContainer}>
            <div className={styles.formWrapper}>
                <div className={styles.header}>
                    <Link to="/shop/item/create" className={styles.createButton}>
                        Создать новый товар
                    </Link>
                </div>

                {isLoading ? (
                    <div>Загрузка...</div>
                ) : (
                    <div className={styles.questList}>
                        {items.map(item => (
                            <div key={item.itemId} className={styles.questItem}>
                                {editingId === item.itemId ? (
                                    <div className={styles.editForm}>
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
                                            {typeOptions.map(option => (
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

                                        {/* Поле для загрузки нового изображения */}
                                        <div className={styles.fileUploadSection}>
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
                                                <div className={styles.previewContainer}>
                                                    <img
                                                        src={previewUrl}
                                                        alt="Предпросмотр нового изображения"
                                                        className={styles.previewImage}
                                                    />
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                ) : (
                                    <div className={styles.questInfo}>
                                        <h3>{item.itemName}</h3>
                                        <p>{item.description}</p>
                                        <div className={styles.details}>
                                            <span>Владелец: {item.owner}</span>
                                            <span>Тип: {getTypeLabel(item.type)}</span>
                                            <span>Цена: {item.cost}</span>
                                            <span>Количество: {item.count}</span>
                                        </div>
                                        {/* Отображение текущего изображения товара */}
                                        {item.imageUrl && (
                                            <div className={styles.imagePreview}>
                                                <img
                                                    src={item.imageUrl}
                                                    alt="Текущее изображение товара"
                                                    className={styles.currentImage}
                                                />
                                            </div>
                                        )}
                                    </div>
                                )}

                                <div className={styles.actions}>
                                    {editingId === item.itemId ? (
                                        <>
                                            <button
                                                onClick={saveChanges}
                                                className={styles.saveButton}
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
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ItemListPage;
