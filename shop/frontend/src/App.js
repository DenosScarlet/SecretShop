import React, { useState, useEffect } from 'react';
import { Container, Typography, Box, Paper, Divider } from '@mui/material';
import { ItemList } from './components/ItemList';
import { ItemForm } from './components/ItemForm';
import { ItemSearch } from './components/ItemSearch';
import { Notification } from './components/Notification';
import { getAllItems, searchItems, deleteItem } from './services/shopService';

function App() {
  const [items, setItems] = useState([]);
  const [editingItem, setEditingItem] = useState(null);
  const [notification, setNotification] = useState({ open: false, message: '', severity: 'info' });

  const fetchItems = async (params = {}) => {
    try {
      console.log('Fetching items with params:', params);
      const items = await searchItems(params);
      console.log('Received items:', items);
      setItems(items);
    } catch (error) {
      console.error('Fetch error:', error);
      setItems([]);
    }
  };

  const showNotification = (message, severity = 'info') => {
    setNotification({ open: true, message, severity });
  };

  useEffect(() => {
    fetchItems();
  }, []);

  return (
    <Container maxWidth="lg" sx={{ py: 4 }}>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" component="h1" gutterBottom>
          Secret Shop
        </Typography>
        <Typography variant="subtitle1">
          Manage your magical items inventory
        </Typography>
      </Box>

      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          Search Items
        </Typography>
        <ItemSearch onSearch={fetchItems} />
      </Paper>

      <Paper elevation={3} sx={{ p: 3, mb: 4 }}>
        <Typography variant="h5" component="h2" gutterBottom>
          {editingItem ? 'Edit Item' : 'Add New Item'}
        </Typography>
        <ItemForm
          item={editingItem}
          onSuccess={() => {
            fetchItems();
            setEditingItem(null);
            showNotification(editingItem ? 'Item updated successfully' : 'Item added successfully', 'success');
          }}
          onCancel={() => setEditingItem(null)}
        />
      </Paper>

      <Divider sx={{ my: 3 }} />

      <ItemList
        items={items}
        onEdit={setEditingItem}
        onDelete={async (id) => {
          try {
            await deleteItem(id);
            fetchItems();
            showNotification('Item deleted successfully', 'success');
          } catch (error) {
            showNotification('Failed to delete item', 'error');
          }
        }}
      />

      <Notification
        open={notification.open}
        message={notification.message}
        severity={notification.severity}
        onClose={() => setNotification({ ...notification, open: false })}
      />
    </Container>
  );
}

export default App;