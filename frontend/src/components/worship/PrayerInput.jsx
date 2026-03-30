import { useState, useEffect } from 'react';
import styles from './ItemInput.module.css';

function PrayerInput({ content, onChange }) {
  const [role,   setRole]   = useState(content?.role || '');
  const [person, setPerson] = useState(content?.person || '');

  useEffect(() => {
    onChange({ role, person });
  }, [role, person]);

  return (
    <div className={styles.inputGroup}>
      <div className={styles.field}>
        <label>기도 구분</label>
        <input
          type="text"
          value={role}
          onChange={(e) => setRole(e.target.value)}
          placeholder="대표기도, 축도 등"
        />
      </div>
      <div className={styles.field}>
        <label>담당자</label>
        <input
          type="text"
          value={person}
          onChange={(e) => setPerson(e.target.value)}
          placeholder="홍길동 집사"
        />
      </div>
    </div>
  );
}

export default PrayerInput;
