import { NavLink } from 'react-router-dom';
import {
  LayoutGrid,
  BookOpen,
  AlignLeft,
  FolderOpen,
  Settings,
  HelpCircle,
  Download,
} from 'lucide-react';
import styles from './Sidebar.module.css';

const NAV_ITEMS = [
  { icon: LayoutGrid, label: 'Dashboard',     to: '/dashboard' },
  { icon: AlignLeft,  label: 'Worship Order', to: '/worships' },
  { icon: BookOpen,   label: 'Scripture',     to: '/scripture' },
  { icon: FolderOpen, label: 'Templates',     to: '/templates' },
];

const BOTTOM_ITEMS = [
  { icon: Settings,   label: 'Settings',  to: '/settings' },
  { icon: HelpCircle, label: 'Support',   to: '/support' },
];

function Sidebar() {
  return (
    <aside className={styles.sidebar}>
      {/* Church info */}
      <div className={styles.churchCard}>
        <div className={styles.churchIcon}>
          <Download size={18} />
        </div>
        <div>
          <p className={styles.churchName}>내 교회</p>
          <p className={styles.churchSub}>예비자</p>
        </div>
      </div>

      {/* Main nav */}
      <nav className={styles.nav}>
        {NAV_ITEMS.map(({ icon: Icon, label, to }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `${styles.navItem} ${isActive ? styles.active : ''}`
            }
          >
            <Icon size={16} className={styles.navIcon} />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      {/* Bottom nav */}
      <div className={styles.bottom}>
        {BOTTOM_ITEMS.map(({ icon: Icon, label, to }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              `${styles.navItem} ${isActive ? styles.active : ''}`
            }
          >
            <Icon size={16} className={styles.navIcon} />
            <span>{label}</span>
          </NavLink>
        ))}

        <button className={styles.exportBtn}>
          <Download size={15} />
          Export PPT
        </button>
      </div>
    </aside>
  );
}

export default Sidebar;
