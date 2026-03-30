import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plus, Calendar, CheckCircle, Clock } from 'lucide-react';
import { getWorships, createWorship, deleteWorship } from '../api/worshipApi';
import { getTemplates } from '../api/templateApi';
import styles from './WorshipPage.module.css';

function WorshipPage() {
  const navigate = useNavigate();
  const [worships,   setWorships]   = useState([]);
  const [templates,  setTemplates]  = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [showModal,  setShowModal]  = useState(false);
  const [form,       setForm]       = useState({ worshipDate: '', title: '', templateId: '' });
  const [creating,   setCreating]   = useState(false);
  const [error,      setError]      = useState('');

  useEffect(() => {
    Promise.all([getWorships(), getTemplates()])
      .then(([wRes, tRes]) => {
        setWorships(wRes.data.data);
        setTemplates(tRes.data.data);
      })
      .catch(() => setError('데이터를 불러오지 못했습니다.'))
      .finally(() => setLoading(false));
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setCreating(true);
    try {
      const res = await createWorship({
        worshipDate: form.worshipDate,
        title:       form.title || null,
        templateId:  form.templateId ? Number(form.templateId) : null,
      });
      navigate(`/worships/${res.data.data.id}`);
    } catch (err) {
      setError(err.response?.data?.message || '생성에 실패했습니다.');
      setCreating(false);
    }
  };

  const handleDelete = async (id, title) => {
    if (!window.confirm(`"${title || '예배'}"를 삭제할까요?`)) return;
    try {
      await deleteWorship(id);
      setWorships((prev) => prev.filter((w) => w.id !== id));
    } catch {
      alert('삭제에 실패했습니다.');
    }
  };

  const formatDate = (dateStr) =>
    new Date(dateStr).toLocaleDateString('ko-KR', {
      year: 'numeric', month: 'long', day: 'numeric',
    });

  return (
    <div className={styles.container}>
      <div className={styles.pageHeader}>
        <div>
          <h1>예배 준비</h1>
          <p className={styles.pageSubtitle}>예배 순서를 구성하고 슬라이드를 준비합니다</p>
        </div>
        <button className={styles.createBtn} onClick={() => setShowModal(true)}>
          <Plus size={16} /> 새 예배 만들기
        </button>
      </div>

      {error && <p className={styles.error}>{error}</p>}
      {loading && <p className={styles.empty}>불러오는 중...</p>}

      {!loading && worships.length === 0 && (
        <div className={styles.emptyState}>
          <Calendar size={40} className={styles.emptyIcon} />
          <p>아직 예배가 없습니다</p>
          <button className={styles.createBtn} onClick={() => setShowModal(true)}>
            첫 예배 만들기
          </button>
        </div>
      )}

      <div className={styles.grid}>
        {worships.map((w) => (
          <div
            key={w.id}
            className={styles.card}
            onClick={() => navigate(`/worships/${w.id}`)}
          >
            <div className={styles.cardTop}>
              <div>
                <p className={styles.worshipDate}>{formatDate(w.worshipDate)}</p>
                <h3 className={styles.worshipTitle}>{w.title || '제목 없음'}</h3>
              </div>
              <span className={`${styles.badge} ${w.status === 'COMPLETE' ? styles.complete : styles.draft}`}>
                {w.status === 'COMPLETE'
                  ? <><CheckCircle size={12} /> 완료</>
                  : <><Clock size={12} /> 준비 중</>}
              </span>
            </div>
            <p className={styles.itemCount}>{w.items.length}개 항목</p>
            <button
              className={styles.deleteBtn}
              onClick={(e) => { e.stopPropagation(); handleDelete(w.id, w.title); }}
            >
              삭제
            </button>
          </div>
        ))}
      </div>

      {/* 생성 모달 */}
      {showModal && (
        <div className={styles.overlay} onClick={() => setShowModal(false)}>
          <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
            <h2>새 예배 만들기</h2>
            <form onSubmit={handleCreate} className={styles.modalForm}>
              <div className={styles.field}>
                <label>예배 날짜 *</label>
                <input
                  type="date"
                  value={form.worshipDate}
                  onChange={(e) => setForm((p) => ({ ...p, worshipDate: e.target.value }))}
                  required
                />
              </div>
              <div className={styles.field}>
                <label>제목 (선택)</label>
                <input
                  type="text"
                  value={form.title}
                  onChange={(e) => setForm((p) => ({ ...p, title: e.target.value }))}
                  placeholder="주일 예배"
                />
              </div>
              <div className={styles.field}>
                <label>템플릿</label>
                <select
                  value={form.templateId}
                  onChange={(e) => setForm((p) => ({ ...p, templateId: e.target.value }))}
                >
                  <option value="">템플릿 없이 시작</option>
                  {templates.map((t) => (
                    <option key={t.id} value={t.id}>{t.name}</option>
                  ))}
                </select>
              </div>
              {error && <p className={styles.error}>{error}</p>}
              <div className={styles.modalFooter}>
                <button type="button" className={styles.cancelBtn} onClick={() => setShowModal(false)}>
                  취소
                </button>
                <button type="submit" className={styles.submitBtn} disabled={creating}>
                  {creating ? '생성 중...' : '예배 만들기'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default WorshipPage;
