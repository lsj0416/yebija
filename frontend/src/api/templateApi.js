import axiosInstance from './axiosInstance';

export const getTemplates = () =>
  axiosInstance.get('/api/templates');

export const getTemplate = (id) =>
  axiosInstance.get(`/api/templates/${id}`);

export const createTemplate = (data) =>
  axiosInstance.post('/api/templates', data);

export const updateTemplate = (id, data) =>
  axiosInstance.put(`/api/templates/${id}`, data);

export const deleteTemplate = (id) =>
  axiosInstance.delete(`/api/templates/${id}`);
