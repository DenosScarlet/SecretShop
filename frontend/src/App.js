import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloak from './services/keycloak';
import { AuthProvider } from './contexts/AuthContext';
import Header from './components/Header/Header';
import LeftMenu from './components/LeftMenu/LeftMenu';
import QuestCreationPage from './pages/QuestCreationPage/QuestCreationPage';
import QuestListPage from './pages/QuestListPage/QuestListPage';
import QuestProtectedRoute from './components/QuestProtectedRoute/QuestProtectedRoute';
import LoginPage from './pages/LoginPage/LoginPage';
import './App.css';

// Обработчик событий Keycloak (одна функция)
const handleKeycloakEvent = (event, error) => {
    console.log('Keycloak event:', event, error);

    switch (event) {
        case 'onReady':
            console.log('Keycloak initialized:', keycloak.authenticated);
            break;
        case 'onAuthSuccess':
            console.log('Authentication successful');
            // Сохраняем токен в localStorage
            if (keycloak.token) {
                localStorage.setItem('keycloak-token', keycloak.token);
            }
            break;
        case 'onAuthError':
            console.log('Authentication failed');
            break;
        case 'onAuthRefreshSuccess':
            console.log('Token refreshed');
            if (keycloak.token) {
                localStorage.setItem('keycloak-token', keycloak.token);
            }
            break;
        case 'onTokenExpired':
            console.log('Token expired');
            keycloak.updateToken(30);
            break;
        default:
            console.log('Unknown Keycloak event:', event);
    }
};

// Инициализация Keycloak
const keycloakInitOptions = {
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
    checkLoginIframe: false
};

function App() {
    return (
        <ReactKeycloakProvider
            authClient={keycloak}
            initOptions={keycloakInitOptions}
            onEvent={handleKeycloakEvent}
        >
            <AuthProvider>
                <Router>
                    <div className="app">
                        <Header />
                        <div className="main-content">
                            <LeftMenu />
                            <Routes>
                                <Route path="/login" element={<LoginPage />} />
                                <Route
                                    path="/quest/create"
                                    element={
                                        <QuestProtectedRoute>
                                            <QuestCreationPage />
                                        </QuestProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/quests"
                                    element={
                                        <QuestProtectedRoute>
                                            <QuestListPage />
                                        </QuestProtectedRoute>
                                    }
                                />
                                <Route path="/" element={<div>Главная страница</div>} />
                            </Routes>
                        </div>
                    </div>
                </Router>
            </AuthProvider>
        </ReactKeycloakProvider>
    );
}

export default App;