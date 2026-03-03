package com.pictet.backend_task.repository.model;

import com.pictet.backend_task.repository.entity.Section;

import java.util.List;

public record CurrentSectionInfo(
    Integer sectionId,
    String text,
    SectionType type,
    List<Section.Option> options,
    Consequence consequence
) {}

