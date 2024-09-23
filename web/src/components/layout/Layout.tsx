import { ReactNode } from 'react';
import Footer from './footer/Footer';
import Header from './header/Header';
import Nav from './nav/Nav';
import styles from './Layout.module.css';

interface LayoutProps {
  children: ReactNode;
}

const Layout = ({ children }: LayoutProps) => {
  return (
    <div className={styles.layout}>
      <Header />
      <main style={{ boxSizing: 'border-box' }}>{children}</main>
      <Footer />
      <Nav />
    </div>
  );
};

export default Layout;
