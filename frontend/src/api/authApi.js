import axiosInstance from './axiosInstance';

export const login = (adminEmail, password) =>
  axiosInstance.post('/api/auth/login', { adminEmail, password });

export const signup = (name, denomination, adminEmail, password) =>
  axiosInstance.post('/api/auth/signup', { name, denomination, adminEmail, password });
