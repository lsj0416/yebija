import { useState } from 'react';
import { RotateCcw, MoreVertical } from 'lucide-react';
import { ITEM_TYPE_LABELS } from '../../utils/itemMeta';
import { updateWorshipItem } from '../../api/worshipApi';
import BibleInput from './BibleInput';
import SermonInput from './SermonInput';
import PrayerInput from './PrayerInput';
import HymnInput from './HymnInput';
import ResponsiveInput from './ResponsiveInput';
import CustomInput from './CustomInput';
import FileAttachment from './FileAttachment';
import styles from './ItemEditor.module.css';

const TYPE_ICONS = {
  HYMN: '♪',
  BIBLE: '📖',
  RESPONSIVE_READING: '☰',
  PRAYER: '🙏',
  SERMON: '✝',
  CUSTOM: '✦',
};

// FILE 모드에서 파일 첨부가 필요한 타입
const FILE_TYPES = ['HYMN', 'RESPONSIVE_READING'];

function ItemEditor({ worshipId, item, onSaved }) {
  const [mode,           setMode]           = useState(item.mode);
  const [content,        setContent]        = useState(item.content || {});
  const [fileStorageKey, setFileStorageKey] = useState(item.fileStorageKey || null);
  const [saving,         setSaving]         = useState(false);
  const [saved,          setSaved]          = useState(false);
  const [error,          setError]          = useState('');

  const isFileMode = mode === 'FILE' && FILE_TYPES.includes(item.type);

  const handleSave = async () => {
    setSaving(true);
    setError('');
    try {
      const res = await updateWorshipItem(worshipId, item.id, {
        label: item.label,
        mode,
        content,
      });
      setSaved(true);
      setTimeout(() => setSaved(false), 2000);
      onSaved(res.data.data);
    } catch {
      setError('저장에 실패했습니다.');
    } finally {
      setSaving(false);
    }
  };

  const inputProps = { content, onChange: setContent, mode, onModeChange: setMode };

  return (
    <div className={styles.editor}>
      <div className={styles.header}>
        <div className={styles.titleRow}>
          <span className={styles.icon}>{TYPE_ICONS[item.type]}</span>
          <div>
            <h2 className={styles.title}>
              {item.label || ITEM_TYPE_LABELS[item.type]}
            </h2>
            <p className={styles.sub}>
              {ITEM_TYPE_LABELS[item.type]} · 슬라이드 구성 및 내용 입력
            </p>
          </div>
        </div>
        <div className={styles.headerActions}>
          <button className={styles.iconBtn} title="히스토리">
            <RotateCcw size={16} />
          </button>
          <button className={styles.iconBtn} title="더보기">
            <MoreVertical size={16} />
          </button>
        </div>
      </div>

      <div className={styles.body}>
        {item.type === 'BIBLE'              && <BibleInput {...inputProps} />}
        {item.type === 'SERMON'             && <SermonInput {...inputProps} />}
        {item.type === 'PRAYER'             && <PrayerInput {...inputProps} />}
        {item.type === 'HYMN'               && <HymnInput {...inputProps} />}
        {item.type === 'RESPONSIVE_READING' && <ResponsiveInput {...inputProps} />}
        {item.type === 'CUSTOM'             && <CustomInput {...inputProps} />}

        {/* FILE 모드 항목 — 파일 첨부 영역 */}
        {isFileMode && (
          <div className={styles.fileSection}>
            <p className={styles.fileSectionLabel}>PPT 파일 첨부</p>
            <FileAttachment
              itemId={item.id}
              fileStorageKey={fileStorageKey}
              onAttached={(key) => setFileStorageKey(key)}
            />
          </div>
        )}
      </div>

      {error && <p className={styles.error}>{error}</p>}

      <div className={styles.footer}>
        <button className={styles.removeBtn} type="button">
          Remove Section
        </button>
        <div className={styles.footerRight}>
          <button className={styles.secondaryBtn} type="button">
            Preview Slides
          </button>
          <button
            className={styles.primaryBtn}
            type="button"
            onClick={handleSave}
            disabled={saving}
          >
            {saving ? '저장 중...' : saved ? '✓ 저장됨' : 'Apply Changes'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ItemEditor;
