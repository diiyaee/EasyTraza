/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cat.copernic.backend.service;

import java.io.File;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 *
 * @author diyae
 */
@Service
public class OcrService {

    @Value("${tesseract.data}")
    private String tessDataPath;

    public String llegirText(File file) throws Exception {

        Tesseract tesseract = new Tesseract();

        tesseract.setDatapath(tessDataPath);

        tesseract.setLanguage("spa");

        return tesseract.doOCR(file);
    }
}