import { useRef, useState } from 'react';
import { Upload, FileCheck, Trash2 } from 'lucide-react';
import { attachFileToItem, deleteFile } from '../../api/fileApi';
import styles from './FileAttachment.module.css';

function FileAttachment({ itemId, fileStorageKey, onAttached }) {
  const inputRef = useRef(null);
  const [uploading, setUploading] = useState(false);
  const [error,     setError]     = useState('');
  const [attached,  setAttached]  = useState(
    fileStorageKey ? { storageKey: fileStorageKey } : null
  );

  const handleFile = async (file) => {
    if (!file) return;
    if (!file.name.toLowerCase().endsWith('.pptx')) {
      setError('.pptx 파일만 업로드할 수 있습니다. (.ppt는 지원하지 않습니다)');
      return;
    }
    setError('');
    setUploading(true);
    try {
      const res = await attachFileToItem(itemId, file);
      const data = res.data.data;
      setAttached(data);
      onAttached(data.storageKey);
    } catch (err) {
      const code = err.response?.data?.error?.code;
      setError(
        code === 'FILE_INVALID_TYPE'
          ? '올바른 .pptx 파일이 아닙니다. 구형 .ppt 파일은 지원하지 않습니다.'
          : '업로드에 실패했습니다.'
      );
    } finally {
      setUploading(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    handleFile(e.dataTransfer.files[0]);
  };

  const handleRemove = async () => {
    if (!attached?.id) {
      setAttached(null);
      onAttached(null);
      return;
    }
    try {
      await deleteFile(attached.id);
      setAttached(null);
      onAttached(null);
    } catch {
      setError('파일 삭제에 실패했습니다.');
    }
  };

  if (attached) {
    return (
      <div className={styles.attached}>
        <FileCheck size={16} className={styles.fileIcon} />
        <span className={styles.fileName}>
          {attached.originalName || attached.storageKey}
        </span>
        <button
          type="button"
          className={styles.removeBtn}
          onClick={handleRemove}
          title="파일 제거"
        >
          <Trash2 size={14} />
        </button>
      </div>
    );
  }

  return (
    <div className={styles.wrapper}>
      <div
        className={styles.dropZone}
        onClick={() => inputRef.current?.click()}
        onDrop={handleDrop}
        onDragOver={(e) => e.preventDefault()}
      >
        <Upload size={20} className={styles.uploadIcon} />
        <p className={styles.dropText}>
          {uploading ? '업로드 중...' : '.pptx 파일을 드래그하거나 클릭하여 선택'}
        </p>
      </div>
      <input
        ref={inputRef}
        type="file"
        accept=".pptx"
        className={styles.hidden}
        onChange={(e) => handleFile(e.target.files[0])}
      />
      {error && <p className={styles.error}>{error}</p>}
    </div>
  );
}

export default FileAttachment;
