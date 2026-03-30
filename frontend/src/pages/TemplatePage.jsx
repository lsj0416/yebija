import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getTemplates, deleteTemplate } from '../api/templateApi';
import { ITEM_TYPE_LABELS } from '../utils/itemMeta';
import styles from './TemplatePage.module.css';

function TemplatePage() {
  const navigate = useNavigate();
  const [templates, setTemplates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchTemplates();
  }, []);

  const fetchTemplates = async () => {
    try {
      const res = await getTemplates();
      setTemplates(res.data.data);
    } catch {
      setError('템플릿을 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`"${name}" 템플릿을 삭제할까요?`)) return;
    try {
      await deleteTemplate(id);
      setTemplates((prev) => prev.filter((t) => t.id !== id));
    } catch {
      alert('삭제에 실패했습니다.');
    }
  };

  return (
    <div className={styles.container}>
      <header className={styles.header}>
        <h1>예배 순서 템플릿</h1>
        <button className={styles.createBtn} onClick={() => navigate('/templates/new')}>
          + 새 템플릿
        </button>
      </header>

      {loading && <p className={styles.empty}>불러오는 중...</p>}
      {error && <p className={styles.error}>{error}</p>}

      {!loading && !error && templates.length === 0 && (
        <div className={styles.empty}>
          <p>등록된 템플릿이 없습니다.</p>
          <button className={styles.createBtn} onClick={() => navigate('/templates/new')}>
            첫 템플릿 만들기
          </button>
        </div>
      )}

      <ul className={styles.list}>
        {templates.map((t) => (
          <li key={t.id} className={`${styles.card} ${t.isDefault ? styles.defaultCard : ''}`}>
            <div className={styles.cardTop}>
              <div>
                <span className={styles.templateName}>{t.name}</span>
                {t.isDefault && <span className={styles.defaultBadge}>기본</span>}
                {t.description && <p className={styles.desc}>{t.description}</p>}
              </div>
              <div className={styles.actions}>
                <Link to={`/templates/${t.id}/edit`} className={styles.editBtn}>수정</Link>
                <button
                  className={styles.deleteBtn}
                  onClick={() => handleDelete(t.id, t.name)}
                >
                  삭제
                </button>
              </div>
            </div>

            <ol className={styles.itemList}>
              {t.items.map((item) => (
                <li key={item.id}>
                  <span className={styles.seq}>{item.seq}.</span>
                  <span className={styles.itemLabel}>
                    {item.label || ITEM_TYPE_LABELS[item.type]}
                  </span>
                  <span className={styles.itemType}>{ITEM_TYPE_LABELS[item.type]}</span>
                  <span className={`${styles.mode} ${styles[item.defaultMode]}`}>
                    {item.defaultMode}
                  </span>
                </li>
              ))}
            </ol>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default TemplatePage;
