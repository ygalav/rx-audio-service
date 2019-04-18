package com.ifua.rx.audioservice.services;

import com.ifua.rx.audioservice.repos.TrackJpaRepository;
import com.ifua.rx.audioservice.entities.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Component
public class TrackServiceImpl implements TrackService {

    private final Scheduler jdbcScheduler;
    private final TrackJpaRepository trackJpaRepository;

    @Autowired
    public TrackServiceImpl(Scheduler jdbcScheduler, TrackJpaRepository trackJpaRepository) {
        this.jdbcScheduler = jdbcScheduler;
        this.trackJpaRepository = trackJpaRepository;
    }

    @Override
    public Mono<Track> createTrack(Mono<Track> trackMono) {
        return trackMono.flatMap(track -> async(() -> trackJpaRepository.save(track)));
    }

    @Override
    public Flux<Track> listAllUnassignedTracks() {
        return async(trackJpaRepository::findByAlbumIsNull).flux().flatMap(Flux::fromIterable);
    }

    @Override
    public Scheduler getScheduler() {
        return jdbcScheduler;
    }
}
