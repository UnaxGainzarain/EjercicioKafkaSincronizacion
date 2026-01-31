package org.cuatrovientos.impresoras;

import com.fasterxml.jackson.annotation.JsonProperty; // <--- NECESARIO PARA QUE NO DE ERROR

public class Document {
    
    // Ponemos las propiedades privadas (Mejor práctica)
    private String sender;
    private String title;
    private String content;
    private ModoImpresion modo; 

    // Definimos el Enum
    public enum ModoImpresion {
        @JsonProperty("Color")
        COLOR,
        
        @JsonProperty("B/N")   
        BLANCO_NEGRO           
    }

    
    public Document() {
    }

    public Document(String sender, String title, String content, ModoImpresion modo) {
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.modo = modo;
    }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ModoImpresion getModo() { return modo; }
    public void setModo(ModoImpresion modo) { this.modo = modo; }
    
}