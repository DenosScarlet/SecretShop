import Keycloak from 'keycloak-js';

const keycloakConfig = {
    url: 'http://localhost:8180',
    realm: 'secretshoprealm',
    clientId: 'frontend-client'
};

const keycloak = new Keycloak(keycloakConfig);

// Делаем keycloak доступным глобально для использования в интерцепторах
window.keycloak = keycloak;

export default keycloak;