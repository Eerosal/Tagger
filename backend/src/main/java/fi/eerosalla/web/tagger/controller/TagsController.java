package fi.eerosalla.web.tagger.controller;

import fi.eerosalla.web.tagger.model.form.TagNamesForm;
import fi.eerosalla.web.tagger.repository.tag.TagEntry;
import fi.eerosalla.web.tagger.repository.tag.TagRepository;
import lombok.SneakyThrows;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RolesAllowed("ADMIN")
@RestController
public class TagsController {

    private static final Pattern TAG_NAME_PATTERN =
        Pattern.compile("^[a-z_]+$");

    private final TagRepository tagRepository;

    public TagsController(final TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @SneakyThrows
    @PostMapping("/api/tags/get-or-create")
    public Object getOrCreateTags(
        final @RequestBody @Validated TagNamesForm tagNamesForm) {

        HashSet<String> tagNames = tagNamesForm.getTagNames().stream()
            .filter(tagName -> !tagName.isEmpty())
            .map(String::toLowerCase)
            .filter(tagName -> TAG_NAME_PATTERN.matcher(tagName).matches())
            .collect(Collectors.toCollection(HashSet::new));

        if (tagNames.isEmpty()) {
            return new ArrayList<>();
        }

        final var queryBuilder = tagRepository.getHandle()
            .queryBuilder();

        queryBuilder.where()
            .in("name", tagNames);

        HashSet<String> newTagNames = new HashSet<>(tagNames);

        List<TagEntry> foundTags = queryBuilder.query();
        for (TagEntry foundTag : foundTags) {
            newTagNames.remove(foundTag.getName());
        }

        if (newTagNames.isEmpty()) {
            return foundTags;
        }

        List<TagEntry> resultTags = new ArrayList<>();

        for (String newTagName : newTagNames) {
            TagEntry newTag = new TagEntry();
            newTag.setName(newTagName);

            tagRepository.create(newTag);

            resultTags.add(newTag);
        }

        resultTags.addAll(foundTags);

        return resultTags;
    }

}
