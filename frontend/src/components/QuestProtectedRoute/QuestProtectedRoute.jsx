import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

const QuestProtectedRoute = ({ children }) => {
    const { isAuthenticated, canManageQuests, loading } = useAuth();

    if (loading) {
        return <div>Загрузка...</div>;
    }

    if (!isAuthenticated) {
        return <div>Необходима авторизация для доступа к управлению квестами</div>;
    }

    if (!canManageQuests()) {
        return <div>У вас недостаточно прав для управления квестами. Доступ имеют только администраторы и менеджеры.</div>;
    }

    return children;
};

export default QuestProtectedRoute;