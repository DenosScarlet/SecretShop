import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/shop';

export const getItem = async (id) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/item/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching item:', error);
    return null;
  }
};

export const getAllItems = async () => {
  try {
    const response = await axios.get(`${API_BASE_URL}/items`);
    return response.data || [];
  } catch (error) {
    console.error('Error fetching items:', error);
    return [];
  }
};

export const addItem = async (item) => {
  try {
    const response = await axios.post(`${API_BASE_URL}/item`, {
      item_name: item.name,
      description: item.description,
      owner: item.owner,
      type: item.type,
      cost: item.cost,
      count: item.count
    });
    return response.data;
  } catch (error) {
    console.error('Error adding item:', error);
    throw error;
  }
};

export const updateItem = async (id, item) => {
  try {
    const response = await axios.put(`${API_BASE_URL}/item/${id}`, {
      item_name: item.name,
      description: item.description,
      owner: item.owner,
      type: item.type,
      cost: item.cost,
      count: item.count
    });
    return response.data;
  } catch (error) {
    console.error('Error updating item:', error);
    throw error;
  }
};

export const deleteItem = async (id) => {
  try {
    await axios.delete(`${API_BASE_URL}/item/${id}`);
  } catch (error) {
    console.error('Error deleting item:', error);
    throw error;
  }
};

export const searchItems = async (params) => {
  try {
    const response = await axios.get(`${API_BASE_URL}/item/search`, {
      params: {
        name: params.name || null,
        owner: params.owner || null,
        type: params.type || null
      }
    });

    // Преобразуем данные к фронтенд-формату
    return response.data.map(item => ({
      id: item.item_id,
      name: item.item_name,
      description: item.description,
      owner: item.owner,
      type: item.type,
      cost: item.cost,
      count: item.count
    }));

  } catch (error) {
    console.error('Search error:', error.response?.data || error.message);
    return [];
  }
};