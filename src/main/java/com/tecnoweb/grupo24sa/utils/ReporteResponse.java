package com.tecnoweb.grupo24sa.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReporteResponse {
    private String textoRespuesta;
    private List<File> archivosAdjuntos;

    public ReporteResponse(String textoRespuesta) {
        this.textoRespuesta = textoRespuesta;
        this.archivosAdjuntos = new ArrayList<>();
    }

    public ReporteResponse(String textoRespuesta, File archivoAdjunto) {
        this.textoRespuesta = textoRespuesta;
        this.archivosAdjuntos = new ArrayList<>();
        if (archivoAdjunto != null) this.archivosAdjuntos.add(archivoAdjunto);
    }

    public ReporteResponse(String textoRespuesta, List<File> archivosAdjuntos) {
        this.textoRespuesta = textoRespuesta;
        this.archivosAdjuntos = archivosAdjuntos != null ? archivosAdjuntos : new ArrayList<>();
    }

    public String getTextoRespuesta() {
        return textoRespuesta;
    }

    public void setTextoRespuesta(String textoRespuesta) {
        this.textoRespuesta = textoRespuesta;
    }

    public List<File> getArchivosAdjuntos() {
        return archivosAdjuntos;
    }

    public void setArchivosAdjuntos(List<File> archivosAdjuntos) {
        this.archivosAdjuntos = archivosAdjuntos;
    }

    public void addArchivoAdjunto(File archivo) {
        if (archivo != null) this.archivosAdjuntos.add(archivo);
    }

    public boolean tieneAdjuntos() {
        return archivosAdjuntos != null && !archivosAdjuntos.isEmpty();
    }
}
