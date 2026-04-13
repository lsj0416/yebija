import axios from 'axios';
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

export const exportPpt = async (worshipId) => {
  try {
    return await axiosInstance.post(`/api/worships/${worshipId}/export`, null, {
      responseType: 'blob',
    });
  } catch (error) {
    const normalizedError = await normalizeBlobError(error);
    throw normalizedError;
  }
};

export const getDownloadFilename = (headers, fallback) => {
  const disposition = headers?.['content-disposition'];
  if (!disposition) return fallback;

  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i);
  if (utf8Match?.[1]) {
    return decodeURIComponent(utf8Match[1]);
  }

  const basicMatch = disposition.match(/filename="([^"]+)"/i);
  return basicMatch?.[1] || fallback;
};

async function normalizeBlobError(error) {
  if (!axios.isAxiosError(error)) {
    return error;
  }

  const data = error.response?.data;
  if (!(data instanceof Blob)) {
    return error;
  }

  const contentType = data.type || error.response?.headers?.['content-type'] || '';
  if (!contentType.includes('application/json')) {
    return error;
  }

  try {
    const parsed = JSON.parse(await data.text());
    error.yebijaCode = parsed?.error?.code;
    error.yebijaMessage = parsed?.error?.message;
  } catch {
    return error;
  }

  return error;
}
