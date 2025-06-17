import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ReactKeycloakProvider } from '@react-keycloak/web';
import keycloak from './services/keycloak';
import { AuthProvider } from './contexts/AuthContext';
import Header from './components/Header/Header';
import LeftMenu from './components/LeftMenu/LeftMenu';
import QuestCreationPage from './pages/QuestCreationPage/QuestCreationPage';
import QuestListPage from './pages/QuestListPage/QuestListPage';
import ItemListPage from './pages/ItemListPage/ItemListPage';
import ItemCreationPage from './pages/ItemCreationPage/ItemCreationPage';
import QuestProtectedRoute from './components/QuestProtectedRoute/QuestProtectedRoute';
import LoginPage from './pages/LoginPage/LoginPage';
import ShopPage from './pages/ShopPage/ShopPage';
import ItemPage from './pages/ItemPage/ItemPage';
import ProtectedRoute from './components/ProtectedRoute/ProtectedRoute';
import OperationListPage from './pages/OperationListPage/OperationListPage';
import UserListPage from './pages/UserListPage/UserListPage';
import UserCreationPage from './pages/UserCreationPage/UserCreationPage';
import UserEditPage from './pages/UserEditPage/UserEditPage';
import './App.css';
import PersonalAccountPage from "./pages/PersonalAccountPage/PersonalAccountPage";
import TasksPage from "./pages/TaskPage/TasksPage";

const handleKeycloakEvent = (event, error) => {
    console.log('Keycloak event:', event, error);
    switch (event) {
        case 'onReady':
            console.log('Keycloak initialized:', keycloak.authenticated);
            break;
        case 'onAuthSuccess':
            console.log('Authentication successful');
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
                                <Route
                                    path="/shop/items"
                                    element={
                                        <QuestProtectedRoute>
                                            <ItemListPage />
                                        </QuestProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/shop"
                                    element={
                                        <ProtectedRoute>
                                            <ShopPage />
                                        </ProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/shop/item/:itemId"
                                    element={
                                        <ProtectedRoute>
                                            <ItemPage />
                                        </ProtectedRoute>
                                    }
                                />
                                <Route path="/shop/operations" element={<OperationListPage />} />
                                <Route
                                    path="/shop/item/create"
                                    element={
                                        <QuestProtectedRoute>
                                            <ItemCreationPage />
                                        </QuestProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/users"
                                    element={
                                        <ProtectedRoute requiredRole="adminGroup">
                                            <UserListPage />
                                        </ProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/users/create"
                                    element={
                                        <ProtectedRoute requiredRole="adminGroup">
                                            <UserCreationPage />
                                        </ProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/users/edit/:userId"
                                    element={
                                        <ProtectedRoute requiredRole="adminGroup">
                                            <UserEditPage />
                                        </ProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/account"
                                    element={
                                        <ProtectedRoute>
                                            <PersonalAccountPage />
                                        </ProtectedRoute>
                                    }
                                />
                                <Route
                                    path="/tasks"
                                    element={
                                        <ProtectedRoute>
                                            <TasksPage/>
                                        </ProtectedRoute>
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