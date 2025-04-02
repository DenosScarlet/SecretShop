import React from 'react';
import { 
  Table, 
  TableBody, 
  TableCell, 
  TableContainer, 
  TableHead, 
  TableRow, 
  Paper,
  IconButton,
  Typography,
  Chip,
  Box
} from '@mui/material';
import { Edit as EditIcon, Delete as DeleteIcon } from '@mui/icons-material';

// Цвета для разных типов товаров
const typeColors = {
  MERCH: 'primary',
  DEVICES: 'secondary',
  ACCESSORIES: 'success',
  COUPONS: 'warning',
};

// Функция для форматирования цены
const formatPrice = (price) => {
  if (!price && price !== 0) return '$0.00';
  return `$${parseFloat(price).toFixed(2)}`;
};

// Функция для преобразования типа в читаемый формат
const formatType = (type) => {
  const typeMap = {
    MERCH: 'Merch',
    DEVICES: 'Devices',
    ACCESSORIES: 'Accessories',
    COUPONS: 'Coupons'
  };
  return typeMap[type] || type;
};

export const ItemList = ({ items, onEdit, onDelete }) => {
  // Логируем полученные данные для отладки
  console.log('Items data received:', items);

  if (!items) {
    return (
      <Box sx={{ p: 4, textAlign: 'center' }}>
        <Typography>Loading items...</Typography>
      </Box>
    );
  }

  if (items.length === 0) {
    return (
      <Paper sx={{ p: 4, textAlign: 'center' }}>
        <Typography variant="h6">No items found</Typography>
        <Typography variant="body1">
          Try adjusting your search criteria
        </Typography>
      </Paper>
    );
  }

  return (
    <TableContainer component={Paper} sx={{ mt: 4 }}>
      <Table sx={{ minWidth: 650 }} aria-label="Items table">
        <TableHead>
          <TableRow>
            <TableCell sx={{ fontWeight: 'bold' }}>Name</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Type</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Owner</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Cost</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Count</TableCell>
            <TableCell sx={{ fontWeight: 'bold' }}>Description</TableCell>
            <TableCell align="right" sx={{ fontWeight: 'bold' }}>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {items.map((item) => (
            <TableRow
              key={item.item_id || item.id}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
              <TableCell component="th" scope="row">
                {item.item_name || item.name || 'No name'}
              </TableCell>
              <TableCell>
                <Chip
                  label={formatType(item.type)}
                  color={typeColors[item.type] || 'default'}
                  sx={{ textTransform: 'capitalize' }}
                />
              </TableCell>
              <TableCell>{item.owner || 'Unknown'}</TableCell>
              <TableCell>
                {formatPrice(item.cost)}
              </TableCell>
              <TableCell>
                {item.count || 0}
              </TableCell>
              <TableCell>
                {item.description || 'No description available'}
              </TableCell>
              <TableCell align="right">
                <IconButton
                  onClick={() => onEdit(item)}
                  color="primary"
                  aria-label="edit"
                >
                  <EditIcon />
                </IconButton>
                <IconButton
                  onClick={() => onDelete(item.item_id || item.id)}
                  color="error"
                  aria-label="delete"
                >
                  <DeleteIcon />
                </IconButton>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};