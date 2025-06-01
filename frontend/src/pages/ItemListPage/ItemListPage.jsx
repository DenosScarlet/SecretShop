import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import styles from '../QuestListPage/QuestListPage.module.css';
import { shopApi } from '../../services/api';

const ItemListPage = () => {
    const [items, setItems] = useState([]);
    const [editingId, setEditingId] = useState(null);
    const [editedData, setEditedData] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        fetchItems();
    }, []);

    const fetchItems = async () => {
        setIsLoading(true);
        try {
            const response = await shopApi.getAllItems();
            setItems(response.data);
        } catch (error) {
            console.error('Error fetching items:', error);
            alert('Ошибка загрузки товаров');
        } finally {
            setIsLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Вы уверены, что хотите удалить этот товар?')) {
            try {
                await shopApi.deleteItem(id);
                fetchItems();
            } catch (error) {
                console.error('Error deleting item:', error);
                alert('Ошибка удаления товара');
            }
        }
    };

    const startEditing = (item) => {
        setEditingId(item.item_id);
        setEditedData({
            item_name: item.item_name,
            description: item.description,
            owner: item.owner,
            type: item.type,
            cost: item.cost,
            count: item.count
        });
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

    const saveChanges = async () => {
        try {
            await shopApi.updateItem(editingId, editedData);
            setEditingId(null);
            fetchItems();
            alert('Товар успешно обновлён!');
        } catch (error) {
            console.error('Ошибка обновления:', error);
            alert('Ошибка обновления товара');
        }
    };

    const cancelEditing = () => {
        setEditingId(null);
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
                            <div key={item.item_id} className={styles.questItem}>
                                {editingId === item.item_id ? (
                                    <div className={styles.editForm}>
                                        <input
                                            name="item_name"
                                            value={editedData.item_name}
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
                                    </div>
                                ) : (
                                    <div className={styles.questInfo}>
                                        <h3>{item.item_name}</h3>
                                        <p>{item.description}</p>
                                        <div className={styles.details}>
                                            <span>Владелец: {item.owner}</span>
                                            <span>Тип: {getTypeLabel(item.type)}</span>
                                            <span>Цена: {item.cost}</span>
                                            <span>Количество: {item.count}</span>
                                        </div>
                                    </div>
                                )}

                                <div className={styles.actions}>
                                    {editingId === item.item_id ? (
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
                                                onClick={() => handleDelete(item.item_id)}
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

export default ItemListPage;