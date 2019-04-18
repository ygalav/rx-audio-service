package com.ifua.rx.audioservice.services;

import com.ifua.rx.audioservice.entities.Album;
import com.ifua.rx.audioservice.entities.Track;
import com.ifua.rx.audioservice.repos.AlbumJpaRepository;
import com.ifua.rx.audioservice.repos.TrackJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class AlbumServiceImpl implements AlbumService {

    private final AlbumJpaRepository albumJpaRepository;
    private final TrackJpaRepository trackJpaRepository;
    private final Scheduler jdbcScheduler;

    @Autowired
    public AlbumServiceImpl(AlbumJpaRepository albumJpaRepository, TrackJpaRepository trackJpaRepository, Scheduler jdbcScheduler) {
        this.albumJpaRepository = albumJpaRepository;
        this.trackJpaRepository = trackJpaRepository;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<Album> listTopLevelAlbums() {
        log.info("Getting list of Albums");
        return async(albumJpaRepository::findByTopLevelAlbumTrue).flux().flatMap(Flux::fromIterable);
    }

    @Override
    public Mono<Album> getAlbum(Integer id) {
        return async(() -> albumJpaRepository.findById(id))
                .flatMap(albumOpt -> albumOpt.isPresent() ? Mono.just(albumOpt.get()) : Mono.empty())
                .doOnNext(album -> log.info("Getting Album for id: [{}], item found [{}]", id, album.toString()));
    }

    @Override
    public Mono<Album> createAlbum(Mono<Album> newAlbum) {
        return newAlbum.flatMap(album -> async(() -> albumJpaRepository.save(album)));
    }

    @Override
    @Transactional
    public Mono<Void> attachAlbumToParent(Integer parentId, Integer childId) {
        return async(() -> {
            Optional<Album> parentOpt = albumJpaRepository.findById(parentId);
            Optional<Album> childOpt = albumJpaRepository.findById(childId);
            if (!Stream.of(parentOpt, childOpt).allMatch(Optional::isPresent)){
                return null;
            }
            Album parent = parentOpt.get();
            Album child = childOpt.get();

            if (parent.getAlbums() == null) {
                parent.setAlbums(new LinkedHashSet<>());
            }

            if (parent.getAlbums().stream().anyMatch(album -> album.getId().equals(childId))) {
                return null;
            }

            parent.getAlbums().add(child);
            albumJpaRepository.saveAndFlush(parent);
            return null;
        });
    }

    @Override
    @Transactional
    public Mono<Void> addTrackToParent(Integer albumId, Integer trackId) {
        return async(() -> {
            Optional<Album> albumOpt = albumJpaRepository.findById(albumId);
            Optional<Track> trackOpt = trackJpaRepository.findById(trackId);

            if (!Stream.of(albumOpt, trackOpt).allMatch(Optional::isPresent)){
                return null;
            }
            Album album = albumOpt.get();
            Track track = trackOpt.get();

            if (album.getTracks() == null) {
                album.setTracks(new LinkedHashSet<>());
            }

            if (album.getTracks().stream().anyMatch(track1 -> track.getId().equals(trackId))) {
                return null;
            }

            album.getTracks().add(track);
            albumJpaRepository.saveAndFlush(album);
            return null;
        });
    }

    @Override
    public Scheduler getScheduler() {
        return jdbcScheduler;
    }

}
