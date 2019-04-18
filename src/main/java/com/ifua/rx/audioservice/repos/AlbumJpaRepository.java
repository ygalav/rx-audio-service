package com.ifua.rx.audioservice.repos;

import com.ifua.rx.audioservice.entities.Album;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumJpaRepository extends JpaRepository<Album, Integer> {

    @EntityGraph(value = "graph.Album")
    @Override
    Optional<Album> findById(Integer id);
    @EntityGraph(value = "graph.Album")
    List<Album> findByTopLevelAlbumTrue();
}
