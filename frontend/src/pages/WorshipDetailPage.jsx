import { useEffect, useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { GripVertical, Save, CheckCheck } from 'lucide-react';
import { getWorship, completeWorship } from '../api/worshipApi';
import { ITEM_TYPE_LABELS } from '../utils/itemMeta';
import ItemEditor from '../components/worship/ItemEditor';
import styles from './WorshipDetailPage.module.css';

const TYPE_ICONS = {
  HYMN: '♪',
  BIBLE: '📖',
  RESPONSIVE_READING: '☰',
  PRAYER: '🙏',
  SERMON: '✝',
  CUSTOM: '✦',
};

function WorshipDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [worship,      setWorship]      = useState(null);
  const [selectedItem, setSelectedItem] = useState(null);
  const [loading,      setLoading]      = useState(true);
  const [completing,   setCompleting]   = useState(false);

  useEffect(() => {
    getWorship(id)
      .then((res) => {
        const data = res.data.data;
        setWorship(data);
        if (data.items.length > 0) setSelectedItem(data.items[0]);
      })
      .catch(() => navigate('/worships'))
      .finally(() => setLoading(false));
  }, [id]);

  const handleItemSaved = (updatedWorship) => {
    setWorship(updatedWorship);
    const refreshed = updatedWorship.items.find((i) => i.id === selectedItem?.id);
    if (refreshed) setSelectedItem(refreshed);
  };

  const handleComplete = async () => {
    if (!window.confirm('예배 준비를 완료 상태로 변경할까요?')) return;
    setCompleting(true);
    try {
      const res = await completeWorship(id);
      setWorship(res.data.data);
    } catch {
      alert('상태 변경에 실패했습니다.');
    } finally {
      setCompleting(false);
    }
  };

  const formatDate = (dateStr) =>
    new Date(dateStr).toLocaleDateString('ko-KR', {
      year: 'numeric', month: 'long', day: 'numeric',
    });

  if (loading) return <div className={styles.loading}>불러오는 중...</div>;
  if (!worship)  return null;

  return (
    <div className={styles.container}>
      {/* 상단 브레드크럼 + 액션 */}
      <div className={styles.topBar}>
        <div className={styles.breadcrumb}>
          <Link to="/worships" className={styles.breadLink}>Worship</Link>
          <span className={styles.breadSep}>/</span>
          <span className={styles.breadCurrent}>
            {worship.title || formatDate(worship.worshipDate)}
          </span>
        </div>
        <div className={styles.topActions}>
          <button className={styles.draftBtn}>
            <Save size={14} /> Draft Saved
          </button>
          {worship.status !== 'COMPLETE' && (
            <button
              className={styles.mergeBtn}
              onClick={handleComplete}
              disabled={completing}
            >
              <CheckCheck size={14} />
              {completing ? '처리 중...' : 'Merge & Download PPT'}
            </button>
          )}
        </div>
      </div>

      <h1 className={styles.pageTitle}>
        {worship.title || formatDate(worship.worshipDate)}
      </h1>

      {/* 2패널 에디터 */}
      <div className={styles.panels}>
        {/* 좌측: 예배 순서 목록 */}
        <div className={styles.leftPanel}>
          <p className={styles.panelLabel}>ORDER OF WORSHIP</p>
          <ul className={styles.orderList}>
            {worship.items.map((item) => (
              <li
                key={item.id}
                className={`${styles.orderItem} ${selectedItem?.id === item.id ? styles.selected : ''}`}
                onClick={() => setSelectedItem(item)}
              >
                <GripVertical size={14} className={styles.grip} />
                <div className={styles.orderItemInfo}>
                  <p className={styles.orderItemName}>
                    {item.label || ITEM_TYPE_LABELS[item.type]}
                  </p>
                  {item.content && Object.keys(item.content).length > 0 && (
                    <p className={styles.orderItemSub}>
                      {getItemSummary(item)}
                    </p>
                  )}
                </div>
                <span className={styles.orderItemIcon}>
                  {TYPE_ICONS[item.type]}
                </span>
              </li>
            ))}
          </ul>

          <button className={styles.addItemBtn}>
            <span>+</span> Add Worship Element
          </button>
        </div>

        {/* 우측: 항목 편집기 */}
        <div className={styles.rightPanel}>
          {selectedItem ? (
            <ItemEditor
              key={selectedItem.id}
              worshipId={worship.id}
              item={selectedItem}
              onSaved={handleItemSaved}
            />
          ) : (
            <div className={styles.emptyEditor}>
              <p>왼쪽에서 항목을 선택해주세요</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

function getItemSummary(item) {
  const c = item.content;
  if (!c) return '';
  if (item.type === 'BIBLE')   return `${c.book} ${c.chapter}:${c.startVerse}-${c.endVerse}`;
  if (item.type === 'SERMON')  return c.title || '';
  if (item.type === 'PRAYER')  return c.person || '';
  if (item.type === 'HYMN')    return c.number ? `찬송가 ${c.number}장` : '';
  if (item.type === 'RESPONSIVE_READING') return c.number ? `교독문 ${c.number}번` : '';
  return c.text?.slice(0, 20) || '';
}

export default WorshipDetailPage;
