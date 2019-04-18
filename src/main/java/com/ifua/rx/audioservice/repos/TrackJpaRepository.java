package com.ifua.rx.audioservice.repos;

import com.ifua.rx.audioservice.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackJpaRepository extends JpaRepository<Track, Integer> {
    List<Track> findByAlbumId(Integer albumId);
    List<Track> findByAlbumIsNull();
}
