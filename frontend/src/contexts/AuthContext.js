import React, { createContext, useContext, useState, useEffect } from 'react';
import { useKeycloak } from '@react-keycloak/web';
import { keycloakApi } from '../services/keycloakApi';
import { questApi } from '../services/questApi';

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
    const [userGroups, setUserGroups] = useState([]);
    const [dtlUser, setDtlUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (initialized) {
            if (keycloak.authenticated) {
                loadUserData();
            } else {
                setLoading(false);
                setUserGroups([]);
            }
        }
    }, [initialized, keycloak.authenticated]);

    const getUserQuests = async () => {
        try {
            const response = await questApi.getUserQuests();
            console.log('User quests:', response.data);
            return response.data || [];
        } catch (error) {
            console.error('Error loading user quests:', error);
            return [];
        }
    };

    const loadUserData = async () => {
        setLoading(true);
        setError(null);

        try {
            const keycloakUser = keycloak.tokenParsed;
            if (!keycloakUser) {
                throw new Error('Keycloak tokenParsed is undefined');
            }

            const userId = keycloakUser.sub;
            console.log('Keycloak user data:', { userId, groups: keycloakUser.groups });

            // Получаем группы через API
            let groups = [];
            try {
                const groupsResponse = await keycloakApi.getUserGroups(userId);
                groups = Array.isArray(groupsResponse.data)
                    ? groupsResponse.data
                        .filter(group => typeof group === 'string')
                        .map(group => group.trim().toLowerCase())
                        .filter(group => group.length > 0)
                    : [];
                console.log('API groups:', groups);
            } catch (groupError) {
                console.warn('Failed to load groups from API, using token groups:', groupError);
                // Fallback к токену
                groups = Array.isArray(keycloakUser.groups)
                    ? keycloakUser.groups
                        .filter(group => typeof group === 'string')
                        .map(group => group.replace(/^\//, '').trim().toLowerCase())
                        .filter(group => group.length > 0)
                    : [];
            }
            setUserGroups(groups);
            console.log('Normalized groups:', groups);

            // Получаем данные пользователя из Keycloak API
            try {
                const userResponse = await keycloakApi.getUserById(userId);
                setUser({
                    userId,
                    username: userResponse.data.username || keycloakUser.preferred_username,
                    firstName: userResponse.data.firstName || keycloakUser.given_name,
                    lastName: userResponse.data.lastName || keycloakUser.family_name,
                    email: userResponse.data.email || keycloakUser.email,
                    enabled: userResponse.data.enabled ?? true,
                    attributes: userResponse.data.attributes || {}
                });
            } catch (apiError) {
                console.warn('Failed to load user data from API, using token data:', apiError);
                setUser({
                    userId,
                    username: keycloakUser.preferred_username,
                    firstName: keycloakUser.given_name,
                    lastName: keycloakUser.family_name,
                    email: keycloakUser.email,
                    enabled: true,
                    attributes: {}
                });
            }

            // Получаем данные из DTL
            try {
                const dtlUserResponse = await keycloakApi.getDtlUser(userId);
                setDtlUser(dtlUserResponse.data);
            } catch (dtlError) {
                console.warn('Failed to load DTL user data:', dtlError);
                setDtlUser(null);
            }

        } catch (error) {
            console.error('Error loading user data:', error);
            setError(error.message);
            setUser(null);
            setUserGroups([]);
        } finally {
            setLoading(false);
        }
    };

    // Проверка принадлежности к группе
    const hasGroup = (group) => {
        if (!group) return false;
        const normalizedGroup = group.toLowerCase();
        const has = userGroups.includes(normalizedGroup);
        console.log(`Checking group ${normalizedGroup}: ${has}`);
        return has;
    };

    // Основные группы системы
    const isAdmin = () => hasGroup('adminGroup');
    const isManager = () => hasGroup('managerGroup');
    const isEmployee = () => hasGroup('userGroup');

    // Права доступа
    const canManageQuests = () => {
        const can = isAdmin() || isManager();
        console.log('canManageQuests:', can);
        return can;
    };

    const canManageUsers = () => {
        const can = isAdmin();
        console.log('canManageUsers:', can);
        return can;
    };

    // Обновление пользователя
    const updateUser = async (userId, userData) => {
        try {
            const response = await keycloakApi.updateUser(userId, userData);
            await loadUserData();
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
            await loadUserData();
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
            await loadUserData();
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
            await loadUserData();
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
        setUser(null);
        setUserGroups([]);
        setDtlUser(null);
    };

    const value = {
        getUserQuests,
        user,
        dtlUser,
        userGroups,
        loading,
        error,
        isAuthenticated: keycloak.authenticated,
        keycloak,
        login,
        logout,
        loadUserData,
        hasGroup,
        isAdmin,
        isManager,
        isEmployee,
        canManageQuests,
        canManageUsers,
        updateUser,
        updateDtlUser,
        updateBalance,
        updateAvatar,
        getUserGroups: () => userGroups
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};