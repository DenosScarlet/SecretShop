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
        item_name: '',
        description: '',
        owner: '',
        type: 'MERCH',
        cost: 0,
        count: 1
    });

    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState('');
    const [preview, setPreview] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            setFile(selectedFile);
            setFileName(selectedFile.name);

            // Создание превью
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreview(reader.result);
            };
            reader.readAsDataURL(selectedFile);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            await shopApi.addItemWithFile(formData, file);
            alert('Товар успешно создан!');
            onSuccess?.();

            // Сброс формы после успешного создания
            setFormData({
                item_name: '',
                description: '',
                owner: '',
                type: 'MERCH',
                cost: 0,
                count: 1
            });
            setFile(null);
            setFileName('');
            setPreview(null);
        } catch (error) {
            console.error('Ошибка:', error);
            alert('Ошибка создания товара');
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className={styles.form}>
            <div className={styles.formGroup}>
                <label className={styles.label}>Название товара</label>
                <input
                    className={styles.input}
                    type="text"
                    required
                    value={formData.item_name}
                    onChange={(e) => setFormData({...formData, item_name: e.target.value})}
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
                    min="0"
                    value={formData.cost}
                    onChange={(e) => setFormData({...formData, cost: parseInt(e.target.value, 10) || 0})}
                />
            </div>

            <div className={styles.formGroup}>
                <label className={styles.label}>Количество</label>
                <input
                    className={styles.input}
                    type="number"
                    min="1"
                    value={formData.count}
                    onChange={(e) => setFormData({...formData, count: parseInt(e.target.value, 10) || 1})}
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