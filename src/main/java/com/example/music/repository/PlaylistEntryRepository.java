package com.example.music.repository;

import com.example.music.entity.PlaylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaylistEntryRepository extends JpaRepository<PlaylistEntry, UUID> {
}
