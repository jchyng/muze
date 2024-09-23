import { Layout } from '@/components';
import DefaultProfile from '@/assets/Default_Profile.png';

import styles from './ActorDetail.module.css';
import { ChevronLeft } from 'lucide-react';

const ActorDetail = () => {
  return (
    <Layout>
      <div className={styles.actor_header}>
        <a href="javascript:history.back();" className={styles.back_button}>
          <ChevronLeft />
        </a>
        <div className="nameTag">
          <h1>배우 이름</h1>
        </div>
      </div>
      <div>
        <div>
          <div className={styles.name_tag}>
            <img
              src={DefaultProfile}
              alt="profileImage"
              className={styles.profile_img}
            />
          </div>
          <table>
            <tr>
              <td>
                출연 작품 <hr />
              </td>
            </tr>
            <tr>
              <td>
                <div>
                  <a href="#">
                    <img
                      src="#"
                      alt="posterImage"
                      className={styles.poster_img}
                    />
                  </a>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </Layout>
  );
};

export default ActorDetail;
