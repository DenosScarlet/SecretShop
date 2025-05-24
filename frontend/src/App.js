import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Header from './components/Header/Header';
import LeftMenu from './components/LeftMenu/LeftMenu';
import QuestCreationPage from './pages/QuestCreationPage/QuestCreationPage';
import './App.css';

function App() {
  return (
      <Router>
        <div className="app">
          <Header />
          <div className="main-content">
            <LeftMenu />
            <Routes>
              <Route path="/quest/create" element={<QuestCreationPage />} />
              {/* Добавьте другие маршруты по необходимости */}
            </Routes>
          </div>
        </div>
      </Router>
  );
}

export default App;