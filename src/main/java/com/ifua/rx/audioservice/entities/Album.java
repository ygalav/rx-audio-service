package com.ifua.rx.audioservice.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity
@NamedEntityGraph(name = "graph.Album",
        attributeNodes = {
            @NamedAttributeNode(value = "albums", subgraph = "albums"),
            @NamedAttributeNode(value = "tracks", subgraph = "tracks")
        },
        subgraphs = {
            @NamedSubgraph(name = "albums", attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("name")}),
            @NamedSubgraph(name = "tracks", attributeNodes = {@NamedAttributeNode("id"), @NamedAttributeNode("name")})
        })
@EqualsAndHashCode
public @Data @ToString class Album {

    @Id
    @GeneratedValue
    @Getter @Setter private Integer id;

    @Getter @Setter @EqualsAndHashCode.Exclude private String name;

    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude @Getter @Setter @EqualsAndHashCode.Exclude private Set<Track> tracks;

    @ManyToMany(fetch = FetchType.LAZY)
    @ToString.Exclude @Getter @Setter @EqualsAndHashCode.Exclude private Set<Album> albums;

    @ManyToOne
    @ToString.Exclude @Getter @Setter @EqualsAndHashCode.Exclude private Language language;

    @Column(nullable=false, columnDefinition="boolean default false")
    @ToString.Exclude @Getter @Setter @EqualsAndHashCode.Exclude private boolean topLevelAlbum;
}
