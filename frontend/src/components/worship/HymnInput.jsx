import { useState, useEffect } from 'react';
import styles from './ItemInput.module.css';

function HymnInput({ content, mode, onModeChange, onChange }) {
  const [number, setNumber] = useState(content?.number || '');
  const [title,  setTitle]  = useState(content?.title || '');

  useEffect(() => {
    onChange({ number, title });
  }, [number, title]);

  return (
    <div className={styles.inputGroup}>
      <div className={styles.modeToggle}>
        <button
          type="button"
          className={mode === 'FILE' ? styles.modeActive : styles.modeBtn}
          onClick={() => onModeChange('FILE')}
        >
          FILE (파일 첨부)
        </button>
        <button
          type="button"
          className={mode === 'AUTO' ? styles.modeActive : styles.modeBtn}
          onClick={() => onModeChange('AUTO')}
        >
          AUTO (자동 생성)
        </button>
      </div>

      <div className={styles.field}>
        <label>찬송가 번호</label>
        <input
          type="number"
          min={1}
          value={number}
          onChange={(e) => setNumber(e.target.value)}
          placeholder="예: 10"
        />
      </div>
      <div className={styles.field}>
        <label>제목 (선택)</label>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          placeholder="찬송가 제목"
        />
      </div>

      {mode === 'FILE' && (
        <p className={styles.hint}>
          파일은 예배 생성 후 파일 업로드 단계에서 첨부합니다.
        </p>
      )}
    </div>
  );
}

export default HymnInput;
