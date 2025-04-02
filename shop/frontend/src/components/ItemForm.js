import React, { useState, useEffect } from 'react';
import {
  TextField,
  Button,
  Grid,
  MenuItem,
  Select,
  InputLabel,
  FormControl,
  Typography,
  Box
} from '@mui/material';
import { addItem, updateItem } from '../services/shopService';

const types = [
  { value: 'MERCH', label: 'Merchandise' },
  { value: 'DEVICES', label: 'Devices' },
  { value: 'ACCESSORIES', label: 'Accessories' },
  { value: 'COUPONS', label: 'Coupons' },
];

export const ItemForm = ({ item, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    owner: '',
    type: 'MERCH',
    cost: 0,
    count: 0,
  });

  useEffect(() => {
    if (item) {
      setFormData({
        name: item.name || '',
        description: item.description || '',
        owner: item.owner || '',
        type: item.type || 'MERCH',
        cost: item.cost || 0,
        count: item.count || 0,
      });
    }
  }, [item]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (item) {
        await updateItem(item.id, formData);
      } else {
        await addItem(formData);
      }
      onSuccess();
    } catch (error) {
      console.error('Error saving item:', error);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Grid container spacing={2}>
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            label="Name"
            name="name"
            value={formData.name}
            onChange={handleChange}
            required
            margin="normal"
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            label="Owner"
            name="owner"
            value={formData.owner}
            onChange={handleChange}
            required
            margin="normal"
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <FormControl fullWidth margin="normal">
            <InputLabel>Type</InputLabel>
            <Select
              name="type"
              value={formData.type}
              onChange={handleChange}
              required
              label="Type"
            >
              {types.map((type) => (
                <MenuItem key={type.value} value={type.value}>
                  {type.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            label="Cost"
            name="cost"
            type="number"
            value={formData.cost}
            onChange={handleChange}
            inputProps={{ min: 0, step: "0.01" }}
            required
            margin="normal"
          />
        </Grid>
        <Grid item xs={12} md={6}>
          <TextField
            fullWidth
            label="Count"
            name="count"
            type="number"
            value={formData.count}
            onChange={handleChange}
            inputProps={{ min: 0 }}
            required
            margin="normal"
          />
        </Grid>
        <Grid item xs={12}>
          <TextField
            fullWidth
            label="Description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            multiline
            rows={4}
            margin="normal"
          />
        </Grid>
      </Grid>
      <Box sx={{ mt: 2 }}>
        <Button
          type="submit"
          variant="contained"
          color="primary"
          sx={{ mr: 1 }}
        >
          {item ? 'Update' : 'Add'} Item
        </Button>
        {item && (
          <Button
            variant="outlined"
            onClick={onCancel}
          >
            Cancel
          </Button>
        )}
      </Box>
    </form>
  );
};