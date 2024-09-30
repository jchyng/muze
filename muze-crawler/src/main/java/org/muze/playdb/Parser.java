package org.muze.playdb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.muze.playdb.domain.Actor;
import org.muze.playdb.domain.Musical;
import org.muze.playdb.request.Genre;
import org.muze.playdb.request.LookupType;
import org.muze.playdb.request.URLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Parser {

    private Logger log = LoggerFactory.getLogger(Parser.class);
    private final int TIMEOUT = 60000;

    protected int getMaxPage(LookupType lookupType, Genre genre) throws IOException {
        String url = URLs.getMusicalUrl(lookupType, genre);

        Document doc = Jsoup.connect(url).timeout(TIMEOUT).get();
        Elements pageElements = doc.select("#contents .container1 > table > tbody > tr:last-child");

        String[] pageNums = pageElements.get(pageElements.size() - 1).text().split("/");

        try {
            return Integer.parseInt(pageNums[1].replaceAll("\\D+", ""));
        } catch (ArrayIndexOutOfBoundsException e) {
            log.info("{} {}에 존재하는 데이터가 없습니다.", lookupType.name(), genre);
            return 0;
        }
    }

    protected List<String> getAllMusicalIds(LookupType lookupType, Genre genre, int maxPage)
            throws IOException {
        String url = URLs.getMusicalUrl(lookupType, genre) + "&" + URLs.PAGE;

        List<String> musicalIds = new ArrayList<>();
        for (int currentPage = 1; currentPage <= maxPage; currentPage++) {
            Document doc = Jsoup.connect(url + currentPage).timeout(TIMEOUT).get();

            Element musicalElement = doc.selectFirst(
                    "#contents .container1 > table > tbody > tr:last-child");

            List<String> idTags = musicalElement.select("a[onclick^=goDetail]").eachAttr("onclick");

            for (String idTag : idTags) {
                String id = idTag.substring(idTag.indexOf("'") + 1, idTag.lastIndexOf("'"));
                musicalIds.add(id);
            }
        }
        return musicalIds;
    }

    protected Musical getMusical(String musicalId) throws ParseException, IOException {
        String url = URLs.getMusicalDetailUrl(musicalId);
        Document doc = Jsoup.connect(url).timeout(TIMEOUT).get();

        Element element = doc.selectFirst(".pddetail");

        String poster = element.selectFirst("h2 > img").attr("src");

        String title = "";
        Element titleElement = element.selectFirst(".title");
        if (titleElement != null) {
            title = titleElement.text();
        }

        String temporary = element.selectFirst("img[alt=일시]").parent().nextElementSibling().text();
        Date stDate = null;
        Date edDate = null;

        if (temporary.length() == 23) {
            String[] dates = temporary.trim().split(" ~ ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

            if (dates[0].length() == 10) {
                stDate = dateFormat.parse(dates[0]);
            }
            if (dates[1].length() == 10) {
                edDate = dateFormat.parse(dates[1]);
            }
        }

        String theater = element.selectFirst("img[alt=장소]").parent().nextElementSibling().text();

        Element viewAgeElement = element.selectFirst("img[alt=관람등급]");
        String viewAge = "";
        if (viewAgeElement != null) {
            viewAge = viewAgeElement.parent().nextElementSibling().text();
        }

        Element runningTimeElement = element.selectFirst("img[alt=관람시간]");
        String runningTime = "";
        if (runningTimeElement != null) {
            runningTime = runningTimeElement.parent().nextElementSibling().text();
        }

        Element mainCharacterElement = doc.selectFirst(
                ".detail_contentsbox > table > tbody > tr b");
        String mainCharacter = "";
        if (mainCharacterElement != null) {
            mainCharacter = mainCharacterElement.text();
        }

        return Musical.builder()
                .id(musicalId)
                .title(title)
                .theater(theater)
                .posterImage(poster)
                .stDate(stDate)
                .edDate(edDate)
                .viewAge(viewAge)
                .runningTime(runningTime)
                .mainCharacter(mainCharacter)
                .build();
    }

    protected List<Actor> getActors(String musicalId) throws IOException {
        String url = URLs.getMusicalDetailUrl(musicalId);
        Document doc = Jsoup.connect(url).timeout(TIMEOUT).get();

        Elements actorListByRole = doc.select(
                ".detail_contentsbox > table > tbody > tr > td > table > tbody > tr");
        List<Actor> actors = new ArrayList<>();

        for (Element e : actorListByRole) {
            String role = e.select("td[width=120]").text();
            Elements profileImages = e.select("td[width=80] > a > img");
            Elements IdAndName = e.select("td[width=52] > a");

            for (int i = 0; i < profileImages.size(); i++) {
                String src = profileImages.get(i).attr("src");
                String id = IdAndName.get(i).attr("href").split("=")[1];
                String name = IdAndName.get(i).text();

                Actor actor = Actor.builder()
                        .id(id)
                        .name(name)
                        .profileImage(src)
                        .role(role)
                        .build();
                actors.add(actor);
            }
        }
        return actors;
    }
}