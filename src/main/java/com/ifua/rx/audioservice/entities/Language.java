package com.ifua.rx.audioservice.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "Language")
public @Data class Language {
    @Id
    @GeneratedValue
    @Column(name = "id")
    @Getter @Setter private Integer id;

    @Column(name = "name")
    @Getter @Setter private String name;

    @Column(name = "shortcut")
    @Getter @Setter private String shortcut;
}
