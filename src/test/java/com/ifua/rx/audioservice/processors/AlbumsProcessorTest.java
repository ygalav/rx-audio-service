package com.ifua.rx.audioservice.processors;

import com.ifua.rx.audioservice.entities.Album;
import com.ifua.rx.audioservice.entities.Track;
import com.ifua.rx.audioservice.repos.AlbumJpaRepository;
import com.ifua.rx.audioservice.repos.TrackJpaRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AlbumsProcessorTest {

    @Autowired
    private AlbumsProcessor albumsProcessor;
    @Autowired
    private AlbumJpaRepository albumJpaRepository;
    @Autowired
    private TrackJpaRepository trackJpaRepository;

    private Album album1;

    @Before
    public void setUp() throws Exception {

        album1 = createAlbum(true);
        album1 = albumJpaRepository.save(album1);

        Track track4Album1 = createTrack();
        track4Album1.setAlbum(album1);
        trackJpaRepository.save(track4Album1);
    }

    private Track createTrack() {
        Track track = new Track();
        track.setName(randomString());
        track.setUrl(randomString());
        return track;
    }

    private Album createAlbum(boolean isTopLevel) {
        Album album = new Album();
        album.setName(randomString());
        album.setTopLevelAlbum(isTopLevel);
        return album;
    }

    @Test
    public void shouldProperlyListTopLevelAlbumsOnly() {
        albumJpaRepository.save(createAlbum(true));
        albumJpaRepository.save(createAlbum(true));
        albumJpaRepository.save(createAlbum(false));

        StepVerifier.create(albumsProcessor.listTopLevelAlbums(null))
                .expectNextCount(albumJpaRepository.findByTopLevelAlbumTrue().size())
                .verifyComplete();
    }

    @Test
    public void shouldProperlyGetSingleAlbum() {

        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("id")).thenReturn(album1.getId().toString());
        StepVerifier.create(albumsProcessor.getAlbum(request))
                .expectNextMatches(album -> album.getId().equals(album1.getId()))
                .verifyComplete();
    }
    @Test
    public void shouldProperlyCreateAlbum() {
        Album albumToCreate = createAlbum(true);
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(Album.class)).thenReturn(Mono.just(albumToCreate));
        //When
        StepVerifier.create(albumsProcessor.createAlbum(request))
                .expectNextMatches(album -> album.getId() != null && albumJpaRepository.findById(album.getId()).isPresent())
                .verifyComplete();
    }

    @Test
    public void shouldProperlyAddAlbumToParent() {
        Album childAlbum = albumJpaRepository.save(createAlbum(false));
        Album parentAlbum = albumJpaRepository.save(createAlbum(false));
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(Album.class)).thenReturn(Mono.just(childAlbum));
        when(request.pathVariable("parentId")).thenReturn(parentAlbum.getId().toString());
        albumsProcessor.attachAlbumToParent(request).block();
        Album child = albumJpaRepository.findById(parentAlbum.getId()).get().getAlbums().stream().findFirst().get();
        assertEquals(childAlbum.getId(), child.getId());
    }

    @Test
    public void shouldProperlyAddTrackToAlbum() {
        Album album = albumJpaRepository.save(createAlbum(false));
        Track track = trackJpaRepository.save(createTrack());
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(Track.class)).thenReturn(Mono.just(track));
        when(request.pathVariable("albumId")).thenReturn(album.getId().toString());

        albumsProcessor.addTrackToParent(request).block();
        album = albumJpaRepository.findById(album.getId()).get();
        boolean hasTrack = album.getTracks().stream().anyMatch(trackInAlbum -> Objects.equals(track.getId(), trackInAlbum.getId()));
        assertTrue(hasTrack);
    }


    @After
    public void tearDown() throws Exception {
        //trackJpaRepository.deleteAll();
        //albumJpaRepository.deleteAll();
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }
}