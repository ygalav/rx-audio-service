package com.ifua.rx.audioservice.processors;

import com.ifua.rx.audioservice.entities.Album;
import com.ifua.rx.audioservice.entities.Track;
import com.ifua.rx.audioservice.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AlbumsProcessor {

    private final AlbumService albumService;

    @Autowired
    public AlbumsProcessor(AlbumService albumService) {
        this.albumService = albumService;
    }

    public Flux<Album> listTopLevelAlbums(final ServerRequest request) {
        return albumService.listTopLevelAlbums();
    }

    public Mono<Album> getAlbum(ServerRequest request) {
        Integer albumId = Integer.valueOf(request.pathVariable("id"));
        return albumService.getAlbum(albumId);
    }

    public Mono<Album> createAlbum(ServerRequest request) {
        return request.bodyToMono(Album.class).flatMap(album -> albumService.createAlbum(Mono.just(album)));
    }

    public Mono<Void> attachAlbumToParent(ServerRequest request) {
        Integer parentId = Integer.valueOf(request.pathVariable("parentId"));
        return request.bodyToMono(Album.class)
                .flatMap(child -> albumService.attachAlbumToParent(parentId, child.getId()));
    }

    public Mono<Void> addTrackToParent(ServerRequest request) {
        Integer albumId = Integer.valueOf(request.pathVariable("albumId"));
        return request.bodyToMono(Track.class)
                .flatMap(track -> albumService.addTrackToParent(albumId, track.getId()));
    }
}
