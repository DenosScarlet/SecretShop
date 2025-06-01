import React, { createContext, useContext, useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { keycloakApi } from '../services/keycloakApi';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const { keycloak, initialized } = useKeycloak();
    const [user, setUser] = useState(null);
    const [userRoles, setUserRoles] = useState([]);
    const [dtlUser, setDtlUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (initialized && keycloak.authenticated) {
            loadUserData();
        } else if (initialized) {
            setLoading(false);
        }
    }, [initialized, keycloak.authenticated]);

    const getUserQuests = async () => {
        if (!user?.userId) return [];

        try {
            const response = await keycloakApi.getUserQuests(user.userId);
            return response.data || [];
        } catch (error) {
            console.error('Error loading user quests:', error);
            return [];
        }
    };

    const loadUserData = async () => {
        try {
            setLoading(true);

            // Получаем данные пользователя из Keycloak токена
            const keycloakUser = keycloak.tokenParsed;
            const userId = keycloakUser.sub;

            // Пытаемся получить полные данные пользователя из микросервиса
            try {
                // Получаем данные пользователя из Keycloak через API
                const userResponse = await keycloakApi.getUserById(userId);
                setUser({
                    userId: userId,
                    username: userResponse.data.username,
                    firstName: userResponse.data.firstName,
                    lastName: userResponse.data.lastName,
                    email: userResponse.data.email,
                    enabled: userResponse.data.enabled,
                    attributes: userResponse.data.attributes || {}
                });

                // Получаем роли пользователя
                try {
                    const rolesResponse = await keycloakApi.getUserRoles(userId);
                    setUserRoles(rolesResponse.data || []);
                } catch (rolesError) {
                    console.warn('Could not load user roles from API, using token roles:', rolesError);
                    // Используем роли из токена как fallback
                    const tokenRoles = keycloakUser.realm_access?.roles || [];
                    setUserRoles(tokenRoles);
                }

                // Получаем данные пользователя из DTL
                try {
                    const dtlUserResponse = await keycloakApi.getDtlUser(userId);
                    setDtlUser(dtlUserResponse.data);
                } catch (dtlError) {
                    console.warn('Could not load DTL user data:', dtlError);
                    setDtlUser(null);
                }

            } catch (error) {
                console.error('Error loading user data from microservice:', error);

                // Fallback: используем данные из Keycloak токена
                setUser({
                    userId: userId,
                    username: keycloakUser.preferred_username,
                    firstName: keycloakUser.given_name,
                    lastName: keycloakUser.family_name,
                    email: keycloakUser.email,
                    enabled: true,
                    attributes: {}
                });

                // Получаем роли из токена
                const tokenRoles = keycloakUser.realm_access?.roles || [];
                setUserRoles(tokenRoles);

                setDtlUser(null);
            }

        } catch (error) {
            console.error('Error loading user data:', error);
        } finally {
            setLoading(false);
        }
    };

    // Проверка ролей пользователя
    const hasRole = (role) => {
        return userRoles.includes(role);
    };

    // Проверка realm ролей (используется в SecurityUtils на backend)
    const hasRealmRole = (role) => {
        return hasRole(role);
    };

    // Основные роли системы
    const isAdmin = () => hasRealmRole('admin');
    const isManager = () => hasRealmRole('manager') || hasRealmRole('managerGroup');
    const isEmployee = () => hasRealmRole('employee') || hasRealmRole('userGroup');

    // Права доступа
    const canManageQuests = () => isAdmin() || isManager();
    const canManageUsers = () => isAdmin();

    // Обновление пользователя
    const updateUser = async (userId, userData) => {
        try {
            const response = await keycloakApi.updateUser(userId, userData);
            await loadUserData(); // Перезагружаем данные пользователя
            return response.data;
        } catch (error) {
            console.error('Error updating user:', error);
            throw error;
        }
    };

    // Обновление DTL пользователя
    const updateDtlUser = async (userId, updateData) => {
        try {
            const response = await keycloakApi.updateDtlUser(userId, updateData);
            await loadUserData(); // Перезагружаем данные пользователя
            return response.data;
        } catch (error) {
            console.error('Error updating DTL user:', error);
            throw error;
        }
    };

    // Обновление баланса
    const updateBalance = async (userId, newBalance) => {
        try {
            const response = await keycloakApi.updateUserBalance(userId, newBalance);
            await loadUserData(); // Перезагружаем данные пользователя
            return response.data;
        } catch (error) {
            console.error('Error updating balance:', error);
            throw error;
        }
    };

    // Обновление аватара
    const updateAvatar = async (userId, avatarUrl) => {
        try {
            const response = await keycloakApi.updateUserAvatar(userId, avatarUrl);
            await loadUserData(); // Перезагружаем данные пользователя
            return response.data;
        } catch (error) {
            console.error('Error updating avatar:', error);
            throw error;
        }
    };

    const login = () => {
        keycloak.login();
    };

    const logout = () => {
        keycloak.logout();
    };

    const value = {
        // Пользовательские данные
        getUserQuests,
        user,
        dtlUser,
        userRoles,
        loading,
        isAuthenticated: keycloak.authenticated,
        keycloak,

        // Методы аутентификации
        login,
        logout,
        loadUserData,

        // Проверка ролей
        hasRole,
        hasRealmRole,
        isAdmin,
        isManager,
        isEmployee,

        // Права доступа
        canManageQuests,
        canManageUsers,

        // Методы обновления
        updateUser,
        updateDtlUser,
        updateBalance,
        updateAvatar,

        // Устаревшие методы (для обратной совместимости)
        userGroups: userRoles, // Alias для ролей
        getUserGroups: () => userRoles
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};