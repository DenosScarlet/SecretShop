import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

const ProtectedRoute = ({ children, requiredRole, fallback = null }) => {
    const { isAuthenticated, hasGroup, loading } = useAuth();

    if (loading) {
        return <div>Загрузка...</div>;
    }

    if (!isAuthenticated) {
        return <div>Необходима авторизация</div>;
    }

    if (requiredRole && !hasGroup(requiredRole)) {
        return fallback || <div>Недостаточно прав доступа</div>;
    }

    return children;
};

export default ProtectedRoute;