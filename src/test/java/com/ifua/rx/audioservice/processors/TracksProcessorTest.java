package com.ifua.rx.audioservice.processors;

import com.ifua.rx.audioservice.entities.Track;
import com.ifua.rx.audioservice.repos.TrackJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Slf4j
public class TracksProcessorTest {

    @Autowired
    private TrackJpaRepository trackJpaRepository;
    @Autowired
    private TracksProcessor tracksProcessor;



    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldListUnassignedTracksProperly() {
        Track trackWithoutAlbum = new Track();
        trackWithoutAlbum.setName("Track Without Album");
        trackWithoutAlbum.setUrl(UUID.randomUUID().toString());
        trackJpaRepository.save(trackWithoutAlbum);
        StepVerifier.create(tracksProcessor.listTracks(null)).expectNextCount(1).verifyComplete();
    }

    @After
    public void tearDown() throws Exception {
        trackJpaRepository.deleteAll();
    }
}