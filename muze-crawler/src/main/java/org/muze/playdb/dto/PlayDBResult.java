package org.muze.playdb.dto;

import lombok.Getter;
import org.muze.playdb.domain.Actor;
import org.muze.playdb.domain.Casting;
import org.muze.playdb.domain.Musical;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class PlayDBResult {
    Set<Musical> musicals = new HashSet<>();
    Set<Actor> actors = new HashSet<>();
    Set<Casting> castings = new HashSet<>();

    public void addMusical(Musical musical) {
        musicals.add(musical);
    }

    public void addActors(List<Actor> actors) {
        actors.addAll(actors);
    }

    public void addCasting(Casting casting) {
        castings.add(casting);
    }

    public void union(PlayDBResult result) {
        musicals.addAll(result.getMusicals());
        actors.addAll(result.getActors());
        castings.addAll(result.getCastings());
    }
}
