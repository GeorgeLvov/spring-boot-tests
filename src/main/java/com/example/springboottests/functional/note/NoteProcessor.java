package com.example.springboottests.functional.note;

import com.example.springboottests.functional.net.GeneralSender;
import com.example.springboottests.functional.note.exception.XmlConverterException;
import com.example.springboottests.functional.note.model.Note;
import com.example.springboottests.functional.note.model.NoteInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

/**
 * Service component for processing notes.
 *
 * @author Georgii Lvov
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoteProcessor {

    private final XmlConverter xmlConverter;
    private final GeneralSender generalSender;

    @Value("${url.note-service}")
    private String noteServiceUrl;

    /**
     * Processes an incoming note XML message.
     * This method converts the XML message to a Note object, creates a NoteInfo object
     * based on the note, and sends the NoteInfo object to a specified service.
     *
     * @param noteXml The XML message representing the note.
     * @throws XmlConverterException if there is an error converting the XML message to a Note object.
     */
    public void process(String noteXml) {
        Note note = xmlConverter.toObject(noteXml, Note.class);

        NoteInfo noteInfo = new NoteInfo(note.noteId(), "PROCESSED");

        generalSender.send(
                noteServiceUrl,
                HttpMethod.POST,
                noteInfo,
                httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_JSON),
                Void.class
        );

        log.info("Note to Note-Service was sent successfully");
    }
}
