import { NavLink, useNavigate } from 'react-router-dom';
import { Bell, HelpCircle, Search } from 'lucide-react';
import useAuthStore from '../../stores/authStore';
import styles from './TopNav.module.css';

const NAV_TABS = [
  { label: 'Dashboard', to: '/dashboard' },
  { label: 'Editor', to: '/worships' },
  { label: 'Settings', to: '/templates' },
];

function TopNav() {
  const navigate = useNavigate();
  const clearTokens = useAuthStore((s) => s.clearTokens);

  const handleLogout = () => {
    clearTokens();
    navigate('/login');
  };

  return (
    <header className={styles.nav}>
      <div className={styles.left}>
        <span className={styles.logo}>Yebija</span>
        <nav className={styles.tabs}>
          {NAV_TABS.map((tab) => (
            <NavLink
              key={tab.to}
              to={tab.to}
              className={({ isActive }) =>
                `${styles.tab} ${isActive ? styles.activeTab : ''}`
              }
            >
              {tab.label}
            </NavLink>
          ))}
        </nav>
      </div>

      <div className={styles.right}>
        <div className={styles.searchBox}>
          <Search size={14} className={styles.searchIcon} />
          <input placeholder="Search..." className={styles.searchInput} />
        </div>
        <button className={styles.iconBtn} title="Notifications">
          <Bell size={18} />
        </button>
        <button className={styles.iconBtn} title="Help">
          <HelpCircle size={18} />
        </button>
        <button className={styles.avatar} onClick={handleLogout} title="로그아웃">
          <span>A</span>
        </button>
      </div>
    </header>
  );
}

export default TopNav;
