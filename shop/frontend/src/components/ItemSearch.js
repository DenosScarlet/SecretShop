import React, { useState } from 'react';
import {
  TextField,
  Button,
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material';

const types = [
  { value: 'MERCH', label: 'Merchandise' },
  { value: 'DEVICES', label: 'Devices' },
  { value: 'ACCESSORIES', label: 'Accessories' },
  { value: 'COUPONS', label: 'Coupons' },
];

export const ItemSearch = ({ onSearch }) => {
  const [searchParams, setSearchParams] = useState({
    name: '',
    owner: '',
    type: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setSearchParams(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSearch({
      name: searchParams.name.trim() || undefined,
      owner: searchParams.owner.trim() || undefined,
      type: searchParams.type || undefined
    });
  };

  const handleReset = () => {
    setSearchParams({ name: '', owner: '', type: '' });
    onSearch({});
  };

  return (
    <form onSubmit={handleSubmit}>
      <Grid container spacing={2}>
        {/* Обновлённые Grid без item */}
        <Grid xs={12} md={4}>
          <TextField
            fullWidth
            label="Name"
            name="name"
            value={searchParams.name}
            onChange={handleChange}
            margin="normal"
          />
        </Grid>
        <Grid xs={12} md={4}>
          <TextField
            fullWidth
            label="Owner"
            name="owner"
            value={searchParams.owner}
            onChange={handleChange}
            margin="normal"
          />
        </Grid>
        <Grid xs={12} md={4}>
          <FormControl fullWidth margin="normal">
            <InputLabel>Type</InputLabel>
            <Select
              name="type"
              value={searchParams.type}
              onChange={handleChange}
              label="Type"
            >
              <MenuItem value="">All Types</MenuItem>
              {types.map((type) => (
                <MenuItem key={type.value} value={type.value}>
                  {type.label}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Grid>
        {/* Кнопки - тоже обновляем Grid */}
        <Grid xs={12}>
          <Button type="submit" variant="contained" color="primary" sx={{ mr: 1 }}>
            Search
          </Button>
          <Button variant="outlined" onClick={handleReset}>
            Reset
          </Button>
        </Grid>
      </Grid>
    </form>
  );
};