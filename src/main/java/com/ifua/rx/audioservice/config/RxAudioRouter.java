package com.ifua.rx.audioservice.config;

import com.ifua.rx.audioservice.entities.Album;
import com.ifua.rx.audioservice.entities.Track;
import com.ifua.rx.audioservice.processors.AlbumsProcessor;
import com.ifua.rx.audioservice.processors.TracksProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
public class RxAudioRouter {

    @Bean
    public RouterFunction<?> routes(TracksProcessor tracksProcessor, AlbumsProcessor albumsProcessor) {
        return nest(path("/audio/ua/"),
                nest(path("/albums"),
                        route(RequestPredicates.GET("/"),
                                request -> ok().body(albumsProcessor.listTopLevelAlbums(request), Album.class))
                        .andRoute(RequestPredicates.GET("/{id}"),
                                request -> ok().body(albumsProcessor.getAlbum(request), Album.class))
                        .andRoute(RequestPredicates.POST("/"),
                                request -> ok().body(albumsProcessor.createAlbum(request), Album.class))
                        .andRoute(RequestPredicates.POST("/{parentId}/albums"),
                                request -> ok().body(albumsProcessor.attachAlbumToParent(request), Void.class))
                        .andRoute(RequestPredicates.POST("/{albumId}/tracks"),
                                request -> ok().body(albumsProcessor.addTrackToParent(request), Void.class))
                )
                .andNest(path("/tracks"),
                        route(RequestPredicates.POST("/"),
                            request -> ok().body(tracksProcessor.createTrack(request), Track.class))
                        .andRoute(RequestPredicates.GET("/"),
                                request -> ok().body(tracksProcessor.listTracks(request), Track.class))
                )
        );
    }
}
