package fi.eerosalla.web.tagger.controller.files;

import fi.eerosalla.web.tagger.model.response.ErrorResponse;
import fi.eerosalla.web.tagger.repository.file.FileEntry;
import fi.eerosalla.web.tagger.repository.file.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice(assignableTypes = {FileController.class})
public class FileControllerAdvice {

    private static class FileEntryNotFoundException extends Exception {
    }

    @Autowired
    private FileRepository fileRepository;

    @ModelAttribute
    public void injectFile(
        final @PathVariable Map<String, String> pathVariables,
        final Model model) throws FileEntryNotFoundException {
        String fileIdStr = pathVariables.get("fileId");
        if (fileIdStr != null) {
            int fileId = Integer.parseInt(fileIdStr);

            FileEntry file = fileRepository.queryForId(fileId);

            if (file == null) {
                throw new FileEntryNotFoundException();
            }

            model.addAttribute(file);
        }
    }

    @ResponseBody
    @ExceptionHandler(FileEntryNotFoundException.class)
    public ResponseEntity<?> handleException() {
        return new ResponseEntity<>(
            new ErrorResponse("File not found"),
            HttpStatus.NOT_FOUND
        );
    }
}
