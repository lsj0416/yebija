import axiosInstance from './axiosInstance';

export const attachFileToItem = (itemId, file) => {
  const formData = new FormData();
  formData.append('file', file);
  return axiosInstance.post(`/api/files/worship-items/${itemId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
};

export const deleteFile = (fileId) =>
  axiosInstance.delete(`/api/files/${fileId}`);

export const exportPpt = (worshipId) =>
  axiosInstance.post(`/api/worships/${worshipId}/export`, null, {
    responseType: 'blob',
  });
