import { useState, useEffect } from 'react';
import styles from './ItemInput.module.css';

function CustomInput({ content, onChange }) {
  const [text, setText] = useState(content?.text || '');

  useEffect(() => {
    onChange({ text });
  }, [text]);

  return (
    <div className={styles.inputGroup}>
      <div className={styles.field}>
        <label>내용</label>
        <textarea
          value={text}
          onChange={(e) => setText(e.target.value)}
          placeholder="항목 내용을 입력하세요"
          rows={4}
          className={styles.textarea}
        />
      </div>
    </div>
  );
}

export default CustomInput;
