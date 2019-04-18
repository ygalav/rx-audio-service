package com.ifua.rx.audioservice.services;

import com.ifua.rx.audioservice.entities.Track;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrackService extends AsyncProtectedService {
    Mono<Track> createTrack(Mono<Track> track);
    Flux<Track> listAllUnassignedTracks();
}
