package com.bbuhha.course_project_microservice.rest;

import com.bbuhha.course_project_microservice.dto.NoteDto;
import com.bbuhha.course_project_microservice.exceptionHandling.NoSuchException;
import com.bbuhha.course_project_microservice.model.Note;
import com.bbuhha.course_project_microservice.model.Person;
import com.bbuhha.course_project_microservice.service.NoteService;
import com.bbuhha.course_project_microservice.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notes/")
public class NoteRestController {

    private final PersonService personService;
    private final NoteService noteService;

    @Autowired
    public NoteRestController(PersonService personService, NoteService noteService) {
        this.personService = personService;
        this.noteService = noteService;
    }


    @GetMapping("/note")
    public ResponseEntity getAllNodes(Principal principal) {
        Person owner =  personService.findByUsername(principal.getName());
        List<NoteDto> result = noteService.findAll(
                owner.getId()).stream()
                              .map(x -> NoteDto.fromNote(x))
                              .collect(Collectors.toList()
                              );

        return ResponseEntity.ok(result);
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity getNodeById(Principal principal, @PathVariable Long noteId) {
        Person owner =  personService.findByUsername(principal.getName());
        Note result = noteService.findNoteByOwnerIdAndId(owner.getId(), noteId);
        return ResponseEntity.ok(NoteDto.fromNote(result));
    }


    @PostMapping("/note")
    public ResponseEntity createNote(Principal principal, @Valid @RequestBody NoteDto noteDto) {
        Person owner =  personService.findByUsername(principal.getName());
        Note newNote = noteDto.toNote();
        newNote.setOwner(owner);
        noteService.save(newNote);
        return ResponseEntity.ok(NoteDto.fromNote(newNote));
    }

    @PutMapping("/note/{noteId}")
    public ResponseEntity updateNote(Principal principal, @PathVariable Long noteId , @Valid @RequestBody NoteDto noteDto) {
        Person owner =  personService.findByUsername(principal.getName());
        noteService.update(owner.getId(), noteId, noteDto.toNote());
        return ResponseEntity.ok("Note updated");
    }

    @DeleteMapping("/note/{noteId}")
    public ResponseEntity deleteNote(Principal principal, @PathVariable Long noteId) {
        Person owner =  personService.findByUsername(principal.getName());
        noteService.deleteNoteByOwnerIdAndId(owner.getId(), noteId);
        return ResponseEntity.ok("Note deleted");
    }
}
