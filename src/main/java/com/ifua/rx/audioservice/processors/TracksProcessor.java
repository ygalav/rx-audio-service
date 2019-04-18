package com.ifua.rx.audioservice.processors;

import com.ifua.rx.audioservice.entities.Track;
import com.ifua.rx.audioservice.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class TracksProcessor {

    @Autowired
    private final TrackService trackService;

    public TracksProcessor(TrackService trackService) {
        this.trackService = trackService;
    }

    public Flux<Track> listTracks(ServerRequest request) {
        return trackService.listAllUnassignedTracks();
    }

    public Mono<Track> createTrack(ServerRequest request) {
        return trackService.createTrack(request.bodyToMono(Track.class));
    }
}
