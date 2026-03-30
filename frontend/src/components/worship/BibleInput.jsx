import { useState, useEffect } from 'react';
import BOOKS from '../../data/books';
import { getVerses } from '../../api/bibleApi';
import styles from './ItemInput.module.css';

function BibleInput({ content, onChange }) {
  const [book, setBook]           = useState(content?.book || '창세기');
  const [chapter, setChapter]     = useState(content?.chapter || 1);
  const [startVerse, setStart]    = useState(content?.startVerse || 1);
  const [endVerse, setEnd]        = useState(content?.endVerse || 1);
  const [preview, setPreview]     = useState('');
  const [loading, setLoading]     = useState(false);

  const selectedBook = BOOKS.find((b) => b.name === book) || BOOKS[0];
  const chapterCount = selectedBook.chapters;

  useEffect(() => {
    onChange({ book, chapter, startVerse, endVerse });
  }, [book, chapter, startVerse, endVerse]);

  const handlePreview = async () => {
    setLoading(true);
    try {
      const res = await getVerses(book, chapter, startVerse, endVerse);
      setPreview(res.data.data?.text || '');
    } catch {
      setPreview('본문을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.inputGroup}>
      <div className={styles.row3}>
        <div className={styles.field}>
          <label>책</label>
          <select value={book} onChange={(e) => { setBook(e.target.value); setChapter(1); }}>
            {BOOKS.map((b) => (
              <option key={b.id} value={b.name}>{b.name}</option>
            ))}
          </select>
        </div>

        <div className={styles.field}>
          <label>장</label>
          <input
            type="number"
            min={1}
            max={chapterCount}
            value={chapter}
            onChange={(e) => setChapter(Number(e.target.value))}
          />
        </div>

        <div className={styles.field}>
          <label>절</label>
          <div className={styles.verseRange}>
            <input
              type="number"
              min={1}
              value={startVerse}
              onChange={(e) => setStart(Number(e.target.value))}
              placeholder="시작"
            />
            <span className={styles.dash}>—</span>
            <input
              type="number"
              min={startVerse}
              value={endVerse}
              onChange={(e) => setEnd(Number(e.target.value))}
              placeholder="끝"
            />
          </div>
        </div>
      </div>

      <button type="button" className={styles.previewBtn} onClick={handlePreview} disabled={loading}>
        {loading ? '불러오는 중...' : '본문 미리보기'}
      </button>

      {preview && (
        <div className={styles.previewBox}>
          <p>{preview}</p>
        </div>
      )}
    </div>
  );
}

export default BibleInput;
