package com.ifua.rx.audioservice.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@EqualsAndHashCode
public @Data class Track {
    @Id
    @GeneratedValue
    @Getter @Setter private Integer id;
    @Getter @Setter @EqualsAndHashCode.Exclude private String name;
    @Getter @Setter @EqualsAndHashCode.Exclude private String url;
    @ManyToOne
    @Getter @Setter @EqualsAndHashCode.Exclude private Album album;

}
