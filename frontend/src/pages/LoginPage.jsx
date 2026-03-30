import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../api/authApi';
import useAuthStore from '../stores/authStore';
import styles from './AuthPage.module.css';

function LoginPage() {
  const navigate = useNavigate();
  const setTokens = useAuthStore((s) => s.setTokens);

  const [form, setForm] = useState({ adminEmail: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(form.adminEmail, form.password);
      const { accessToken, refreshToken } = res.data.data;
      setTokens(accessToken, refreshToken);
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message || '로그인에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>예비자</h1>
        <p className={styles.subtitle}>예배 준비 올인원 플랫폼</p>

        <form onSubmit={handleSubmit} className={styles.form}>
          <div className={styles.field}>
            <label htmlFor="adminEmail">이메일</label>
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
              placeholder="비밀번호를 입력하세요"
              required
            />
          </div>

          {error && <p className={styles.error}>{error}</p>}

          <button type="submit" className={styles.submitBtn} disabled={loading}>
            {loading ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <p className={styles.link}>
          아직 계정이 없으신가요? <Link to="/register">교회 등록하기</Link>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
