import axiosInstance from './axiosInstance';

export const getWorships = () =>
  axiosInstance.get('/api/worships');

export const getWorship = (id) =>
  axiosInstance.get(`/api/worships/${id}`);

export const createWorship = (data) =>
  axiosInstance.post('/api/worships', data);

export const updateWorship = (id, data) =>
  axiosInstance.put(`/api/worships/${id}`, data);

export const completeWorship = (id) =>
  axiosInstance.post(`/api/worships/${id}/complete`);

export const deleteWorship = (id) =>
  axiosInstance.delete(`/api/worships/${id}`);

export const updateWorshipItem = (worshipId, itemId, data) =>
  axiosInstance.put(`/api/worships/${worshipId}/items/${itemId}`, data);
