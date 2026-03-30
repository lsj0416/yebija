import { useState, useEffect } from 'react';
import styles from './ItemInput.module.css';

function SermonInput({ content, onChange }) {
  const [title,     setTitle]     = useState(content?.title || '');
  const [scripture, setScripture] = useState(content?.scripture || '');
  const [preacher,  setPreacher]  = useState(content?.preacher || '');

  useEffect(() => {
    onChange({ title, scripture, preacher });
  }, [title, scripture, preacher]);

  return (
    <div className={styles.inputGroup}>
      <div className={styles.field}>
        <label>설교 제목</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="은혜의 시작"
        />
      </div>
      <div className={styles.field}>
        <label>본문 구절</label>
        <input
          type="text"
          value={scripture}
          onChange={(e) => setScripture(e.target.value)}
          placeholder="창세기 1:1-3"
        />
      </div>
      <div className={styles.field}>
        <label>설교자</label>
        <input
          type="text"
          value={preacher}
          onChange={(e) => setPreacher(e.target.value)}
          placeholder="홍길동 목사"
        />
      </div>
    </div>
  );
}

export default SermonInput;
