import axiosInstance from './axiosInstance';

export const getVerses = (book, chapter, verseStart, verseEnd) =>
  axiosInstance.get('/api/bible/verses', {
    params: { book, chapter, verseStart, verseEnd },
  });
