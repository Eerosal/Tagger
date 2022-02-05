package fi.eerosalla.web.tagger.controller.tags;

import fi.eerosalla.web.tagger.model.form.TagNamesForm;
import fi.eerosalla.web.tagger.repository.tag.TagEntry;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TagsController {

    @Autowired
    private TagRepository tagRepository;

    @SneakyThrows
    @PostMapping("/api/tags/get-or-create")
    public Object getOrCreateTags(
        final @RequestBody @Validated TagNamesForm tagNamesForm) {

        final var queryBuilder = tagRepository.getHandle()
            .queryBuilder();

        queryBuilder.where()
            .in("name", tagNamesForm.getTagNames());

        List<TagEntry> tags = new ArrayList<>(queryBuilder.query());
        if (tags.size() == tagNamesForm.getTagNames().size()) {
            return tags;
        }

        HashSet<String> tagsToCreate =
            new HashSet<>(tagNamesForm.getTagNames());
        for (TagEntry foundTag : tags) {
            tagsToCreate.remove(foundTag.getName());
        }

        List<TagEntry> newTags = tagsToCreate.stream().map(name -> {
            TagEntry newTag = new TagEntry();

            newTag.setName(name);

            return newTag;
        }).collect(Collectors.toList());
        tags.addAll(newTags);

        for (TagEntry newTag : newTags) {
            tagRepository.create(newTag);
        }

        return tags;
    }

}
