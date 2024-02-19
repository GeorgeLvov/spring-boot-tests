package com.example.springboottests.functional.note.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "note")
public record Note(String noteId) {
}
