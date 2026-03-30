import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { signup } from '../api/authApi';
import useAuthStore from '../stores/authStore';
import styles from './AuthPage.module.css';

const DENOMINATIONS = [
  '대한예수교장로회(합동)',
  '대한예수교장로회(통합)',
  '기독교대한감리회',
  '기독교대한성결교회',
  '한국기독교장로회',
  '기타',
];

function RegisterPage() {
  const navigate = useNavigate();
  const setTokens = useAuthStore((s) => s.setTokens);

  const [form, setForm] = useState({
    name: '',
    denomination: '',
    adminEmail: '',
    password: '',
    passwordConfirm: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (form.password !== form.passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }
    if (form.password.length < 8) {
      setError('비밀번호는 8자 이상이어야 합니다.');
      return;
    }

    setLoading(true);
    try {
      const res = await signup(form.name, form.denomination, form.adminEmail, form.password);
      const { accessToken, refreshToken } = res.data.data;
      setTokens(accessToken, refreshToken);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || '회원가입에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>교회 등록</h1>
        <p className={styles.subtitle}>예비자 서비스를 시작하세요</p>

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="name">교회명</label>
            <input
              id="name"
              name="name"
              type="text"
              value={form.name}
              onChange={handleChange}
              placeholder="○○교회"
              required
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="denomination">교단</label>
            <select
              id="denomination"
              name="denomination"
              value={form.denomination}
              onChange={handleChange}
              required
            >
              <option value="">교단을 선택하세요</option>
              {DENOMINATIONS.map((d) => (
                <option key={d} value={d}>{d}</option>
              ))}
            </select>
          </div>

          <div className={styles.field}>
            <label htmlFor="adminEmail">관리자 이메일</label>
            <input
              id="adminEmail"
              name="adminEmail"
              type="email"
              value={form.adminEmail}
              onChange={handleChange}
              placeholder="admin@church.com"
              required
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="password">비밀번호</label>
            <input
              id="password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              placeholder="8자 이상"
              required
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="passwordConfirm">비밀번호 확인</label>
            <input
              id="passwordConfirm"
              name="passwordConfirm"
              type="password"
              value={form.passwordConfirm}
              onChange={handleChange}
              placeholder="비밀번호를 다시 입력하세요"
              required
            />
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <button type="submit" className={styles.submitBtn} disabled={loading}>
            {loading ? '등록 중...' : '교회 등록하기'}
          </button>
        </form>

        <p className={styles.link}>
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
}

export default RegisterPage;
