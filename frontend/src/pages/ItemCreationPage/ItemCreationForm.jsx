import React, { useState } from 'react';
import styles from './ItemCreationForm.module.css';
import { shopApi } from '../../services/api';

const typeOptions = [
    { value: 'MERCH', label: 'Мерч' },
    { value: 'DEVICES', label: 'Устройства' },
    { value: 'ACCESSORIES', label: 'Аксессуары' },
    { value: 'COUPONS', label: 'Купоны' }
];

export default function ItemCreationForm({ onSuccess }) {
    const [formData, setFormData] = useState({
        itemName: '', // Используем itemName вместо item_name
        description: '',
        owner: '',
        type: 'MERCH',
        cost: 100,
        count: 1
    });

    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState('');
    const [preview, setPreview] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            if (!selectedFile.type.startsWith('image/')) {
                setErrorMessage('Пожалуйста, выберите файл изображения');
                return;
            }

            if (selectedFile.size > 5 * 1024 * 1024) {
                setErrorMessage('Размер файла не должен превышать 5MB');
                return;
            }

            setFile(selectedFile);
            setFileName(selectedFile.name);
            setErrorMessage('');

            const reader = new FileReader();
            reader.onloadend = () => {
                setPreview(reader.result);
            };
            reader.readAsDataURL(selectedFile);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Проверка обязательных полей
        if (!formData.itemName || !formData.description || !formData.owner) {
            setErrorMessage("Заполните название, описание и владельца товара");
            return;
        }

        if (!file) {
            setErrorMessage("Выберите изображение товара");
            return;
        }

        setIsSubmitting(true);
        setErrorMessage('');

        try {
            // Создаем JSON для товара (с правильными именами полей)
            const itemJson = JSON.stringify(formData);

            // Вызываем API с itemJson и файлом
            await shopApi.addItemWithFile(itemJson, file);

            alert('Товар успешно создан!');
            onSuccess?.();

            // Сброс формы
            setFormData({
                itemName: '',
                description: '',
                owner: '',
                type: 'MERCH',
                cost: 100,
                count: 1
            });
            setFile(null);
            setFileName('');
            setPreview(null);
        } catch (error) {
            console.error('Ошибка:', error);
            const errorMsg = error.response?.data?.message ||
                error.message ||
                'Неизвестная ошибка при создании товара';
            setErrorMessage(errorMsg);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            {errorMessage && (
                <div className={styles.error}>
                    {errorMessage}
                </div>
            )}

            <div className={styles.formGroup}>
                <label className={styles.label}>Название товара</label>
                <input
                    className={styles.input}
                    type="text"
                    required
                    value={formData.itemName}
                    onChange={(e) => setFormData({...formData, itemName: e.target.value})}
                    placeholder="Введите название товара"
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Описание</label>
                <textarea
                    className={`${styles.input} ${styles.textarea}`}
                    value={formData.description}
                    onChange={(e) => setFormData({...formData, description: e.target.value})}
                    placeholder="Введите описание товара"
                    rows="3"
                    required
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Владелец</label>
                <input
                    className={styles.input}
                    type="text"
                    required
                    value={formData.owner}
                    onChange={(e) => setFormData({...formData, owner: e.target.value})}
                    placeholder="Введите владельца товара"
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Тип товара</label>
                <select
                    className={styles.select}
                    value={formData.type}
                    onChange={(e) => setFormData({...formData, type: e.target.value})}
                >
                    {typeOptions.map(option => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Цена</label>
                <input
                    className={styles.input}
                    type="number"
                    min="1"
                    step="1"
                    value={formData.cost}
                    onChange={(e) => {
                        const value = e.target.value;
                        setFormData({...formData, cost: value === "" ? 100 : parseInt(value, 10)});
                    }}
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Количество</label>
                <input
                    className={styles.input}
                    type="number"
                    min="1"
                    step="1"
                    value={formData.count}
                    onChange={(e) => {
                        const value = e.target.value;
                        setFormData({...formData, count: value === "" ? 1 : parseInt(value, 10)});
                    }}
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Изображение товара</label>
                <input
                    type="file"
                    id="file-upload"
                    className={styles.fileInput}
                    onChange={handleFileChange}
                    accept="image/*"
                    required
                />
                <label htmlFor="file-upload" className={styles.fileLabel}>
                    <span>{fileName ? fileName : 'Выберите изображение'}</span>
                </label>
                {fileName && <div className={styles.fileName}>Выбран файл: {fileName}</div>}

                {preview && (
                    <div className={styles.previewContainer}>
                        <img
                            src={preview}
                            alt="Предпросмотр изображения товара"
                            className={styles.previewImage}
                        />
                    </div>
                )}
            </div>

            <button
                type="submit"
                className={styles.submitButton}
                disabled={isSubmitting}
            >
                {isSubmitting ? 'Создание...' : 'Создать товар'}
            </button>
        </form>
    );
}