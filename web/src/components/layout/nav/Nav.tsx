import styles from './Nav.module.css';
import { House, MessageCircleMore, UserRound } from 'lucide-react';

const Nav = () => {
  return (
    <nav className={styles.nav}>
      <a className={styles.nav_item} href="${baseURL}/main">
        <House />
        <div className={styles.nav_item_text}>HOME</div>
      </a>
      <a className={styles.nav_item} href="${baseURL}/community">
        <MessageCircleMore />
        <div className={styles.nav_item_text}>COMMUNITY</div>
      </a>
      <a className={styles.nav_item} href="${baseURL}/mypage/myinfo">
        <UserRound />
        <div className={styles.nav_item_text}>MY PAGE</div>
      </a>
    </nav>
  );
};

export default Nav;
