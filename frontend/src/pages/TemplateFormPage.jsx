import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { createTemplate, getTemplate, updateTemplate } from '../api/templateApi';
import { ITEM_TYPES, ITEM_TYPE_LABELS, ITEM_TYPE_DEFAULT_MODE } from '../utils/itemMeta';
import styles from './TemplateFormPage.module.css';

const newItem = (seq) => ({
  _key: Date.now() + seq,
  type: 'HYMN',
  seq,
  label: '',
  defaultMode: ITEM_TYPE_DEFAULT_MODE['HYMN'],
});

function TemplateFormPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const isEdit = !!id;

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [isDefault, setIsDefault] = useState(false);
  const [items, setItems] = useState([newItem(1)]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isEdit) return;
    getTemplate(id)
      .then((res) => {
        const t = res.data.data;
        setName(t.name);
        setDescription(t.description || '');
        setIsDefault(t.isDefault);
        setItems(t.items.map((item) => ({ ...item, _key: item.id })));
      })
      .catch(() => setError('템플릿을 불러오지 못했습니다.'));
  }, [id, isEdit]);

  const handleItemChange = (index, field, value) => {
    setItems((prev) => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      if (field === 'type') {
        updated[index].defaultMode = ITEM_TYPE_DEFAULT_MODE[value];
      }
      return updated;
    });
  };

  const addItem = () => {
    setItems((prev) => [...prev, newItem(prev.length + 1)]);
  };

  const removeItem = (index) => {
    setItems((prev) =>
      prev
        .filter((_, i) => i !== index)
        .map((item, i) => ({ ...item, seq: i + 1 }))
    );
  };

  const moveItem = (index, direction) => {
    const next = index + direction;
    if (next < 0 || next >= items.length) return;
    setItems((prev) => {
      const updated = [...prev];
      [updated[index], updated[next]] = [updated[next], updated[index]];
      return updated.map((item, i) => ({ ...item, seq: i + 1 }));
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (items.length === 0) {
      setError('항목을 하나 이상 추가해주세요.');
      return;
    }
    setError('');
    setLoading(true);

    const payload = {
      name,
      description,
      isDefault,
      items: items.map(({ type, seq, label, defaultMode }) => ({
        type,
        seq,
        label: label || null,
        defaultMode,
      })),
    };

    try {
      if (isEdit) {
        await updateTemplate(id, payload);
      } else {
        await createTemplate(payload);
      }
      navigate('/templates');
    } catch (err) {
      setError(err.response?.data?.message || '저장에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <button className={styles.backBtn} onClick={() => navigate('/templates')}>← 목록</button>
        <h1>{isEdit ? '템플릿 수정' : '새 템플릿'}</h1>
      </header>

      <form onSubmit={handleSubmit} className={styles.form}>
        <section className={styles.section}>
          <div className={styles.field}>
            <label>템플릿 이름 *</label>
            <input
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="주일 예배 순서"
              maxLength={100}
              required
            />
          </div>

          <div className={styles.field}>
            <label>설명</label>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="템플릿 설명 (선택)"
              maxLength={255}
            />
          </div>

          <label className={styles.checkboxLabel}>
            <input
              type="checkbox"
              checked={isDefault}
              onChange={(e) => setIsDefault(e.target.checked)}
            />
            기본 템플릿으로 설정
          </label>
        </section>

        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2>예배 순서 항목</h2>
            <button type="button" className={styles.addBtn} onClick={addItem}>
              + 항목 추가
            </button>
          </div>

          <ul className={styles.itemList}>
            {items.map((item, index) => (
              <li key={item._key} className={styles.itemRow}>
                <span className={styles.seq}>{index + 1}</span>

                <div className={styles.itemFields}>
                  <select
                    value={item.type}
                    onChange={(e) => handleItemChange(index, 'type', e.target.value)}
                  >
                    {ITEM_TYPES.map((t) => (
                      <option key={t} value={t}>{ITEM_TYPE_LABELS[t]}</option>
                    ))}
                  </select>

                  <input
                    type="text"
                    value={item.label}
                    onChange={(e) => handleItemChange(index, 'label', e.target.value)}
                    placeholder="레이블 (선택)"
                    maxLength={50}
                  />

                  <select
                    value={item.defaultMode}
                    onChange={(e) => handleItemChange(index, 'defaultMode', e.target.value)}
                  >
                    <option value="AUTO">AUTO</option>
                    <option value="FILE">FILE</option>
                  </select>
                </div>

                <div className={styles.itemActions}>
                  <button type="button" onClick={() => moveItem(index, -1)} disabled={index === 0}>↑</button>
                  <button type="button" onClick={() => moveItem(index, 1)} disabled={index === items.length - 1}>↓</button>
                  <button type="button" className={styles.removeBtn} onClick={() => removeItem(index)}>✕</button>
                </div>
              </li>
            ))}
          </ul>

          {items.length === 0 && (
            <p className={styles.emptyItems}>항목을 추가해주세요.</p>
          )}
        </section>

        {error && <p className={styles.error}>{error}</p>}

        <div className={styles.footer}>
          <button type="button" className={styles.cancelBtn} onClick={() => navigate('/templates')}>
            취소
          </button>
          <button type="submit" className={styles.submitBtn} disabled={loading}>
            {loading ? '저장 중...' : isEdit ? '수정 완료' : '템플릿 생성'}
          </button>
        </div>
      </form>
    </div>
  );
}

export default TemplateFormPage;
