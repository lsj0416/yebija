import { useState, useEffect } from 'react';
import styles from './ItemInput.module.css';

function ResponsiveInput({ content, onChange }) {
  const [number, setNumber] = useState(content?.number || '');

  useEffect(() => {
    onChange({ number });
  }, [number]);

  return (
    <div className={styles.inputGroup}>
      <div className={styles.field}>
        <label>교독문 번호</label>
        <input
          type="number"
          min={1}
          value={number}
          onChange={(e) => setNumber(e.target.value)}
          placeholder="예: 1"
        />
      </div>
      <p className={styles.hint}>
        교독문 파일을 파일 업로드 단계에서 첨부합니다.
      </p>
    </div>
  );
}

export default ResponsiveInput;
