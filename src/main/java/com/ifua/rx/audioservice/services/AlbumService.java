package com.ifua.rx.audioservice.services;

import com.ifua.rx.audioservice.entities.Album;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AlbumService extends AsyncProtectedService {

    Flux<Album> listTopLevelAlbums();

    Mono<Album> getAlbum(Integer id);

    Mono<Album> createAlbum(Mono<Album> newAlbum);

    Mono<Void> attachAlbumToParent(Integer parentId, Integer childId);

    @Transactional
    Mono<Void> addTrackToParent(Integer albumId, Integer trackId);
}
