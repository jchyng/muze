import { Layout } from '@/components';

import styles from './Home.module.css';
import { ChevronLeft, ChevronRight, Search } from 'lucide-react';
import { Swiper, SwiperSlide } from 'swiper/react';
import { Swiper as SwiperType } from 'swiper';
import { Navigation } from 'swiper/modules';
import 'swiper/css';
import { useRef, useState } from 'react';

const Home = () => {
  const [communitySwiper, setCommunitySwiper] = useState<SwiperType | null>(
    null
  );
  const [recommendSwiper, setRecommendSwiper] = useState<SwiperType | null>(
    null
  );
  const [musicalSwiper, setMusicalSwiper] = useState<SwiperType | null>(null);
  return (
    <Layout>
      {/* 메인 검색 form */}
      <form action="#" className={styles.main_search_form}>
        <input
          type="text"
          name="keyword"
          className={styles.main_search}
          placeholder="작품, 배우, 게시글, 태그를 검색해 보세요"
        />
        <button className={styles.main_search_button}>
          <Search />
        </button>
      </form>

      {/* 인기 뮤지컬 */}
      <span className={styles.category_title}>인기 뮤지컬 🔥</span>
      <ul className={styles.popular_musical_list}>
        <a href="#" className={styles.popular_musical_item}>
          <li>1. 뮤지컬 제목</li>
        </a>
        <a href="#" className={styles.popular_musical_item}>
          <li>2. 뮤지컬 제목</li>
        </a>
      </ul>

      {/* 커뮤니티 인기글 */}
      <span className={styles.category_title}>커뮤니티 인기글 🔥</span>
      <div className={styles.popular_community_swiper}>
        <button
          className={styles.swiper_navigation_button}
          onClick={() => communitySwiper?.slidePrev()}
        >
          <ChevronLeft />
        </button>
        <Swiper
          spaceBetween={8}
          slidesPerView={3}
          navigation={true}
          modules={[Navigation]}
          onSwiper={setCommunitySwiper}
        >
          <SwiperSlide className={styles.swiper_slide}>
            <a href="#" className={styles.community_card}>
              <p className={styles.card_title}>카드 제목</p>
              <span className="author">카드 작성자</span>
            </a>
          </SwiperSlide>
          <SwiperSlide className={styles.swiper_slide}>
            <a href="#" className={styles.community_card}>
              <p className={styles.card_title}>카드 제목</p>
              <span className="author">카드 작성자</span>
            </a>
          </SwiperSlide>
          <SwiperSlide className={styles.swiper_slide}>
            <a href="#" className={styles.community_card}>
              <p className={styles.card_title}>카드 제목</p>
              <span className="author">카드 작성자</span>
            </a>
          </SwiperSlide>
          <SwiperSlide className={styles.swiper_slide}>
            <a href="#" className={styles.community_card}>
              <p className={styles.card_title}>카드 제목</p>
              <span className="author">카드 작성자</span>
            </a>
          </SwiperSlide>
          <SwiperSlide className={styles.swiper_slide}>
            <a href="#" className={styles.community_card}>
              <p className={styles.card_title}>카드 제목</p>
              <span className="author">카드 작성자</span>
            </a>
          </SwiperSlide>
        </Swiper>
        <button
          className={styles.swiper_navigation_button}
          onClick={() => communitySwiper?.slideNext()}
        >
          <ChevronRight />
        </button>
      </div>

      {/* 추천 배우 */}
      <span className={styles.category_title}>추천 배우✨</span>
      <div className={styles.today_actor_box}>
        <a id="actorInfo" href="#" className={styles.today_actor}>
          <img
            id="actorImage"
            src="https://blog.kakaocdn.net/dn/bh3D0M/btqB2IJwyS8/gjckk2Kc75QcomPVDI8TT0/img.jpg"
            alt="해당 배우는 이미지가 없습니다."
            className={styles.today_actor_image}
          />
          <span className={styles.today_actor_name}>홍광호</span>
        </a>
        <div className={styles.today_actor_swiper}>
          <button
            className={styles.swiper_navigation_button}
            onClick={() => recommendSwiper?.slidePrev()}
          >
            <ChevronLeft />
          </button>
          <Swiper
            spaceBetween={8}
            slidesPerView={2}
            navigation={true}
            modules={[Navigation]}
            onSwiper={setRecommendSwiper}
            slideClass={styles.swiper_slide}
          >
            <SwiperSlide className={styles.swiper_slide}>
              <a href="#" className={styles.today_actor_poster}>
                <img
                  src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                  alt="posterImage"
                  className={styles.poster_image}
                />
              </a>
            </SwiperSlide>
            <SwiperSlide className={styles.swiper_slide}>
              <a href="#" className={styles.today_actor_poster}>
                <img
                  src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                  alt="posterImage"
                  className={styles.poster_image}
                />
              </a>
            </SwiperSlide>
            <SwiperSlide className={styles.swiper_slide}>
              <a href="#" className={styles.today_actor_poster}>
                <img
                  src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                  alt="posterImage"
                  className={styles.poster_image}
                />
              </a>
            </SwiperSlide>
          </Swiper>
          <button
            className={styles.swiper_navigation_button}
            onClick={() => recommendSwiper?.slideNext()}
          >
            <ChevronRight />
          </button>
        </div>
      </div>

      <span className={styles.category_title}>공연 중인 뮤지컬 🤩</span>
      <div className={styles.progress_musical_swiper}>
        <button
          className={styles.swiper_navigation_button}
          onClick={() => musicalSwiper?.slidePrev()}
        >
          <ChevronLeft />
        </button>
        <Swiper
          spaceBetween={8}
          slidesPerView={3}
          navigation={true}
          modules={[Navigation]}
          onSwiper={setMusicalSwiper}
        >
          <SwiperSlide>
            <a href="#">
              <img
                src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                alt="posterImage"
                className={styles.poster_image}
              />
            </a>
          </SwiperSlide>
          <SwiperSlide>
            <a href="#">
              <img
                src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                alt="posterImage"
                className={styles.poster_image}
              />
            </a>
          </SwiperSlide>
          <SwiperSlide>
            <a href="#">
              <img
                src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                alt="posterImage"
                className={styles.poster_image}
              />
            </a>
          </SwiperSlide>
          <SwiperSlide>
            <a href="#">
              <img
                src="https://cdn.m-i.kr/news/photo/202101/784601_561474_542.jpg"
                alt="posterImage"
                className={styles.poster_image}
              />
            </a>
          </SwiperSlide>
        </Swiper>
        <button
          className={styles.swiper_navigation_button}
          onClick={() => musicalSwiper?.slideNext()}
        >
          <ChevronRight />
        </button>
      </div>
    </Layout>
  );
};

export default Home;
